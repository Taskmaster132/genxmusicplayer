package au.com.idealogica.genxmusicplayer.ui.playlists

import au.com.idealogica.genxmusicplayer.model.CurrentPlaylistSong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.androidx.scope.ScopeViewModel

class PlaylistsViewModel : ScopeViewModel() {
	private val _playlists = MutableStateFlow<List<CurrentPlaylistSong>>(emptyList())
	val playlists = _playlists.asStateFlow()

	private val _songsNotInPlaylist = MutableStateFlow<List<CurrentPlaylistSong>>(emptyList())
	val songsNotInPlaylist = _songsNotInPlaylist.asStateFlow()

	private val _songsInPlaylist = MutableStateFlow<List<CurrentPlaylistSong>>(emptyList())
	val songsInPlaylist = _songsInPlaylist.asStateFlow()
}