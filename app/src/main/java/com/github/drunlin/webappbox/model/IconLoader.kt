package com.github.drunlin.webappbox.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF
import android.util.TypedValue
import com.github.drunlin.webappbox.common.getDimension
import com.github.drunlin.webappbox.common.iconSize
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class IconLoader {
    @Inject lateinit var context: Context

    @Volatile var canceled: Boolean = false
        private set

    fun cancel() {
        canceled = true
    }

    private fun checkCanceled() {
        if (canceled) throw RuntimeException()
    }

    fun load(url: String): Bitmap? = try {
        getIcon(getIconData(getIconUrl(url)))
    } catch (e: Exception) {
        null
    }

    private fun getIcon(data: ByteArray): Bitmap {
        checkCanceled()

        val opt = BitmapFactory.Options()
        opt.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(data, 0, data.size, opt)
        opt.inSampleSize = 2
        while (opt.outWidth / opt.inSampleSize > context.iconSize
                || opt.outHeight / opt.inSampleSize > context.iconSize) {
            opt.inSampleSize *= 2
        }
        opt.inSampleSize /= 2
        opt.inJustDecodeBounds = false

        val padding = context.getDimension(TypedValue.COMPLEX_UNIT_DIP, 5f)
        val bounderSize = context.getDimension(TypedValue.COMPLEX_UNIT_DIP, 48f)
        val iconSize = context.getDimension(TypedValue.COMPLEX_UNIT_DIP, 38f)
        val src = BitmapFactory.decodeByteArray(data, 0, data.size, opt)
        val rect = RectF(padding, padding, padding + iconSize, padding + iconSize)
        val dest = Bitmap.createBitmap(bounderSize.toInt(), bounderSize.toInt(), src.config)
        Canvas(dest).drawBitmap(src, null, rect, null)
        return dest
    }

    private fun getIconData(url: URL): ByteArray {
        checkCanceled()

        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 3000
        connection.readTimeout = 7000
        connection.connect()
        return connection.inputStream.readBytes()
    }

    private fun getIconUrl(url: String): URL {
        checkCanceled()

        val ua =  "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) " +
                "AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1"
        val document = Jsoup.connect(url).userAgent(ua).timeout(10000).get()
        val selectors = "link[rel='icon'], link[rel='apple-touch-icon'], " +
                "link[rel='apple-touch-icon-precomposed'], link[rel='shortcut icon']"
        val urlContext = URL(url)
        val icons = document.select(selectors)
                .map { parse(urlContext, it) }
                .filter { it.url.openConnection().contentType == "image/png" }
                .sortedBy { it.order }
        return (icons.find { it.size >= context.iconSize } ?: icons.last()).url
    }

    private fun parse(context: URL, element: Element): Icon {
        val sizes = element.attr("sizes")
        val size = if (sizes.isEmpty()) 0 else sizes.split('x').map(String::toInt).max()!!
        val priority = when (element.attr("rel")) {
            "icon" -> 4
            "apple-touch-icon-precomposed" -> 3
            "apple-touch-icon" -> 2
            "shortcut icon" -> 1
            else -> 0
        }
        return Icon(URL(context, element.attr("href")), size, size * 10 + priority)
    }

    private class Icon(val url: URL, val size: Int, val order: Int)
}
