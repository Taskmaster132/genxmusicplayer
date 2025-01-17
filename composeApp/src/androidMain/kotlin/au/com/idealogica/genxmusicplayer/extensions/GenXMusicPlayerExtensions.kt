package au.com.idealogica.genxmusicplayer.extensions

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import au.com.idealogica.genxmusicplayer.model.CurrentPlaylistSong

fun CurrentPlaylistSong.toMediaItem(): MediaItem {
	val metadata = MediaMetadata.Builder()
		.setTitle(this.song.name)
		.setArtist(this.song.artist)
		.build()

	return MediaItem.Builder()
		.setUri(this.song.path)
		.setMediaMetadata(metadata)
		.build()
}
