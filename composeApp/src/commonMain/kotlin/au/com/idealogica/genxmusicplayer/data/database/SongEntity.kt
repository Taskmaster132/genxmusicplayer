package au.com.idealogica.genxmusicplayer.data.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["path"], unique = true)])
data class SongEntity(
	@PrimaryKey
	val songId: Long,
	val name: String,
	val path: String,
	val cdTrackNumber: String,
	val album: String,
	val artist: String,
	val duration: Int
)