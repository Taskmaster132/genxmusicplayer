package au.com.idealogica.genxmusicplayer.data.database

import androidx.room.RoomDatabaseConstructor

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object GenXMusicPlayerDatabaseConstructor: RoomDatabaseConstructor<GenXMusicPlayerDatabase> {
	override fun initialize(): GenXMusicPlayerDatabase
}