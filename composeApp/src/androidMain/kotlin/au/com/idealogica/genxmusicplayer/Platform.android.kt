package au.com.idealogica.genxmusicplayer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.ui.graphics.vector.ImageVector
import au.com.idealogica.genxmusicplayer.model.PlayerState
import au.com.idealogica.genxmusicplayer.model.PlaylistSong
import au.com.idealogica.genxmusicplayer.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AndroidPlatform(private val activity: MainActivity) : Platform {
	private var currentSongIndex = -1
	private val _currentSong = MutableStateFlow<PlaylistSong?>(null)
	private val _songs = MutableStateFlow<List<PlaylistSong>>(emptyList())
	private val _playingState = MutableStateFlow(PlayerState.STOPPED)

	fun updateCurrentSong(playlistSong: PlaylistSong) {
		currentSongIndex = playlistSong.index
		_currentSong.tryEmit(playlistSong)
	}

	fun updateSongs(songs: List<Song>) {
		_songs.tryEmit(
			songs.mapIndexed { index, song -> PlaylistSong(index, song) }
		)
	}

	fun updatePlaying(newState: PlayerState) {
		_playingState.tryEmit(newState)
	}

	fun playNextSong() {
		val currentPlayList = _songs.value
		if (currentPlayList.isEmpty()) {
			return
		}

		val nextSongIndex = (currentSongIndex + 1) % currentPlayList.size
		val nextSong = currentPlayList[nextSongIndex]
		playSong(nextSong)
	}

	private fun internalReplaySong() {
		val playList = _songs.value
		if (playList.isEmpty()) {
			return
		}

		val newIndex = if (currentSongIndex < 0) 0 else currentSongIndex
		val newSong = playList[newIndex]
		playSong(newSong)
	}

	override val currentSong: StateFlow<PlaylistSong?>
		get() = _currentSong
	override val playSong: (PlaylistSong) -> Unit
		get() = { song -> activity.playSong(song) }
	override val replaySong: () -> Unit
		get() = { internalReplaySong() }
	override val songs: StateFlow<List<PlaylistSong>>
		get() = _songs
	override val playerState: StateFlow<PlayerState>
		get() = _playingState
	override val playIcon: ImageVector
		get() = Icons.Filled.PlayArrow
	override val pauseIcon: ImageVector
		get() = Icons.Filled.Pause
	override val stopIcon: ImageVector
		get() = Icons.Filled.Stop
	override val pauseSong: () -> Unit
		get() = { activity.pauseSong() }
	override val stopSong: () -> Unit
		get() = { activity.stopSong() }
	override val resumeSong: () -> Unit
		get() = { activity.resumeSong() }
}