package au.com.idealogica.genxmusicplayer.ui.player

sealed interface PlayerScreenActions {
	data object ShuffleTapped : PlayerScreenActions
	data object SearchTapped : PlayerScreenActions
	data object SortTapped : PlayerScreenActions
	data object ClearTapped: PlayerScreenActions
}