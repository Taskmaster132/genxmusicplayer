package au.com.idealogica.genxmusicplayer.service

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import au.com.idealogica.genxmusicplayer.extensions.toMediaItem
import au.com.idealogica.genxmusicplayer.model.PlaylistSong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.IOException

class GenXMusicService : MediaSessionService() {
	private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

	private var mediaSession: MediaSession? = null

	val player: Player by inject()

	override fun onCreate() {
		super.onCreate()

		mediaSession = MediaSession.Builder(this, player).build()
	}

	override fun onBind(intent: Intent?): IBinder {
		super.onBind(intent)

		GenXMusicPlayerNotificationManager.ensureChannelExists(this)
//		val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//			.setStyle(
//				androidx.media.app.NotificationCompat.MediaStyle()
//					.setMediaSession(mediaSession?.player?.sessionToken)
//					.setShowActionsInCompactView(0, 1, 2) // Show play/pause, previous, next
//			)
//			.setSmallIcon(R.drawable.notification_icon)
//
//		val playPauseAction = NotificationCompat.Action(
//			R.drawable.play_pause,
//			"Play",
//			PendingIntent.getBroadcast(this, PLAY_REQUEST_CODE, Intent(ACTION_PLAY), PendingIntent.FLAG_IMMUTABLE)
//		)

//		startForeground(NOTIFICATION_ID, notification.build())

		return MusicBinder()
	}

	override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
		mediaSession

	private fun playSong(playlistSong: PlaylistSong) {
		try {
			mediaSession?.player?.stop()
			mediaSession?.player?.clearMediaItems()
			mediaSession?.player?.addMediaItem(playlistSong.toMediaItem())

			mediaSession?.player?.prepare()
			mediaSession?.player?.play()

			mediaSession?.player?.playlistMetadata
		} catch (e: IOException) {
			println("Error playing song: $e")
		}
	}

	private fun addToQueue(playList: List<PlaylistSong>) {
		if (playList.isEmpty()) {
			return
		}

		try {
			playSong(playList.first())
			for (i in 1 until playList.size) {
				mediaSession?.player?.addMediaItem(playList[i].toMediaItem())
			}
		} catch (e: IOException) {
			println("Error playing song: $e")
		}
	}

	fun pauseSong(): Boolean {
		return if (mediaSession?.player?.isPlaying == true) {
			mediaSession?.player?.pause()

			true
		} else {
			false
		}
	}

	fun resumeSong(): Boolean{
		return if (mediaSession?.player?.isPlaying != true) {
			mediaSession?.player?.play()

			true
		} else {
			false
		}
	}

	fun stopSong(): Boolean {
		return if (mediaSession?.player?.isPlaying == true) {
			mediaSession?.player?.stop()

			true
		} else {
			false
		}
	}

	override fun onTaskRemoved(rootIntent: Intent?) {
		val player = mediaSession?.player ?: return
		if (!player.playWhenReady
			|| player.mediaItemCount == 0
			|| player.playbackState == Player.STATE_ENDED) {
			// Stop the service if not playing, continue playing in the background
			// otherwise.
			stopSelf()
		}
	}

	fun listen(songs: StateFlow<List<PlaylistSong>>) {
		serviceScope.launch {
			songs.collect {
				addToQueue(it)
			}
		}
	}

	override fun onDestroy() {
		mediaSession?.run {
			player.release()
			release()
			mediaSession?.release()
			mediaSession = null
		}

		super.onDestroy()

		serviceScope.cancel()
	}

	inner class MusicBinder : Binder() {
		fun getService(): GenXMusicService {
			return this@GenXMusicService
		}
	}
}
