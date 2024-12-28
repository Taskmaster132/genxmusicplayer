package au.com.idealogica.genxmusicplayer

import android.app.Application
import au.com.idealogica.genxmusicplayer.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class GenXMusicPlayerApplication : Application() {
	override fun onCreate() {
		super.onCreate()

		startKoin {
			androidLogger()
			androidContext(this@GenXMusicPlayerApplication)
			modules(appModule)
		}
	}
}