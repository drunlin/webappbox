package com.github.drunlin.webappbox.module

import android.content.Context
import com.github.drunlin.webappbox.model.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(val context: Context) {
    @Singleton
    @Provides
    fun provideContext() = context

    @Singleton
    @Provides
    fun provideWebappManager(comp: AppComponent) = comp.inject(WebappManager())

    @Singleton
    @Provides
    fun providePreferenceModel(comp: AppComponent) = comp.inject(PreferenceModel()).apply { init() }

    @Singleton
    @Provides
    fun provideUserAgentManager(comp: AppComponent) = comp.inject(UserAgentManager())

    @Singleton
    @Provides
    fun provideDbHelper(comp: AppComponent) = comp.inject(DatabaseManager(context))

    @Provides
    fun provideIconLoader(comp: AppComponent) = comp.inject(IconLoader())
}
