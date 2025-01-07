package au.com.idealogica.genxmusicplayer.ui.playlists

import au.com.idealogica.genxmusicplayer.model.PlaylistSong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.androidx.scope.ScopeViewModel

class PlaylistsViewModel : ScopeViewModel() {
	private val _playlists = MutableStateFlow<List<PlaylistSong>>(emptyList())
	val playlists = _playlists.asStateFlow()

	private val _songsNotInPlaylist = MutableStateFlow<List<PlaylistSong>>(emptyList())
	val songsNotInPlaylist = _songsNotInPlaylist.asStateFlow()

	private val _songsInPlaylist = MutableStateFlow<List<PlaylistSong>>(emptyList())
	val songsInPlaylist = _songsInPlaylist.asStateFlow()
}