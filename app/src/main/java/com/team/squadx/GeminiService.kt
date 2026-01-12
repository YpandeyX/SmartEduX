package com.team.squadx

import com.google.ai.client.generativeai.GenerativeModel

object GeminiService {

    private const val API_KEY = "AIzaSyB8LT2p9SGkyTpsfp6aXzdQxw0a-ZxXwXE"

    suspend fun extractQuery(text: String): String {

        val prompt = """
You are an API. 
Extract ONLY JSON from this query.

Format:
{
  "subject": "",
  "branch": "",
  "section": ""
}

User: "$text"

Rules:
- Do not explain
- Do not add any text
- Only output JSON
"""

        val raw = GeminiApi.call(prompt)

        // 🔥 Clean response
        val start = raw.indexOf("{")
        val end = raw.lastIndexOf("}") + 1

        return raw.substring(start, end)
    }

    suspend fun formatAnswer(text: String): String {
        val prompt = "Make this reply friendly and human: $text"
        return GeminiApi.call(prompt)
    }
}
