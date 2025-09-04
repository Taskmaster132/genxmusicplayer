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
	val duration: Int
) {
	fun containsText(text: String): Boolean {
		return name.contains(text, ignoreCase = true)
			|| album.contains(text, ignoreCase = true)
			|| artist.contains(text, ignoreCase = true)
	}

	override fun hashCode(): Int {
		return path.hashCode()
	}

	override fun equals(other: Any?): Boolean {
		return other is Song && other.path == path
	}
}
