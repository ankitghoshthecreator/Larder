package com.larder.app.feature.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.larder.app.MainActivity
import com.larder.app.domain.calculator.ExpiryCalculator
import com.larder.app.domain.model.Item

object NotificationChannels {
    const val CHANNEL_EXPIRING_SOON = "larder_expiring_soon"
    const val CHANNEL_LOW_STOCK = "larder_low_stock"
    const val CHANNEL_HOUSEHOLD_ACTIVITY = "larder_household_activity"
}

class LarderNotificationService(private val context: Context) {

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)

            listOf(
                NotificationChannel(
                    NotificationChannels.CHANNEL_EXPIRING_SOON,
                    "Expiring Items",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply { description = "Alerts for pantry items expiring within 3 days" },

                NotificationChannel(
                    NotificationChannels.CHANNEL_LOW_STOCK,
                    "Low Stock",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = "Alerts for pantry items running low in quantity" },

                NotificationChannel(
                    NotificationChannels.CHANNEL_HOUSEHOLD_ACTIVITY,
                    "Household Activity",
                    NotificationManager.IMPORTANCE_LOW
                ).apply { description = "Updates from other household members" }

            ).forEach { notificationManager.createNotificationChannel(it) }
        }
    }

    fun notifyExpiringSoon(items: List<Item>) {
        if (items.isEmpty()) return

        val title = if (items.size == 1) {
            "${items.first().name} is expiring soon"
        } else {
            "${items.size} items are expiring soon"
        }

        val body = items.take(3).joinToString(", ") { item ->
            val days = ExpiryCalculator.calculateDaysRemaining(item.expiryEstimate)
            "${item.name} (${if (days >= 0) "${days}d" else "expired"})"
        }

        showNotification(
            id = 1001,
            channelId = NotificationChannels.CHANNEL_EXPIRING_SOON,
            title = title,
            body = body
        )
    }

    fun notifyLowStock(items: List<Item>) {
        if (items.isEmpty()) return

        val title = if (items.size == 1) {
            "${items.first().name} is running low"
        } else {
            "${items.size} items are running low"
        }

        val body = items.take(3).joinToString(", ") { "${it.name} (${it.quantity} ${it.unit})" }

        showNotification(
            id = 1002,
            channelId = NotificationChannels.CHANNEL_LOW_STOCK,
            title = title,
            body = body
        )
    }

    fun notifyHouseholdActivity(memberName: String, actionDescription: String) {
        showNotification(
            id = 1003,
            channelId = NotificationChannels.CHANNEL_HOUSEHOLD_ACTIVITY,
            title = "$memberName updated the pantry",
            body = actionDescription
        )
    }

    private fun showNotification(id: Int, channelId: String, title: String, body: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(id, notification)
        } catch (e: SecurityException) {
            // Permission not granted — silently skip
        }
    }
}
