package au.com.idealogica.genxmusicplayer.ui.player.search

import androidx.lifecycle.viewModelScope
import au.com.idealogica.genxmusicplayer.model.Song
import au.com.idealogica.genxmusicplayer.repository.GenXMusicRepository
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
import org.koin.core.component.inject

class PlayerSearchDialogViewModel : ScopeViewModel() {

	private val genXMusicRepository: GenXMusicRepository by inject()

	private val _searchStr = MutableStateFlow("")
	val searchStr = _searchStr.asStateFlow()

	private val allSongsOnDevice = genXMusicRepository.songsOnDevice

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

	fun onAction(action: PlayerSearchDialogActions) {
		when (action) {
			is PlayerSearchDialogActions.SearchStringUpdated -> _searchStr.update { action.searchStr }
			else -> { /* should never get here. */ }
		}
	}
}