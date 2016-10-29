package com.github.drunlin.webappbox.widget.databinding

import android.databinding.BindingAdapter
import android.databinding.BindingMethod
import android.databinding.BindingMethods
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView

@BindingMethods(
        BindingMethod(type = ImageView::class, attribute = "android:src", method = "setImageBitmap")
)
class MethodBinding

@BindingAdapter("app:src")
fun <T> setImage(view: ImageView, value: T) {
    val bitmap = when (value) {
        is Bitmap -> value
        is Int -> BitmapFactory.decodeResource(view.resources, value)
        else -> throw UnsupportedOperationException()
    }
    view.setImageBitmap(bitmap)
}

@BindingAdapter("app:selected")
fun setSelected(view: View, selected: Boolean) {
    view.isSelected = selected
}
