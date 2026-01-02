// TimeUtils.kt
package com.team.squadx

import java.util.concurrent.TimeUnit

object TimeUtils {

    fun getTimeAgo(time: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - time

        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
            diff < TimeUnit.HOURS.toMillis(1) ->
                "${TimeUnit.MILLISECONDS.toMinutes(diff)} min ago"

            diff < TimeUnit.DAYS.toMillis(1) ->
                "${TimeUnit.MILLISECONDS.toHours(diff)} hours ago"

            diff < TimeUnit.DAYS.toMillis(2) -> "Yesterday"

            else -> "${TimeUnit.MILLISECONDS.toDays(diff)} days ago"
        }
    }
}
