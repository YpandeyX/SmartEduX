package com.team.squadx

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

object GeminiApi {

    // 🔑 Use your Gemini API key here
    private const val API_KEY = "API KEY HIDDEN"

    private const val URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$API_KEY"

    private val client = OkHttpClient()

    fun call(prompt: String): String {
        return try {

            // ---- Build Gemini Request JSON ----
            val part = JSONObject()
            part.put("text", prompt)

            val partsArray = JSONArray()
            partsArray.put(part)

            val content = JSONObject()
            content.put("role", "user")
            content.put("parts", partsArray)

            val contentsArray = JSONArray()
            contentsArray.put(content)

            val root = JSONObject()
            root.put("contents", contentsArray)

            val body = root.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(URL)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: return "Empty Gemini response"

            val json = JSONObject(responseBody)

            // ---- Handle API error ----
            if (json.has("error")) {
                return "Gemini Error: " + json.getJSONObject("error").getString("message")
            }

            // ---- Parse Gemini answer ----
            if (!json.has("candidates")) {
                return "AI Error: No candidates\n$responseBody"
            }

            val candidates = json.getJSONArray("candidates")
            if (candidates.length() == 0) {
                return "AI returned empty result"
            }

            val contentObj = candidates.getJSONObject(0).getJSONObject("content")
            val parts = contentObj.getJSONArray("parts")

            if (parts.length() == 0) {
                return "AI returned no text"
            }

            parts.getJSONObject(0).getString("text")

        } catch (e: Exception) {
            "AI crash: ${e.message}"
        }
    }
}