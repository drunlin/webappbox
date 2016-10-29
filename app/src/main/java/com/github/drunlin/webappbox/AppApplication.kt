package com.github.drunlin.webappbox

import android.app.Application
import com.github.drunlin.webappbox.module.*
import com.github.drunlin.webappbox.module.WebappModule.Flag
import com.github.drunlin.webappbox.module.WebappModule.Flag.NORMAL

class AppApplication : Application() {
    lateinit var component: AppComponent
        private set

    private val webappComponents = Factory<String, WebappComponent>()

    override fun onCreate() {
        super.onCreate()

        component = DaggerAppComponent.builder().appModule(AppModule(this)).build()
    }

    fun webappComponent(id: Long, flag: Flag = NORMAL) = webappComponents.get("$id-$flag") {
        component.webappComponent(WebappModule(id, flag))
    }
}
