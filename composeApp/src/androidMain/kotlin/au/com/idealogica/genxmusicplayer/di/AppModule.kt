package au.com.idealogica.genxmusicplayer.di

import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import au.com.idealogica.genxmusicplayer.ui.mainactivity.MainActivityViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
	viewModelOf(::MainActivityViewModel)
	single<Player> {
		ExoPlayer.Builder(androidContext()).build()
	}
}