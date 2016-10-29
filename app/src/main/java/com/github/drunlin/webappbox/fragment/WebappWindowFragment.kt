package com.github.drunlin.webappbox.fragment

import android.Manifest
import android.annotation.TargetApi
import android.app.ActivityManager
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnKeyListener
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.webkit.*
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.activity.FragmentActivity
import com.github.drunlin.webappbox.common.*
import com.github.drunlin.webappbox.data.LaunchMode
import com.github.drunlin.webappbox.data.Orientation
import com.github.drunlin.webappbox.data.Policy
import com.github.drunlin.webappbox.data.Rule
import com.github.drunlin.webappbox.model.PatternManager
import com.github.drunlin.webappbox.model.PreferenceModel
import com.github.drunlin.webappbox.model.RuleManager
import com.github.drunlin.webappbox.model.WebappModel
import kotlinx.android.synthetic.main.fragment_webapp_window.*
import kotlinx.android.synthetic.main.fragment_webapp_window.view.*
import kotlinx.android.synthetic.main.status_bar.*
import java.io.File
import javax.inject.Inject

class WebappWindowFragment() : Fragment(), DownloadListener, OnKeyListener, OnGlobalLayoutListener {
    @Inject lateinit var webappModel: WebappModel
    @Inject lateinit var preferenceModel: PreferenceModel
    @Inject lateinit var ruleManager: RuleManager
    @Inject lateinit var patternManager: PatternManager

    var rule: Rule? = null
        private set
    var currentUrl: String? = null
        private set

    private val activity: FragmentActivity get() = getActivity() as FragmentActivity
    private val manager: WebappFragment get() =  parentFragment as WebappFragment

    private var pendingUrl: String? = null

    private var uploadFileCallback: ValueCallback<Uri>? = null
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    private var pendingDownloadInfo: DownloadInfo? = null

    private var pendingGeolocationPermissionsPrompt: GeolocationPermissionsPrompt? = null

    private var contentView: View? = null
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null

    private var softKeyboardVisible = false

    init {
        retainInstance = true
    }

    constructor(url: String) : this() {
        pendingUrl = url
    }

    fun loadUrl(url: String) {
        webview?.loadUrl(url) ?: run { pendingUrl = url }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (context as WebappContext).component.inject(this)

        ruleManager.onInsert.add(this) { onRulesChange() }
        ruleManager.onRemove.add(this) { onRulesChange() }
        ruleManager.onUpdate.add(this) { onRulesChange() }
        ruleManager.onMove.add(this) { f, t -> onRulesChange() }
        preferenceModel.onChange.add(this) { onRulesChange() }
    }

    private fun onRulesChange() {
        if (isResumed && !isHidden) updateSystemUi()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (contentView == null)
            contentView = inflater.inflate(R.layout.fragment_webapp_window, container, false)
        else
            (contentView?.parent as ViewGroup?)?.removeView(contentView)
        return contentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        statusBarBackground?.setBackgroundColor(Color.WHITE)

        refreshLayout.setOnRefreshListener { webview.reload() }

        webview.settings.setAppCachePath(File(context.cacheDir, "webapp").absolutePath)
        webview.settings.setAppCacheEnabled(true)
        webview.settings.databaseEnabled = true
        webview.settings.domStorageEnabled = true
        webview.settings.loadWithOverviewMode = true
        webview.settings.useWideViewPort = true

        webview.setWebViewClient(AppWebViewClient())
        webview.setWebChromeClient(AppWebChromeClient())
        webview.setDownloadListener(this)
        webview.setOnKeyListener(this)

        if (pendingUrl != null) {
            webview.loadUrl(pendingUrl)
            pendingUrl = null
        } else if (webview.url.isNullOrEmpty() && savedInstanceState != null) {
            webview.restoreState(savedInstanceState)
        }

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        if (savedInstanceState == null)
            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
        closeButton.setOnClickListener {
            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.action == KeyEvent.ACTION_UP) {
                if (rule?.launchMode == LaunchMode.STANDARD && webview.canGoBack())
                    webview.goBack()
                else
                    manager.goBack()
            }
            return true
        }
        return false
    }

    override fun onDownloadStart(url: String?, userAgent: String?, contentDisposition: String?,
                                 mimetype: String?, contentLength: Long) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            pendingDownloadInfo = DownloadInfo(url, contentDisposition, mimetype)
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_STORAGE)
        } else {
            downloadFile(url, contentDisposition, mimetype)
        }
    }

    private fun downloadFile(url: String?, contentDisposition: String?, mimetype: String?) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setTitle(contentDisposition)
        val name = URLUtil.guessFileName(url, contentDisposition, mimetype)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name)
        val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)
    }

    override fun onResume() {
        super.onResume()

        resume()
    }

    private fun resume() {
        activity.onWindowFocusChanged.add(this) { if (it) updateSystemUi() }
        activity.contentView.viewTreeObserver.addOnGlobalLayoutListener(this)

        updateSystemUi()

        webview.requestFocus()
        webview.onResume()
    }

    override fun onPause() {
        super.onPause()

        pause()
    }

    private fun pause() {
        activity.onWindowFocusChanged.remove(this)
        activity.contentView.viewTreeObserver.removeOnGlobalLayoutListener(this)

        webview.onPause()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (hidden) pause() else resume()
    }

    override fun onGlobalLayout() {
        if (activity.contentView.paddingBottom
                > context.getDimension(TypedValue.COMPLEX_UNIT_DIP, 72f)) {
            softKeyboardVisible = true
        } else if (softKeyboardVisible) {
            softKeyboardVisible = false

            updateSystemUi()
        }
    }

    private fun updateSystemUi() {
        currentUrl?.run { updateSystemUi(this) }
    }

    private fun updateSystemUi(url: String) {
        rule = ruleManager.getRule(url)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            statusBarBackground.setBackgroundColor(rule!!.color)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val webapp = webappModel.webapp
                activity.setTaskDescription(
                        ActivityManager.TaskDescription(webapp.name, webapp.icon, rule!!.color))
            }
        }

        activity.requestedOrientation = when (rule!!.orientation) {
            Orientation.NORMAL -> ActivityInfo.SCREEN_ORIENTATION_USER
            Orientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            Orientation.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        activity.window.decorView.systemUiVisibility = if (rule!!.fullScreen) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            else
                (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LOW_PROFILE)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            else
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

    private fun grantGeolocationPermissions() {
        pendingGeolocationPermissionsPrompt?.run { callback.invoke(origin, true, false) }
        pendingGeolocationPermissionsPrompt = null
    }

    private fun requestGeolocationPermissions() {
        if (grantOrRequestSystemGeolocationPermissions()) return

        Snackbar.make(refreshLayout, R.string.turn_on_location, Snackbar.LENGTH_LONG)
                .setAction(R.string.turn_on) {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(intent, REQUEST_LOCATION_SETTINGS)
                }.setCallback(object : Snackbar.Callback() {
                    override fun onDismissed(snackbar: Snackbar, event: Int) {
                        if (event != DISMISS_EVENT_ACTION) grantGeolocationPermissions()
                    }
                }).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_GET_CONTENT -> onGetContentResult(data, resultCode)
            REQUEST_LOCATION_SETTINGS -> onLocationSettingsResult()
        }
    }

    private fun onLocationSettingsResult() {
        if (!grantOrRequestSystemGeolocationPermissions()) grantGeolocationPermissions()
    }

    private fun onGetContentResult(data: Intent?, resultCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val result = WebChromeClient.FileChooserParams.parseResult(resultCode, data)
            filePathCallback?.onReceiveValue(result)
            filePathCallback = null
        } else {
            uploadFileCallback?.onReceiveValue(data?.data)
            uploadFileCallback = null
        }
    }

    private fun grantOrRequestSystemGeolocationPermissions(): Boolean {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                || lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission_group.LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                grantGeolocationPermissions()
            } else {
                val permissions = arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                requestPermissions(permissions, PERMISSIONS_REQUEST_LOCATION)
            }
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_LOCATION -> grantGeolocationPermissions()
            PERMISSIONS_REQUEST_STORAGE -> onRequestStoragePermissionsResult(grantResults)
        }
    }

    private fun onRequestStoragePermissionsResult(grantResults: IntArray) {
        if (grantResults.getOrNull(0) == PackageManager.PERMISSION_GRANTED) {
            pendingDownloadInfo?.run { downloadFile(url, contentDisposition, mimetype) }
            pendingDownloadInfo = null
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        webview?.saveState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        webview.setWebViewClient(null)
        webview.setWebChromeClient(null)
    }

    override fun onDestroy() {
        super.onDestroy()

        ruleManager.onInsert.remove(this)
        ruleManager.onRemove.remove(this)
        ruleManager.onUpdate.remove(this)
        ruleManager.onMove.remove(this)
        preferenceModel.onChange.remove(this)

        contentView!!.webview.loadUrl("about:blank")
        contentView = null
    }

    private data class DownloadInfo(val url: String?,
                                    val contentDisposition: String?,
                                    val mimetype: String?)

    private inner class AppWebViewClient : WebViewClient() {
        private var loading = false

        @Suppress("OverridingDeprecatedMember")
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (!URLUtil.isValidUrl(url)) {
                return true
            } else if (!patternManager.matches(url)) {
                activity.startWebBrowser(url)
                return true
            }
            return manager.onLoadUrl(url)
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            currentUrl = url

            updateSystemUi(url)

            view.settings.userAgentString = rule!!.userAgent.value
            view.settings.javaScriptEnabled = rule!!.jsEnabled

            loading = true
            view.postDelayed({ refreshLayout?.isRefreshing = loading }, 1000)

            manager.onUrlChange?.invoke(url)
        }

        private fun hideProgressBar() {
            loading = false
            refreshLayout?.isRefreshing = false
        }

        @TargetApi(Build.VERSION_CODES.M)
        override fun onPageCommitVisible(view: WebView, url: String) {
            hideProgressBar()
        }

        override fun onPageFinished(view: WebView, url: String) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) hideProgressBar()
        }
    }

    private data class GeolocationPermissionsPrompt(val origin: String?,
                                                    val callback: GeolocationPermissions.Callback)

    private inner class AppWebChromeClient : WebChromeClient() {
        override fun onGeolocationPermissionsShowPrompt(origin: String?,
                                                        callback: GeolocationPermissions.Callback) {
            when (webappModel.webapp.locationPolicy) {
                Policy.ASK -> showGeolocationPermissionsPrompt(origin, callback)
                Policy.ALLOW -> callback.invoke(origin, true, false)
                Policy.DENY -> callback.invoke(origin, false, false)
            }
        }

        private fun showGeolocationPermissionsPrompt(origin: String?,
                                                     callback: GeolocationPermissions.Callback) {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED

            denyButton.setOnClickListener {
                webappModel.setLocationPolicy(Policy.DENY)
                callback.invoke(origin, false, false)
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            }

            allowButton.setOnClickListener {
                webappModel.setLocationPolicy(Policy.ALLOW)
                pendingGeolocationPermissionsPrompt = GeolocationPermissionsPrompt(origin, callback)
                requestGeolocationPermissions()
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        override fun onGeolocationPermissionsHidePrompt() {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onShowFileChooser(webView: WebView,
                                       callback: ValueCallback<Array<Uri>>,
                                       fileChooserParams: FileChooserParams): Boolean {
            filePathCallback = callback

            startActivityForResult(fileChooserParams.createIntent(), REQUEST_GET_CONTENT)
            return true
        }

        //override system hidden api
        @Suppress("UNUSED")
        fun openFileChooser(callback: ValueCallback<Uri>, acceptType: String?,
                            @Suppress("UNUSED_PARAMETER") capture: String?) {
            uploadFileCallback = callback

            val intent = Intent(Intent.ACTION_GET_CONTENT)
                    .setType(acceptType)
                    .addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, REQUEST_GET_CONTENT)
        }
    }
}
