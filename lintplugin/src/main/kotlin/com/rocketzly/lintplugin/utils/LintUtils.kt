package com.rocketzly.lintplugin.utils

import com.android.build.gradle.internal.VariantManager
import com.android.build.gradle.internal.scope.VariantScope
import com.android.build.gradle.tasks.LintBaseTask
import com.rocketzly.lintplugin.LintPlugin
import com.rocketzly.lintplugin.task.LintCreationAction.Companion.TASK_NAME_LINT_FULL
import com.rocketzly.lintplugin.task.LintCreationAction.Companion.TASK_NAME_LINT_INCREMENT
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileCollection

/**
 * Created by rocketzly on 2020/10/11.
 */
class LintUtils {
    companion object {
        fun checkTaskIsIncrementOrFullLint(task: Task, project: Project? = null) =
            (task.name == TASK_NAME_LINT_INCREMENT || task.name == TASK_NAME_LINT_FULL)
                    && (project == null || task.project == project)

        /**
         * 遍历rootProject获取agp版本
         */
        fun getAgpVersion(project: Project): String {
            return StaticMemberContainer.get(StaticMemberContainer.Key.AGP_VERSION) {
                val group = "com.android.tools.build"
                val name = "gradle"
                var version = ""

                for (dependency in project.rootProject.buildscript.configurations.getByName("classpath").dependencies) {
                    if (dependency.group != group || dependency.name != name || dependency.version.isNullOrEmpty()) continue
                    if (version.isEmpty()) version = dependency.version!!//空的直接赋值

                    //如果有多个agp获取最高版本
                    val originSplit = version.split(".")
                    val latestSplit = dependency.version!!.split(".")
                    for (i in 0 until Math.min(originSplit.size, latestSplit.size)) {//比较三位,从最高位开始比较
                        if (latestSplit[i] == originSplit[i]) continue//相同比较下一位
                        if (originSplit[i] > latestSplit[i]) {//origin大直接跳出
                            break
                        }
                        //latest大则用latest#version
                        version = dependency.version!!
                        break
                    }
                }
                version
            }
        }

        fun getAppPluginId(project: Project): String {
            return StaticMemberContainer.get(StaticMemberContainer.Key.APP_PLUGIN_ID) {
                when {
                    getAgpVersion(project) >= "3.6.0" -> {
                        "com.android.internal.application"
                    }
                    else -> {
                        "com.android.application"
                    }
                }
            }
        }

        fun getLibraryPluginId(project: Project): String {
            return StaticMemberContainer.get(StaticMemberContainer.Key.LIBRARY_PLUGIN_ID) {
                when {
                    getAgpVersion(project) >= "3.6.0" -> {
                        "com.android.internal.library"
                    }
                    else -> {
                        "com.android.library"
                    }
                }
            }
        }

        fun getVariantManager(project: Project): VariantManager? {
            val variantManagerStr = "variantManager"

            return when {
                project.plugins.hasPlugin(getAppPluginId(project)) -> {
                    ReflectionUtils.getFieldValue(
                        project.plugins.getPlugin(getAppPluginId(project)),
                        variantManagerStr
                    ) as VariantManager?
                }
                project.plugins.hasPlugin(getLibraryPluginId(project)) -> {
                    ReflectionUtils.getFieldValue(
                        project.plugins.getPlugin(getLibraryPluginId(project)),
                        variantManagerStr
                    ) as VariantManager?
                }
                else -> {
                    null
                }
            }
        }

        fun getFullVariantName(variantScope: VariantScope): String {
            val methodName = when {
                getAgpVersion(LintPlugin.mProject) >= "4.0.0" -> {
                    "getName"
                }
                else -> {
                    "getFullVariantName"
                }
            }
            return ReflectionUtils.invokeMethod(
                variantScope,
                methodName,
                arrayOf(),
                arrayOf()
            ) as String
        }

        fun addArtifactsToInputs(
            task: LintBaseTask,
            inputs: ConfigurableFileCollection,
            variantScope: VariantScope
        ) {
            val methodName = when {
                getAgpVersion(LintPlugin.mProject) >= "4.0.0" -> {
                    "addModelArtifactsToInputs"
                }
                else -> {
                    "addJarArtifactsToInputs"
                }
            }
            ReflectionUtils.invokeMethod(
                task,
                methodName,
                arrayOf(ConfigurableFileCollection::class.java, VariantScope::class.java),
                arrayOf(inputs, variantScope)
            )
        }
    }
}