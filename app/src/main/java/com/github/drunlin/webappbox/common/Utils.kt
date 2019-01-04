package com.github.drunlin.webappbox.common

import android.app.Activity
import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.PNG
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.util.Patterns
import android.util.TypedValue
import android.webkit.URLUtil
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.github.drunlin.webappbox.AppApplication
import com.github.drunlin.webappbox.BuildConfig
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.ByteArrayOutputStream

private var autoIncrement: Long = 0

fun generateId() = ++autoIncrement

fun <T> asyncCall(asyncBlock: () -> T, block: (T) -> Unit) {
    Observable.fromCallable<T> { asyncBlock() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { block(it) }
}

fun runOnIoThread(action: () -> Unit) {
    Schedulers.io().createWorker().schedule(action)
}

fun <T> Iterable<T>.findNullableIndexedValue(predicate: (T) -> Boolean): IndexedValue<T?> {
    var index = -1
    val value = find { ++index; predicate(it) }
    return IndexedValue(value?.let { index } ?: -1, value)
}

fun <T> Iterable<T>.findIndexedValue(predicate: (T) -> Boolean): IndexedValue<T> {
    return withIndex().find { predicate(it.value) }!!
}

fun Intent.setClass(clazz: Class<*>) = setClassName(BuildConfig.APPLICATION_ID, clazz.name)!!

fun Intent.getLongExtra(name: String) = getLongExtra(name, -1)

fun Bitmap.toByteArray(): ByteArray {
    return ByteArrayOutputStream().apply { compress(PNG, 0, this) }.toByteArray()
}

fun ByteArray.toBitmap() = BitmapFactory.decodeByteArray(this, 0, size)!!

fun Context.getBitmap(id: Int) = BitmapFactory.decodeResource(resources, id)!!

fun Context.getDimension(unit: Int, value: Float): Float {
    return TypedValue.applyDimension(unit, value, resources.displayMetrics)
}

fun Context.getRawText(id: Int) = resources.openRawResource(id).reader().readText()

fun Context.getResourceId(attr: Int): Int {
    val array = obtainStyledAttributes(intArrayOf(attr))
    val resId = array.getResourceId(0, 0)
    array.recycle()
    return resId
}

val Context.iconSize: Int
    get() = (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).launcherLargeIconSize

val Activity.app: AppApplication get() = application as AppApplication

fun Activity.startWebBrowser(url: String) {
    safeStartActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}

fun Activity.safeStartActivity(intent: Intent) {
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        //do nothing
    }
}

fun <T : Fragment> FragmentManager.add(@IdRes viewId: Int, fragment: T) : T {
    beginTransaction().add(viewId, fragment).commit()
    return fragment
}

fun FragmentManager.remove(fragment: Fragment) {
    beginTransaction().remove(fragment).commit()
}

val Fragment.app: AppApplication get() = activity!!.application as AppApplication

fun Fragment.getSystemService(name: String): Any? = context!!.getSystemService(name)

var Fragment.friendFragment: Fragment
    set(value) {
        arguments ?: run { arguments = Bundle() }

        var depth = 0
        var fragment: Fragment? = value
        do {
            fragment!!.fragmentManager!!.putFragment(arguments!!, "FRIEND_FRAGMENT${depth++}", fragment)
            fragment = fragment.parentFragment
        } while (fragment != null)

        arguments!!.putInt("FRIEND_FRAGMENT_DEPTH", depth)
    }
    get() {
        var fragment: Fragment? = null
        val depth = arguments!!.getInt("FRIEND_FRAGMENT_DEPTH")
        (depth downTo 0).forEach {
            val fm = fragment?.childFragmentManager ?: activity!!.supportFragmentManager
            fragment = fm.getFragment(arguments!!, "FRIEND_FRAGMENT$it")
        }
        return fragment!!
    }

fun Fragment.showDialog(dialogFragment: DialogFragment) {
    dialogFragment.show(childFragmentManager)
}

fun DialogFragment.show(manager: FragmentManager) {
    show(manager, null)
}

val AlertDialog.positiveButton : Button get() = getButton(AlertDialog.BUTTON_POSITIVE)

val ImageView.bitmap: Bitmap get() = (drawable as BitmapDrawable).bitmap

val TextView.string: String get() = text.toString()

fun String.isValidUrl() = URLUtil.isValidUrl(this) && Patterns.WEB_URL.matcher(this).matches()
