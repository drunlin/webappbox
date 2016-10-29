package com.github.drunlin.webappbox.fragment

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.github.drunlin.webappbox.BR
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.common.asyncCall
import com.github.drunlin.webappbox.data.Unique
import com.github.drunlin.webappbox.model.DataManager
import com.github.drunlin.webappbox.widget.adapter.ListAdapter
import kotlinx.android.synthetic.main.editable_list_content.*
import kotlinx.android.synthetic.main.list_content.*
import java.util.*

abstract class ListFragment<T: Unique, M : DataManager<T>> :
        SecondaryFragment(), ActionMode.Callback {

    abstract protected val manager: M

    override val contentViewResId = R.layout.editable_list_content
    abstract protected val itemResId: Int

    protected val adapter by lazy { Adapter().apply { asyncCall({ manager.data }) { list = it } } }

    protected var actionModel: ActionMode? = null
    protected var selectedSet: HashSet<Long>? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        registerListeners()
    }

    open protected fun registerListeners() {
        manager.onInsert.add(this) { adapter.notifyItemInserted(it) }
        manager.onRemove.add(this) { adapter.notifyDataSetChanged() }
        manager.onUpdate.add(this) { adapter.notifyItemChanged(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        fab.setOnClickListener { onInsert() }
    }

    abstract fun onInsert()

    open protected fun ViewHolder.onItemCreated() = Unit

    abstract protected fun ViewHolder.onItemClick()

    abstract protected fun ViewHolder.onBindItem(data: T)

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        actionModel = mode
        selectedSet = HashSet()

        mode.menuInflater.inflate(R.menu.list_action_mode, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        onRemove()
        mode.finish()
        return true
    }

    open fun onRemove() {
        manager.remove(selectedSet!!)
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        actionModel = null
        selectedSet = null

        adapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterListeners()
    }

    open protected fun unregisterListeners() {
        manager.onInsert.add(this) { adapter.notifyItemInserted(it) }
        manager.onRemove.add(this) { adapter.notifyDataSetChanged() }
        manager.onUpdate.add(this) { adapter.notifyItemChanged(it) }
    }

    protected inner class Adapter : ListAdapter<T, ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, type: Int) : ListFragment<T, M>.ViewHolder {
            return ViewHolder(DataBindingUtil
                    .inflate<ViewDataBinding>(activity.layoutInflater, itemResId, parent, false))
        }
    }

    protected inner class ViewHolder(val binding: ViewDataBinding) :
            ListAdapter.ViewHolder<T>(binding.root) {

        init {
            onItemCreated()

            itemView.setOnClickListener {
                actionModel?.run { setSelected(!itemView.isSelected) } ?: onItemClick()
            }
            itemView.setOnLongClickListener {
                actionModel?.run { finish() } ?: run {
                    activity.startActionMode(this@ListFragment)
                    setSelected(true)
                }
                return@setOnLongClickListener true
            }
        }

        private fun setSelected(selected: Boolean) {
            if (selected) selectedSet?.add(data!!.id) else selectedSet?.remove(data!!.id)
            itemView.isSelected = selected
        }

        override fun onBind(data: T) {
            onBindItem(data)
            binding.setVariable(BR.selected, selectedSet?.contains(data.id) ?: false)
            binding.executePendingBindings()
        }
    }
}
