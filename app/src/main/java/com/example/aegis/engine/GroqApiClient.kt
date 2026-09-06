package com.example.aegis.engine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object GroqApiClient {

    suspend fun generateCompletion(
        apiKey: String,
        model: String = "llama-3.3-70b-versatile",
        systemPrompt: String,
        userPrompt: String,
        temperature: Double = 0.7,
        maxTokens: Int = 800
    ): Result<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(IllegalStateException("Groq API Key is empty"))
        }

        try {
            val url = URL("https://api.groq.com/openai/v1/chat/completions")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Authorization", "Bearer $apiKey")
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
                doOutput = true
                connectTimeout = 12000
                readTimeout = 18000
            }

            val messagesArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", systemPrompt)
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", userPrompt)
                })
            }

            val body = JSONObject().apply {
                put("model", model)
                put("messages", messagesArray)
                put("temperature", temperature)
                put("max_tokens", maxTokens)
            }

            OutputStreamWriter(conn.outputStream).use { it.write(body.toString()) }

            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val responseText = stream?.let { BufferedReader(InputStreamReader(it)).use { r -> r.readText() } } ?: ""

            if (code in 200..299) {
                val json = JSONObject(responseText)
                val choices = json.optJSONArray("choices")
                if (choices != null && choices.length() > 0) {
                    val messageObj = choices.getJSONObject(0).optJSONObject("message")
                    val content = messageObj?.optString("content", "") ?: ""
                    Result.success(content.trim())
                } else {
                    Result.failure(Exception("No choices returned from Groq API"))
                }
            } else {
                Result.failure(Exception("Groq API returned HTTP $code: $responseText"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
