package com.rocketzly.lintplugin

import com.rocketzly.lintplugin.analyze.AnalyzeHelper
import com.rocketzly.lintplugin.dependency.DependencyHelper
import com.rocketzly.lintplugin.extension.ExtensionHelper
import com.rocketzly.lintplugin.task.LintTaskHelper
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * User: Rocket
 * Date: 2020/8/13
 * Time: 3:30 PM
 */
class LintPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        DependencyHelper().apply(project)
        ExtensionHelper().apply(project)
        //分析器，用来分析入参和结果
        AnalyzeHelper().apply(project)
        LintTaskHelper().apply(project)
    }
}