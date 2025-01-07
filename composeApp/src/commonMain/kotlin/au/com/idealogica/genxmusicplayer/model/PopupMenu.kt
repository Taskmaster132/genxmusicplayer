package au.com.idealogica.genxmusicplayer.model

data class PopupMenu(
	val label: String,
	val callback: (Song) -> Unit
)
