package au.com.idealogica.genxmusicplayer.di

import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import au.com.idealogica.genxmusicplayer.data.database.GenXMusicPlayerDatabaseFactory
import au.com.idealogica.genxmusicplayer.ui.mainactivity.MainActivityViewModel
import au.com.idealogica.genxmusicplayer.ui.player.PlayerViewModel
import au.com.idealogica.genxmusicplayer.ui.player.search.PlayerSearchDialogViewModel
import au.com.idealogica.genxmusicplayer.ui.playlists.PlaylistsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual val platformModule: Module
	get() = module {
		viewModelOf(::PlayerViewModel)
		viewModelOf(::PlayerSearchDialogViewModel)
		viewModelOf(::PlaylistsViewModel)
		viewModelOf(::MainActivityViewModel)

		single<Player> {
			ExoPlayer.Builder(androidContext()).build()
		}

		single { GenXMusicPlayerDatabaseFactory(androidApplication()) }
	}