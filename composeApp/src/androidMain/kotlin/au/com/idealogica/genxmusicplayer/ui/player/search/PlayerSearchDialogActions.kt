package au.com.idealogica.genxmusicplayer.ui.player.search

sealed interface PlayerSearchDialogActions {
	data class SearchStringUpdated(val searchStr: String) : PlayerSearchDialogActions
	data object DoneButtonTapped : PlayerSearchDialogActions
}