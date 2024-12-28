package au.com.idealogica.genxmusicplayer.ui.mainactivity

import au.com.idealogica.genxmusicplayer.model.PlaylistSong

sealed interface MainActivityScreenEvents {
	data class SongTapped(val song: PlaylistSong) : MainActivityScreenEvents
}