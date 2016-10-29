package com.github.drunlin.webappbox.module

import com.github.drunlin.webappbox.activity.WebappActivity
import com.github.drunlin.webappbox.activity.WebappEditorActivity
import com.github.drunlin.webappbox.fragment.*
import com.github.drunlin.webappbox.model.*
import dagger.Subcomponent
import javax.inject.Singleton

@Singleton
@Subcomponent(modules = arrayOf(WebappModule::class))
interface WebappComponent {
    val webappId: Long
    val webappModel: WebappModel
    val ruleManager: RuleManager
    val patternManager: PatternManager

    fun inject(activity: WebappActivity)
    fun inject(activity: WebappEditorActivity)
    fun inject(fragment: WebappEditorFragment)
    fun inject(fragment: PatternsFragment)
    fun inject(fragment: PatternEditorFragment)
    fun inject(fragment: RulesFragment)
    fun inject(fragment: RuleEditorFragment)
    fun inject(fragment: WebappWindowFragment)
    fun inject(fragment: PreviewFragment)
    fun inject(fragment: WebappFragment)
    fun inject(model: PersistentWebappModel): PersistentWebappModel
    fun inject(model: WebappModel): WebappModel
    fun inject(model: StoredWebappModel): StoredWebappModel
    fun inject(model: PersistentPatternManager): PersistentPatternManager
    fun inject(model: PatternManager): PatternManager
    fun inject(model: RuleManager): RuleManager
    fun inject(model: PersistentRuleManager): PersistentRuleManager
}
