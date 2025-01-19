package au.com.idealogica.genxmusicplayer.data.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["name"], unique = true)])
data class PlaylistEntity(
	@PrimaryKey(autoGenerate = true)
	val playlistId: Long,
	val name: String
)