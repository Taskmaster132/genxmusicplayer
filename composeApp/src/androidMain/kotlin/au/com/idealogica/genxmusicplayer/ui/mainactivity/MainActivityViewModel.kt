package au.com.idealogica.genxmusicplayer.ui.mainactivity

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.Player
import au.com.idealogica.genxmusicplayer.model.PlaylistSong
import au.com.idealogica.genxmusicplayer.model.Song
import au.com.idealogica.genxmusicplayer.service.GenXMusicService
import au.com.idealogica.genxmusicplayer.service.GenXMusicServiceBridge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.scope.ScopeViewModel

class MainActivityViewModel : ScopeViewModel() {
	private val _player = MutableStateFlow<Player?>(null)
	val player = _player.asStateFlow()

	private val _songs = MutableStateFlow<List<PlaylistSong>>(emptyList())
	val songs = _songs.asStateFlow()

	private val _currentSong = MutableStateFlow<PlaylistSong?>(null)
	val currentSong = _currentSong.asStateFlow()

	private val _currentPlaylist = MutableStateFlow<List<PlaylistSong>>(emptyList())

	private var bridge: GenXMusicServiceBridge? = null

	fun updateSongs(songs: List<Song>) {
		val songList = songs.mapIndexed { index, song -> PlaylistSong(index, song) }
		_songs.tryEmit(songList)
	}

	fun onEvent(event: MainActivityScreenEvents) {
		when (event) {
			is MainActivityScreenEvents.SongTapped -> {
				event.song.index
				bridge?.playSong(event.song)
			}
		}
	}

	fun playSong(index: Int) {
		viewModelScope.launch(Dispatchers.Default) {
			val currentList = _songs.value.subList(index, _songs.value.size)
			val start = _songs.value.subList(0, index)
			val newList = currentList + start
			_currentPlaylist.tryEmit(newList)
		}
	}

	fun loadPlayer(service: GenXMusicService) {
		_player.tryEmit(service.player)
		service.listen(_currentPlaylist.asStateFlow())
	}

	fun setBridge(bridge: GenXMusicServiceBridge) {
		this.bridge = bridge
	}
}