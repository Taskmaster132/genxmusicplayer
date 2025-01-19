package au.com.idealogica.genxmusicplayer

import android.app.Application
import au.com.idealogica.genxmusicplayer.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class GenXMusicPlayerApplication : Application() {
	override fun onCreate() {
		super.onCreate()

		initKoin {
			androidLogger()
			androidContext(this@GenXMusicPlayerApplication)
		}
	}
}