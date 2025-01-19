package au.com.idealogica.genxmusicplayer.ui.player

sealed interface PlayerScreenActions {
	data class ShuffleTapped(val shuffle: Boolean) : PlayerScreenActions
	data object AddTapped : PlayerScreenActions
	data object ClearTapped : PlayerScreenActions
	data class PlayTapped(val index: Int) : PlayerScreenActions
}