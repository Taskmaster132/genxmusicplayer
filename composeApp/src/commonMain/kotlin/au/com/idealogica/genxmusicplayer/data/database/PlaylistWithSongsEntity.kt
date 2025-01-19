package au.com.idealogica.genxmusicplayer.data.database

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PlaylistWithSongsEntity(
	@Embedded val playlist: PlaylistEntity,
	@Relation(
		parentColumn = "playlistId",
		entityColumn = "songId",
		associateBy = Junction(PlaylistSongCrossRefEntity::class)
	)
	val songs: List<SongEntity>
)
