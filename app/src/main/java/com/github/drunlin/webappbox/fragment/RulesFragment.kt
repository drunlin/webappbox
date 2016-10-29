package com.github.drunlin.webappbox.fragment

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.MotionEvent
import android.view.View
import com.github.drunlin.webappbox.BR
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.data.Rule
import com.github.drunlin.webappbox.model.RuleManager
import kotlinx.android.synthetic.main.item_rule.view.*
import kotlinx.android.synthetic.main.list_content.*
import javax.inject.Inject

class RulesFragment : ListFragment<Rule, RuleManager>() {
    @Inject override lateinit var manager: RuleManager

    override val titleResId = R.string.rules
    override val itemResId = R.layout.item_rule

    private val itemDragHelper by lazy { ItemTouchHelper(ItemTouchHelperCallback()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (context as WebappContext).component.inject(this)
    }

    override fun registerListeners() {
        super.registerListeners()

        manager.onMove.add(this) { from, to -> adapter.notifyItemMoved(from, to) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemDragHelper.attachToRecyclerView(recyclerView)
    }

    override fun onInsert() {
        activity.replaceContentFragment(RuleEditorFragment())
    }

    override fun ListFragment<Rule, RuleManager>.ViewHolder.onItemCreated() {
        itemView.dragButton.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) itemDragHelper.startDrag(this)
            return@setOnTouchListener true
        }
    }

    override fun ListFragment<Rule, RuleManager>.ViewHolder.onItemClick() {
        activity.replaceContentFragment(RuleEditorFragment(data!!.id))
    }

    override fun ListFragment<Rule, RuleManager>.ViewHolder.onBindItem(data: Rule) {
        binding.setVariable(BR.rule, data)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        itemDragHelper.attachToRecyclerView(null)
    }

    override fun unregisterListeners() {
        super.unregisterListeners()

        manager.onMove.remove(this)
    }

    private inner class ItemTouchHelperCallback :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder): Boolean {
            manager.swap(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun isLongPressDragEnabled() = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) = Unit
    }
}
