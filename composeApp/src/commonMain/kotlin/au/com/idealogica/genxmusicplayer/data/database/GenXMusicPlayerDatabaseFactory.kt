package au.com.idealogica.genxmusicplayer.data.database

import androidx.room.RoomDatabase

expect class GenXMusicPlayerDatabaseFactory {
	fun create(): RoomDatabase.Builder<GenXMusicPlayerDatabase>
}