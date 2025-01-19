package au.com.idealogica.genxmusicplayer.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
	entities = [PlaylistEntity::class, SongEntity::class, PlaylistSongCrossRefEntity::class],
	version = 1
)
@ConstructedBy(GenXMusicPlayerDatabaseConstructor::class)
abstract class GenXMusicPlayerDatabase: RoomDatabase() {
	abstract val genXMusicPlayerDao: GenXMusicPlayerDao

	companion object {
		const val DB_NAME = "genXMusicPlayer.db"
	}
}