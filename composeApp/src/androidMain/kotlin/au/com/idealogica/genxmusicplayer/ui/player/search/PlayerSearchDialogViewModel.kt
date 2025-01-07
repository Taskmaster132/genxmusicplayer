package au.com.idealogica.genxmusicplayer.ui.player.search

import androidx.lifecycle.viewModelScope
import au.com.idealogica.genxmusicplayer.model.Song
import au.com.idealogica.genxmusicplayer.ui.mainactivity.MainActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.scope.ScopeViewModel

class PlayerSearchDialogViewModel : ScopeViewModel() {

	private val _searchStr = MutableStateFlow("")
	val searchStr = _searchStr.asStateFlow()

	private val _allSongsOnDevice = MutableStateFlow<List<Song>>(emptyList())
	private val allSongsOnDevice = _allSongsOnDevice.asStateFlow()

	val visibleSongs: StateFlow<List<Song>> = combine(searchStr, allSongsOnDevice) { searchStr, allSongs ->
		if (searchStr.isBlank()) {
			allSongs
		} else {
			allSongs.filter { it.containsText(searchStr) }
		}
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5000),
		initialValue = emptyList()
	)

	fun loadAllSongs(mainActivityViewModel: MainActivityViewModel) {
		viewModelScope.launch(Dispatchers.IO) {
			mainActivityViewModel.allSongsOnDevice.collect { songs ->
				_allSongsOnDevice.update { songs }
			}
		}
	}

	fun onAction(action: PlayerSearchDialogActions) {
		when (action) {
			is PlayerSearchDialogActions.SearchStringUpdated -> _searchStr.update { action.searchStr }
			else -> { /* should never get here. */ }
		}
	}
}