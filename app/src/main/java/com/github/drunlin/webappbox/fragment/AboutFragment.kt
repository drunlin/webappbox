package com.github.drunlin.webappbox.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.common.getSystemService
import com.github.drunlin.webappbox.common.safeStartActivity
import com.github.drunlin.webappbox.common.startWebBrowser
import kotlinx.android.synthetic.main.fragment_about.*

class AboutFragment : SecondaryFragment() {
    override val titleResId = R.string.about
    override val contentViewResId = R.layout.fragment_about

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        DataBindingUtil.bind<ViewDataBinding>(contentView)

        githubItem.setOnClickListener {
            activity.startWebBrowser(getString(R.string.project_homepage))
        }

        developerItem.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
                    .putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.author_email)))
                    .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            activity.safeStartActivity(intent)
        }

        licensesItem.setOnClickListener { activity.replaceContentFragment(LicensesFragment()) }

        manualItem.setOnClickListener { activity.replaceContentFragment(ManualFragment()) }

        donationItem.setOnClickListener {
            Snackbar.make(view, R.string.copy_to_clipboard, Snackbar.LENGTH_LONG)
                    .setAction(R.string.copy) { copyEmailAddress() }
                    .show()
        }
    }

    private fun copyEmailAddress() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.primaryClip = ClipData.newPlainText(null, getString(R.string.author_email))
    }
}
