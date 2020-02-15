package com.github.drunlin.webappbox.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import com.github.drunlin.webappbox.BR
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.common.app
import com.github.drunlin.webappbox.common.asyncCall
import com.github.drunlin.webappbox.data.Shortcut
import com.github.drunlin.webappbox.databinding.ItemAppBinding
import com.github.drunlin.webappbox.model.WebappManager
import com.github.drunlin.webappbox.widget.adapter.ListAdapter
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView
import kotlinx.android.synthetic.main.list_content.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AppsFragment : Fragment() {
    @Inject lateinit var webappManager: WebappManager

    private val adapter by lazy {
        Adapter().apply { asyncCall({ webappManager.shortcuts }) { list = it } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app.component.inject(this)

        webappManager.onInsert.add(this) { adapter.notifyItemInserted(it) }
        webappManager.onRemove.add(this) { adapter.notifyItemRemoved(it) }
        webappManager.onUpdate.add(this) { adapter.notifyItemChanged(it) }

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.list_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager =
            GridLayoutManager(context, resources.getInteger(R.integer.launcher_column_count))
        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_apps, menu)

        val searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        RxSearchView.queryTextChanges(searchView)
            .skip(1)
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.io())
            .map { it.toString() }
            .map { webappManager.filter(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { adapter.list = it }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }

    override fun onDestroy() {
        super.onDestroy()

        webappManager.onInsert.remove(this)
        webappManager.onRemove.remove(this)
        webappManager.onUpdate.remove(this)
    }

    private inner class Adapter : ListAdapter<Shortcut, ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppsFragment.ViewHolder {
            return ViewHolder(ItemAppBinding.inflate(layoutInflater, parent, false))
        }
    }

    private inner class ViewHolder(val binding: ItemAppBinding) :
        ListAdapter.ViewHolder<Shortcut>(binding.root) {

        override fun onBind(data: Shortcut) {
            binding.setVariable(BR.shortcut, data)
            (activity as OnShortcutCreated).onShortcutCreated(itemView, data)
        }
    }
}

interface OnShortcutCreated {
    fun onShortcutCreated(itemView: View, shortcut: Shortcut)
}
