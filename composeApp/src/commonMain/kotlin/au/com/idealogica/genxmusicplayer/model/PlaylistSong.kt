package au.com.idealogica.genxmusicplayer.model

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistSong(
	val song: Song
) {
	private val timestamp = Clock.System.now().toEpochMilliseconds()
	val id: Int = hashCode()

	override fun hashCode(): Int {
		return (17 * song.hashCode()) + timestamp.hashCode()
	}

	override fun equals(other: Any?): Boolean {
		return other is PlaylistSong
			&& other.timestamp == timestamp
			&& other.song == song
	}
}
