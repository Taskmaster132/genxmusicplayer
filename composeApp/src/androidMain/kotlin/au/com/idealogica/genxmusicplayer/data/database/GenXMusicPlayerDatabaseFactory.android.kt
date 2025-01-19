@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package au.com.idealogica.genxmusicplayer.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class GenXMusicPlayerDatabaseFactory(
	private val context: Context
) {
	actual fun create(): RoomDatabase.Builder<GenXMusicPlayerDatabase> {
		val appContext = context.applicationContext
		val dbFile = appContext.getDatabasePath(GenXMusicPlayerDatabase.DB_NAME)

		return Room.databaseBuilder(
			context = appContext,
			name = dbFile.absolutePath
		)
	}
}