package com.example.aegis.engine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

data class LinkedInUserInfo(
    val sub: String,
    val name: String,
    val email: String? = null,
    val pictureUrl: String? = null,
    val rawJson: String? = null
)

data class LinkedInPostResult(
    val success: Boolean,
    val postId: String? = null,
    val message: String,
    val httpCode: Int = 0
)

object LinkedInApiClient {

    suspend fun fetchUserInfo(accessToken: String): Result<LinkedInUserInfo> = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.linkedin.com/v2/userinfo")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("Authorization", "Bearer $accessToken")
                setRequestProperty("Accept", "application/json")
                connectTimeout = 10000
                readTimeout = 10000
            }

            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val responseText = BufferedReader(InputStreamReader(stream)).use { it.readText() }

            if (code in 200..299) {
                val json = JSONObject(responseText)
                val sub = json.optString("sub", "")
                val name = json.optString("name", "LinkedIn User")
                val email = json.optString("email", null)
                val picture = json.optString("picture", null)
                Result.success(LinkedInUserInfo(sub = sub, name = name, email = email, pictureUrl = picture, rawJson = responseText))
            } else {
                Result.failure(Exception("LinkedIn API returned HTTP $code: $responseText"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun publishUgcPost(
        accessToken: String,
        authorUrn: String,
        text: String
    ): LinkedInPostResult = withContext(Dispatchers.IO) {
        try {
            val fullAuthorUrn = if (authorUrn.startsWith("urn:li:person:")) authorUrn else "urn:li:person:$authorUrn"

            // Try UGC Posts endpoint
            val url = URL("https://api.linkedin.com/v2/ugcPosts")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Authorization", "Bearer $accessToken")
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("X-Restli-Protocol-Version", "2.0.0")
                doOutput = true
                connectTimeout = 12000
                readTimeout = 12000
            }

            val payload = JSONObject().apply {
                put("author", fullAuthorUrn)
                put("lifecycleState", "PUBLISHED")
                put("specificContent", JSONObject().apply {
                    put("com.linkedin.ugc.ShareContent", JSONObject().apply {
                        put("shareCommentary", JSONObject().apply {
                            put("text", text)
                        })
                        put("shareMediaCategory", "NONE")
                    })
                })
                put("visibility", JSONObject().apply {
                    put("com.linkedin.ugc.MemberNetworkVisibility", "PUBLIC")
                })
            }

            OutputStreamWriter(conn.outputStream).use { it.write(payload.toString()) }

            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val responseText = stream?.let { BufferedReader(InputStreamReader(it)).use { r -> r.readText() } } ?: ""

            if (code == 201 || code == 200) {
                val postUrn = try {
                    JSONObject(responseText).optString("id", "urn:li:ugcPost:${System.currentTimeMillis()}")
                } catch (_: Exception) {
                    "urn:li:ugcPost:${System.currentTimeMillis()}"
                }
                LinkedInPostResult(
                    success = true,
                    postId = postUrn,
                    message = "Successfully published to LinkedIn feed!",
                    httpCode = code
                )
            } else {
                // If UGC fails due to endpoint version, try REST posts endpoint
                val restResult = publishRestPost(accessToken, fullAuthorUrn, text)
                if (restResult.success) {
                    restResult
                } else {
                    LinkedInPostResult(
                        success = false,
                        message = "LinkedIn HTTP $code: $responseText",
                        httpCode = code
                    )
                }
            }
        } catch (e: Exception) {
            LinkedInPostResult(
                success = false,
                message = e.localizedMessage ?: "Network connection error while calling LinkedIn",
                httpCode = -1
            )
        }
    }

    private suspend fun publishRestPost(
        accessToken: String,
        authorUrn: String,
        text: String
    ): LinkedInPostResult = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.linkedin.com/rest/posts")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Authorization", "Bearer $accessToken")
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("LinkedIn-Version", "202401")
                setRequestProperty("X-Restli-Protocol-Version", "2.0.0")
                doOutput = true
                connectTimeout = 10000
                readTimeout = 10000
            }

            val payload = JSONObject().apply {
                put("author", authorUrn)
                put("commentary", text)
                put("visibility", "PUBLIC")
                put("distribution", JSONObject().apply {
                    put("feedDistribution", "MAIN_FEED")
                })
                put("lifecycleState", "PUBLISHED")
                put("isReshareDisabledByAuthor", false)
            }

            OutputStreamWriter(conn.outputStream).use { it.write(payload.toString()) }

            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val responseText = stream?.let { BufferedReader(InputStreamReader(it)).use { r -> r.readText() } } ?: ""

            if (code == 201 || code == 200) {
                LinkedInPostResult(
                    success = true,
                    postId = "urn:li:share:${System.currentTimeMillis()}",
                    message = "Published successfully via LinkedIn REST API",
                    httpCode = code
                )
            } else {
                LinkedInPostResult(
                    success = false,
                    message = "REST API Error $code: $responseText",
                    httpCode = code
                )
            }
        } catch (e: Exception) {
            LinkedInPostResult(
                success = false,
                message = e.localizedMessage ?: "REST API error",
                httpCode = -1
            )
        }
    }
}
