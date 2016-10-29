package com.github.drunlin.webappbox.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.ViewDataBinding
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.github.drunlin.webappbox.BR
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.common.*
import com.github.drunlin.webappbox.data.Policy
import com.github.drunlin.webappbox.data.Webapp
import com.github.drunlin.webappbox.model.PatternManager
import com.github.drunlin.webappbox.model.RuleManager
import com.github.drunlin.webappbox.model.WebappManager
import com.github.drunlin.webappbox.model.WebappModel
import com.jakewharton.rxbinding.widget.RxTextView
import kotlinx.android.synthetic.main.fragment_webapp_editor.*
import kotlinx.android.synthetic.main.text_input.view.*
import rx.android.schedulers.AndroidSchedulers
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WebappEditorFragment(id: Long?) : EditorFragment<Webapp>(id),
        IconLoaderFragment.OnIconLoadedListener, IconChooserFragment.OnSelectedListener {

    @Inject lateinit var webappManager: WebappManager
    @Inject lateinit var webappModel: WebappModel
    @Inject lateinit var patternManager: PatternManager
    @Inject lateinit var ruleManager: RuleManager

    override val titleResId = id?.let { R.string.edit_webapp } ?: R.string.new_webapp
    override val contentViewResId = R.layout.fragment_webapp_editor

    override val data by lazy { id?.let { webappModel.webapp } }

    private var pendingImageUri: Uri? = null

    constructor() : this(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity as WebappContext).component.inject(this)

        pendingImageUri = savedInstanceState?.getParcelable(BUNDLE_IMAGE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webappModel.onUrlChange.add(this) { urlInput.edit.setText(it) }

        RxTextView.textChanges(urlInput.edit)
                .skip(1)
                .doOnNext { confirmMenu?.isEnabled = false }
                .map { it.trim().toString() }
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .map { onUrlChange(it) }
                .subscribe { confirmMenu?.isEnabled = it }

        savedInstanceState?.run { iconImage.setImageBitmap(getParcelable(BUNDLE_ICON)) }
        iconImage.setOnClickListener { showDialog(IconChooserFragment()) }

        patternsItem.setOnClickListener { activity.replaceContentFragment(PatternsFragment()) }

        rulesItem.setOnClickListener { activity.replaceContentFragment(RulesFragment()) }

        previewItem.setOnClickListener {
            synchronize()
            activity.replaceContentFragment(PreviewFragment())
        }
    }

    override fun onBindData(binding: ViewDataBinding, data: Webapp) {
        binding.setVariable(BR.webapp, data)
    }

    override fun onConfigureView(data: Webapp) {
        locationSpinner.value = data.locationPolicy.name
    }

    private fun synchronize() {
        webappModel.update(urlInput.edit.string, iconImage.bitmap, nameInput.edit.string,
                Policy.valueOf(locationSpinner.value))
    }

    private fun onUrlChange(url: String): Boolean {
        if (url.isEmpty()) {
            urlInput.layout.isErrorEnabled = false
        } else if (!url.isValidUrl()) {
            urlInput.layout.error = getString(R.string.invalid_url)
        } else if (url != webappModel.originalUrl && webappManager.isExisted(url)) {
            urlInput.layout.error = getString(R.string.exited_url)
        } else {
            urlInput.layout.isErrorEnabled = false
            return true
        }
        return false
    }

    override fun onSelected(which: Int) {
        when (which) {
            0 -> iconImage.setImageResource(R.mipmap.ic_webapp)
            1 -> loadIcon()
            2 -> pickPicture()
        }
    }

    private fun loadIcon() {
        if (urlInput.edit.string.isValidUrl())
            showDialog(IconLoaderFragment(urlInput.edit.string))
        else
            Snackbar.make(view!!, R.string.invalid_url, Toast.LENGTH_SHORT).show()
    }

    override fun onIconLoaded(icon: Bitmap?) {
        icon?.run { iconImage.setImageBitmap(this) }
                ?: Snackbar.make(view!!, R.string.download_failed, Snackbar.LENGTH_SHORT).show()
    }

    private fun pickPicture() {
        val uri = Uri.fromFile(File(context.externalCacheDir, "icon"))
        val intent = Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI)
                .setType("image/*")
                .putExtra("crop", "true")
                .putExtra("outputX", context.iconSize)
                .putExtra("outputY", context.iconSize)
                .putExtra("aspectX", 1)
                .putExtra("aspectY", 1)
                .putExtra("scale", true)
                .putExtra(MediaStore.EXTRA_OUTPUT, uri)
        val chooser = Intent.createChooser(intent, getText(R.string.pick_image))
        if (chooser.resolveActivity(context.packageManager) != null)
            startActivityForResult(chooser, REQUEST_PICK_PICTURE)
        else
            Snackbar.make(view!!, R.string.gallery_not_found, Snackbar.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_PICK_PICTURE) {
            try {
                iconImage.setImageBitmap(Media.getBitmap(activity.contentResolver, data!!.data))
            } catch (e: SecurityException) {
                pendingImageUri = data!!.data
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSIONS_REQUEST_STORAGE)
                }
            } catch (e: Exception) {
                //do nothing
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_STORAGE
                && grantResults.getOrNull(0) == PackageManager.PERMISSION_GRANTED) {
            try {
                iconImage.setImageBitmap(Media.getBitmap(activity.contentResolver, pendingImageUri))
            } catch (e: Exception) {
                //do nothing
            }
            pendingImageUri = null
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        if (outState == null) return

        iconImage?.run { outState.putParcelable(BUNDLE_ICON, bitmap) }
        pendingImageUri?.run { outState.putParcelable(BUNDLE_IMAGE, this) }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        webappModel.onUrlChange.remove(this)
    }

    override fun onCommit() {
        synchronize()

        val comp = (activity as WebappContext).component
        val addShortcut = shortcutItem.isChecked
        id?.run { webappManager.update(comp, addShortcut) } ?: webappManager.insert(comp, addShortcut)
    }
}
