package com.github.drunlin.webappbox.widget

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.widget.FrameLayout
import com.github.drunlin.webappbox.common.STATE_CHILDREN

open class ViewStateLayout(context: Context, attar: AttributeSet) : FrameLayout(context, attar) {
    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        if (id == NO_ID) {
            super.dispatchSaveInstanceState(container)
            return
        }

        val state = Bundle()
        val array = SparseArray<Parcelable>()
        state.putSparseParcelableArray(STATE_CHILDREN, array)
        container.put(id, state)

        getSaveFromParentEnabledChildren().forEach { it.saveHierarchyState(array) }
    }

    private fun getSaveFromParentEnabledChildren()
            = (0..childCount - 1).map { getChildAt(it) }.filter { it.isSaveFromParentEnabled }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        if (id == NO_ID) {
            super.dispatchRestoreInstanceState(container)
            return
        }

        val array = (container[id] as Bundle).getSparseParcelableArray<Parcelable>(STATE_CHILDREN)
        getSaveFromParentEnabledChildren().forEach { it.restoreHierarchyState(array) }
    }
}
