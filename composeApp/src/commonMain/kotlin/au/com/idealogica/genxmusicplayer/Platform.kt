package au.com.idealogica.genxmusicplayer

import androidx.compose.ui.graphics.vector.ImageVector
import au.com.idealogica.genxmusicplayer.model.PlayerState
import au.com.idealogica.genxmusicplayer.model.PlaylistSong
import kotlinx.coroutines.flow.StateFlow

interface Platform {
	val currentSong: StateFlow<PlaylistSong?>
	val songs: StateFlow<List<PlaylistSong>>
	val playerState: StateFlow<PlayerState>
	val playSong: (PlaylistSong) -> Unit
	val replaySong: () -> Unit
	val resumeSong: () -> Unit
	val pauseSong: () -> Unit
	val stopSong: () -> Unit

	val playIcon: ImageVector
	val pauseIcon: ImageVector
	val stopIcon: ImageVector
}