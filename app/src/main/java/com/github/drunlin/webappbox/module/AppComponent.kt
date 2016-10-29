package com.github.drunlin.webappbox.module

import com.github.drunlin.webappbox.activity.LauncherActivity
import com.github.drunlin.webappbox.activity.MainActivity
import com.github.drunlin.webappbox.activity.WebappActivity
import com.github.drunlin.webappbox.fragment.IconLoaderFragment
import com.github.drunlin.webappbox.fragment.PreferencesFragment
import com.github.drunlin.webappbox.fragment.UserAgentEditorFragment
import com.github.drunlin.webappbox.fragment.UserAgentsFragment
import com.github.drunlin.webappbox.model.*
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    fun inject(model: DatabaseManager): DatabaseManager
    fun inject(manager: WebappManager): WebappManager
    fun inject(activity: MainActivity)
    fun inject(activity: LauncherActivity)
    fun inject(activity: WebappActivity)
    fun inject(model: PreferenceModel): PreferenceModel
    fun inject(fragment: PreferencesFragment)
    fun inject(manager: UserAgentManager): UserAgentManager
    fun inject(fragment: UserAgentEditorFragment)
    fun inject(fragment: UserAgentsFragment)
    fun inject(model: IconLoader): IconLoader
    fun inject(fragment: IconLoaderFragment)

    fun webappComponent(module: WebappModule): WebappComponent
}
