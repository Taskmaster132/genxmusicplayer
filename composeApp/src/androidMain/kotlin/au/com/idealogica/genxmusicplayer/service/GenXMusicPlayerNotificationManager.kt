package au.com.idealogica.genxmusicplayer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import au.com.idealogica.genxmusicplayer.model.GenXMusicPlayerConstants.CHANNEL_ID

object GenXMusicPlayerNotificationManager {
	fun ensureChannelExists(context: Context) {
		val channel = NotificationChannel(CHANNEL_ID, "GenXMusicPlayer music channel", NotificationManager.IMPORTANCE_DEFAULT)
		val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		notificationManager.createNotificationChannel(channel)
	}
}