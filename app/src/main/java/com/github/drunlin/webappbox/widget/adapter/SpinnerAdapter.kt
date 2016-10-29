package com.github.drunlin.webappbox.widget.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import android.widget.Spinner
import com.github.drunlin.webappbox.R
import java.util.*

class SpinnerAdapter(context: Context, title: String,
                     data: MutableList<MutableMap<String, Any>> = LinkedList()) :
        SimpleAdapter(context, data, R.layout.clickable_item, arrayOf(TITLE, SUMMARY, SUMMARY),
                intArrayOf(R.id.titleText, R.id.summaryText, R.id.text)) {

    companion object {
        private val TITLE = "title"
        private val SUMMARY = "summary"
    }

    var title: String = title
        set(value) {
            field = value
            data.forEach { it[TITLE] = value }
            notifyDataSetChanged()
        }

    var data: MutableList<MutableMap<String, Any>> = data
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    init {
        setDropDownViewResource(R.layout.clickable_text)
    }

    constructor(context: Context, title: String, collection: Iterable<Any>) : this(context, title) {
        data = collection.map { item(it) }.toMutableList()
    }

    fun item(value: Any) = mutableMapOf(TITLE to title, SUMMARY to value)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = super.getView(position, convertView, parent)
        view.setOnClickListener { (view.parent as Spinner).performClick() }
        return view
    }
}
