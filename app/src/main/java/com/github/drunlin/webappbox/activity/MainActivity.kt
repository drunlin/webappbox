package com.github.drunlin.webappbox.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import com.github.drunlin.webappbox.BR
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.common.app
import com.github.drunlin.webappbox.common.asyncCall
import com.github.drunlin.webappbox.data.Shortcut
import com.github.drunlin.webappbox.databinding.ItemMainBinding
import com.github.drunlin.webappbox.model.WebappManager
import com.github.drunlin.webappbox.widget.adapter.ListAdapter
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView
import kotlinx.android.synthetic.main.list_content.*
import kotlinx.android.synthetic.main.toolbar.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : TranslucentStatusActivity() {
    @Inject lateinit var webappManager: WebappManager

    private val adapter by lazy {
        Adapter().apply { asyncCall({ webappManager.shortcuts }) { list = it } }
    }

    private var selectionId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app.component.inject(this)

        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.apps)

        webappManager.onInsert.add(this) { adapter.notifyItemInserted(it) }
        webappManager.onRemove.add(this) { adapter.notifyItemRemoved(it) }
        webappManager.onUpdate.add(this) { adapter.notifyItemChanged(it) }

        recyclerView.layoutManager =
                GridLayoutManager(this, resources.getInteger(R.integer.launcher_column_count))
        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)

        val searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        RxSearchView.queryTextChanges(searchView)
                .skip(1)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .map { it.toString() }
                .map { webappManager.filter(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { adapter.list = it }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_new -> startActivity(WebappEditorActivity.new())
            R.id.menu_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.menu_about -> startActivity(Intent(this, AboutActivity::class.java))
        }
        return true
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, info: ContextMenu.ContextMenuInfo?) {
        menuInflater.inflate(R.menu.item_webapp, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_edit -> startActivity(WebappEditorActivity.edit(selectionId))
            R.id.menu_delete -> webappManager.delete(selectionId)
            R.id.menu_add_shortcut -> webappManager.installShortcut(selectionId)
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()

        webappManager.onInsert.remove(this)
        webappManager.onRemove.remove(this)
        webappManager.onUpdate.remove(this)
    }

    private inner class Adapter : ListAdapter<Shortcut, ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainActivity.ViewHolder {
            return ViewHolder(ItemMainBinding.inflate(layoutInflater, parent, false))
        }
    }

    private inner class ViewHolder(val binding: ItemMainBinding) :
            ListAdapter.ViewHolder<Shortcut>(binding.root) {

        init {
            itemView.setOnClickListener { startActivity(WebappActivity.start(data!!.uuid)) }
            itemView.setOnCreateContextMenuListener(this@MainActivity)
            itemView.setOnLongClickListener {
                selectionId = data!!.id
                itemView.showContextMenu()
                return@setOnLongClickListener true
            }
        }

        override fun onBind(data: Shortcut) {
            binding.setVariable(BR.shortcut, data)
        }
    }
}
