package au.com.idealogica.genxmusicplayer

import androidx.compose.ui.graphics.vector.ImageVector
import au.com.idealogica.genxmusicplayer.model.PlaylistSong
import kotlinx.coroutines.flow.StateFlow
import platform.UIKit.UIDevice

class IOSPlatform : Platform {
	override val currentSong: StateFlow<PlaylistSong?>
		get() = TODO("Not yet implemented")
	override val songs: StateFlow<List<PlaylistSong>>
		get() = TODO("Not yet implemented")
	override val playerState: StateFlow<PlayerState>
		get() = TODO("Not yet implemented")
	override val playSong: (PlaylistSong) -> Unit
		get() = TODO("Not yet implemented")
	override val replaySong: () -> Unit
		get() = TODO("Not yet implemented")
	override val resumeSong: () -> Unit
		get() = TODO("Not yet implemented")
	override val pauseSong: () -> Unit
		get() = TODO("Not yet implemented")
	override val stopSong: () -> Unit
		get() = TODO("Not yet implemented")
	override val playIcon: ImageVector
		get() = TODO("Not yet implemented")
	override val pauseIcon: ImageVector
		get() = TODO("Not yet implemented")
	override val stopIcon: ImageVector
		get() = TODO("Not yet implemented")
}

//actual fun getPlatform(): Platform = IOSPlatform()