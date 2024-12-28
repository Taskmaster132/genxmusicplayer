package au.com.idealogica.genxmusicplayer.model

import kotlinx.serialization.Serializable

@Serializable
data class Song(
	val id: Long,
	val name: String,
	val path: String,
	val cdTrackNumber: String,
	val album: String,
	val artist: String,
	val author: String,
	val composer: String,
	val genre: String,
	val year: Int,
	val duration: Int
)
