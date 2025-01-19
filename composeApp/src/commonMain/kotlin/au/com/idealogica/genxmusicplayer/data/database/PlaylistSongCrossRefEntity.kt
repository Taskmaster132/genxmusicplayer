package au.com.idealogica.genxmusicplayer.data.database

import androidx.room.Entity

@Entity(primaryKeys = ["playlistId", "songId"])
data class PlaylistSongCrossRefEntity(
	val playlistId: Long,
	val songId: Long
)
