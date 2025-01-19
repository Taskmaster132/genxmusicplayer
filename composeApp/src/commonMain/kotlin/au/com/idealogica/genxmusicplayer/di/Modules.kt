package au.com.idealogica.genxmusicplayer.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import au.com.idealogica.genxmusicplayer.data.database.GenXMusicPlayerDatabase
import au.com.idealogica.genxmusicplayer.data.database.GenXMusicPlayerDatabaseFactory
import au.com.idealogica.genxmusicplayer.repository.DefaultGenXMusicRepository
import au.com.idealogica.genxmusicplayer.repository.GenXMusicRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
	singleOf(::DefaultGenXMusicRepository).bind<GenXMusicRepository>()
	single {
		get<GenXMusicPlayerDatabaseFactory>().create()
			.setDriver(BundledSQLiteDriver())
			.build()
	}
	single { get<GenXMusicPlayerDatabase>().genXMusicPlayerDao }
}