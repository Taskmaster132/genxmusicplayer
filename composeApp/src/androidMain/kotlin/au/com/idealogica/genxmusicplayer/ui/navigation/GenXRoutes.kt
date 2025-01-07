package au.com.idealogica.genxmusicplayer.ui.navigation

import kotlinx.serialization.Serializable

sealed interface GenXRoutes {
	@Serializable
	data object GenXGraph : GenXRoutes

	@Serializable
	data object Player : GenXRoutes

	@Serializable
	data object Playlists
}