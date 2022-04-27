package com.example.traders.utils

import kotlin.reflect.KClass

object MappingUtils {
    fun paramsToJson(params: List<String>, subscription: String, type: String): String {
        val paramString = params.joinToString(separator = ", ") { param ->
            "\"${param}@${type}\""
        }
        return "{\n" +
                "    \"method\": \"${subscription}\",\n" +
                "    \"params\": [ ${paramString}],\n" +
                "    \"id\": 1 \n" +
                "}"
    }

    // Converts enum names to String array
    fun KClass<out Enum<*>>.enumConstantNames() =
        this.java.enumConstants.map(Enum<*>::name)
}
