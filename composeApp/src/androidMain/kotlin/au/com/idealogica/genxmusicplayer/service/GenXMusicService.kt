package au.com.idealogica.genxmusicplayer.service

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import au.com.idealogica.genxmusicplayer.extensions.toMediaItem
import au.com.idealogica.genxmusicplayer.model.PlaylistModification
import au.com.idealogica.genxmusicplayer.model.CurrentPlaylistSong
import au.com.idealogica.genxmusicplayer.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.IOException

class GenXMusicService : MediaSessionService() {
	private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

	private var mediaSession: MediaSession? = null

	val player: Player by inject()

	private var buildJob: Job? = null

	private val _currentPlaylist = MutableStateFlow<List<CurrentPlaylistSong>>(emptyList())
	val currentPlaylist = _currentPlaylist.asStateFlow()

	override fun onCreate() {
		super.onCreate()

		val session = MediaSession.Builder(this, player).build()
		addSession(session)
		mediaSession = session
	}

	override fun onBind(intent: Intent?): IBinder {
		super.onBind(intent)

		return MusicBinder()
	}

	override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
		mediaSession

	private fun playSong(playlistSong: CurrentPlaylistSong) {
		try {
			if (player.isCommandAvailable(Player.COMMAND_STOP)) {
				player.stop()
			}

			if (player.isCommandAvailable(Player.COMMAND_CHANGE_MEDIA_ITEMS)) {
				player.clearMediaItems()
				player.addMediaItem(playlistSong.toMediaItem())
			}

			if (player.isCommandAvailable(Player.COMMAND_PREPARE)) {
				player.prepare()
			}

			if (player.isCommandAvailable(Player.COMMAND_PLAY_PAUSE)) {
				player.play()
			}
		} catch (e: IOException) {
			println("Error playing song: $e")
		}
	}

	private fun modifyPlaylist(playlistModification: PlaylistModification) {
		serviceScope.launch {
			when (playlistModification) {
				PlaylistModification.NoAction -> return@launch
				is PlaylistModification.AddSongAndPlayNow -> addSongAndPlayNow(playlistModification.song)
				is PlaylistModification.AddSongAndPlayNext -> insertSongAsNextSongInPlaylist(playlistModification.song)
				is PlaylistModification.AddSongToPlaylist -> addSongToPlaylist(playlistModification.song)
				PlaylistModification.ClearPlaylist -> stopAndClear()
				is PlaylistModification.ShuffleModeChanged -> player.shuffleModeEnabled = playlistModification.active
				is PlaylistModification.PlaySong -> player.seekTo(playlistModification.index, 0)
			}
		}
	}

	private suspend fun addSongAndPlayNow(song: Song) {
		if (checkIfPlaylistIsEmpty(song)) {
			return
		}

		buildJob?.join()

		val playlistSong = CurrentPlaylistSong(song)
		val index = player.currentMediaItemIndex + 1
		player.addMediaItem(index, playlistSong.toMediaItem())
		player.seekToNextMediaItem()

		val currentPlaylist = _currentPlaylist.value.toMutableList()
		currentPlaylist.add(index, playlistSong)
		_currentPlaylist.update { currentPlaylist }
	}

	private suspend fun insertSongAsNextSongInPlaylist(song: Song) {
		if (checkIfPlaylistIsEmpty(song)) {
			return
		}

		buildJob?.join()

		val playlistSong = CurrentPlaylistSong(song)
		val index = player.currentMediaItemIndex + 1
		player.addMediaItem(index, playlistSong.toMediaItem())

		val currentPlaylist = _currentPlaylist.value.toMutableList()
		currentPlaylist.add(index, playlistSong)
		_currentPlaylist.update { currentPlaylist }
	}

	private suspend fun addSongToPlaylist(song: Song) {
		if (checkIfPlaylistIsEmpty(song)) {
			return
		}

		buildJob?.join()

		val playlistSong = CurrentPlaylistSong(song)
		player.addMediaItem(playlistSong.toMediaItem())

		val currentPlaylist = _currentPlaylist.value.toMutableList()
		currentPlaylist.add(playlistSong)
		_currentPlaylist.update { currentPlaylist }
	}

	private fun checkIfPlaylistIsEmpty(song: Song): Boolean {
		if (player.mediaItemCount < 1) {
			val playlistSong = CurrentPlaylistSong(song = song)
			_currentPlaylist.update { listOf(playlistSong) }
			playSong(playlistSong)
			return true
		}

		return false
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

	private fun stopAndClear() {
		player.stop()
		player.clearMediaItems()
		_currentPlaylist.update { emptyList() }
	}

	fun listen(modifications: StateFlow<PlaylistModification>) {
		serviceScope.launch {
			modifications.collect {
				modifyPlaylist(it)
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
