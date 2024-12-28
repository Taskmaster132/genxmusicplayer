package au.com.idealogica.genxmusicplayer.extensions

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import au.com.idealogica.genxmusicplayer.model.PlaylistSong

fun PlaylistSong.toMediaItem(): MediaItem {
	val metadata = MediaMetadata.Builder()
		.setTitle(this.song.name)
		.setArtist(this.song.artist)
		.setGenre(this.song.genre)
		.build()

	return MediaItem.Builder()
		.setUri(this.song.path)
		.setMediaMetadata(metadata)
		.build()
}