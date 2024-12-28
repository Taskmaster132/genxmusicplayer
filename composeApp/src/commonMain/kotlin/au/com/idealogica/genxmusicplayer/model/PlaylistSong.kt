package au.com.idealogica.genxmusicplayer.model

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistSong(
	val index: Int,
	val song: Song
)
