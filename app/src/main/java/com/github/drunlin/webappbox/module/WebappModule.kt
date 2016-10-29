package com.github.drunlin.webappbox.module

import com.github.drunlin.webappbox.model.*
import com.github.drunlin.webappbox.module.WebappModule.Flag
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class WebappModule(private val id: Long, private val flag: Flag = Flag.NORMAL) {
    companion object {
        private val patterManagers = Factory<Long, PersistentPatternManager>()
        private val ruleManagers = Factory<Long, PersistentRuleManager>()
    }

    @Singleton
    @Provides
    fun provideWebappId() = id

    @Singleton
    @Provides
    fun provideWebappModel(comp: WebappComponent) = when (flag) {
        Flag.NORMAL -> comp.inject(PersistentWebappModel(id))
        Flag.NEW -> comp.inject(WebappModel(id))
        Flag.EDIT -> comp.inject(StoredWebappModel(id))
    }

    @Singleton
    @Provides
    fun providePatternManager(comp: WebappComponent) = if (flag == Flag.NEW)
        comp.inject(PatternManager())
    else
        comp.inject(patterManagers.get(id) { PersistentPatternManager(id) })

    @Singleton
    @Provides
    fun provideRuleManager(comp: WebappComponent) = if (flag == Flag.NEW)
        comp.inject(RuleManager())
    else
        comp.inject(ruleManagers.get(id) { PersistentRuleManager(id) })

    enum class Flag { NORMAL, NEW, EDIT }
}
