package com.example.traders.utils

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
}
