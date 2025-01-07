package au.com.idealogica.genxmusicplayer.ui.mainactivity

import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import au.com.idealogica.genxmusicplayer.model.PlaylistModification
import au.com.idealogica.genxmusicplayer.model.PlaylistSong
import au.com.idealogica.genxmusicplayer.model.Song
import au.com.idealogica.genxmusicplayer.service.GenXDeviceService
import au.com.idealogica.genxmusicplayer.service.GenXMusicService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.scope.ScopeViewModel
import java.util.concurrent.atomic.AtomicBoolean

class MainActivityViewModel : ScopeViewModel() {

	private val _player = MutableStateFlow<Player?>(null)
	val player = _player.asStateFlow()

	private val _currentPlaylistName = MutableStateFlow("No playlist is currently selected")
	val currentPlaylistName = _currentPlaylistName.asStateFlow()

	private val _currentPlaylist = MutableStateFlow<List<PlaylistSong>>(emptyList())
	val currentPlaylist = _currentPlaylist.asStateFlow()

	private val _currentSongIndex = MutableStateFlow(-1)
	val currentSongIndex = _currentSongIndex.asStateFlow()

	private val _playlistModification = MutableStateFlow<PlaylistModification>(PlaylistModification.NoAction)
	private val playlistModification = _playlistModification.asStateFlow()

	private val serviceIsBound = AtomicBoolean(false)

	lateinit var deviceService: GenXDeviceService
	val allSongsOnDevice: StateFlow<List<Song>> by lazy {
		deviceService.allSongsOnDevice
	}

	fun serviceBound(musicService: GenXMusicService) {
		_player.update { musicService.player }
		musicService.listen(playlistModification)

		musicService.player.addListener(object : Player.Listener {
			override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
				val currentIndex = musicService.player.currentMediaItemIndex
				_currentSongIndex.update { currentIndex }
			}
		})

		viewModelScope.launch(Dispatchers.Default) {
			musicService.currentPlaylist.collect { newPlaylist ->
				_currentPlaylist.update { newPlaylist }
			}
		}
	}

	fun addSongToPlayListAndPlayImmediately(song: Song) {
		_currentPlaylistName.update { "Ad hoc" }
		_playlistModification.update { PlaylistModification.AddSongAndPlayNow(song) }

		if (serviceIsBound.compareAndSet(false, true)) {
			deviceService.bindService()
		}
	}

	fun insertSongAsNextSongInPlaylist(song: Song) {
		_currentPlaylistName.update { "Ad hoc" }
		_playlistModification.update { PlaylistModification.AddSongAndPlayNext(song) }

		if (serviceIsBound.compareAndSet(false, true)) {
			deviceService.bindService()
		}
	}

	fun addSongToPlaylist(song: Song) {
		_currentPlaylistName.update { "Ad hoc" }
		_playlistModification.update { PlaylistModification.AddSongToPlaylist(song) }

		if (serviceIsBound.compareAndSet(false, true)) {
			deviceService.bindService()
		}
	}
}