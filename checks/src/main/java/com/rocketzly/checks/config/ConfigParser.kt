package com.rocketzly.checks.config

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.rocketzly.checks.config.bean.AvoidUsageApi
import com.rocketzly.checks.config.bean.DependencyApi
import com.rocketzly.checks.config.bean.HandleExceptionMethod
import com.rocketzly.checks.config.bean.ResourceName
import java.io.File

/**
 * lint配置解析器
 * User: Rocket
 * Date: 2020/6/10
 * Time: 7:33 PM
 */
class ConfigParser(configFile: File) {

    private var configJson = JsonObject()

    companion object {
        const val KEY_AVOID_USAGE_API = "avoid_usage_api"
        const val KEY_HANDLE_EXCEPTION_METHOD = "handle_exception_method"
        const val KEY_DEPENDENCY_API = "dependency_api"
        const val KEY_RESOURCE_NAME = "resource_name"
    }

    init {
        var loadConfigSuccess = false
        if (configFile.exists() && configFile.isFile) {
            configJson = Gson().fromJson(configFile.bufferedReader(), JsonObject::class.java)
            loadConfigSuccess = true
        }
        println("---------------LintConfig----------------")
        println("Lint配置文件${configFile.absolutePath}加载${if (loadConfigSuccess) "成功" else "失败"}")
        println("---------------LintConfig----------------")
    }

    fun getAvoidUsageApi(): AvoidUsageApi {
        return Gson().fromJson(
            configJson.getAsJsonObject(KEY_AVOID_USAGE_API),
            AvoidUsageApi::class.java
        ) ?: AvoidUsageApi()
    }

    fun getHandleExceptionMethod(): List<HandleExceptionMethod> {
        return Gson().fromJson(
            configJson.getAsJsonArray(KEY_HANDLE_EXCEPTION_METHOD),
            object : TypeToken<List<HandleExceptionMethod>>() {}.type
        ) ?: listOf()
    }

    fun getDependencyApiList(): List<DependencyApi> {
        return Gson().fromJson(
            configJson.getAsJsonArray(KEY_DEPENDENCY_API),
            object : TypeToken<List<DependencyApi>>() {}.type
        ) ?: listOf()
    }

    fun getResourceName(): ResourceName {
        return Gson().fromJson(
            configJson.getAsJsonObject(KEY_RESOURCE_NAME),
            object : TypeToken<ResourceName>() {}.type
        ) ?: ResourceName()
    }
}