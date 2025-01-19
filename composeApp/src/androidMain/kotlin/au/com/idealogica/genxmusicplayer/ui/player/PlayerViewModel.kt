package au.com.idealogica.genxmusicplayer.ui.player

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.androidx.scope.ScopeViewModel

class PlayerViewModel : ScopeViewModel() {

	private val _showSearchDialog = MutableStateFlow(false)
	val showSearchDialog = _showSearchDialog.asStateFlow()

	fun searchTapped() {
		_showSearchDialog.update { true }
	}

	fun hideSearchDialog() {
		_showSearchDialog.update { false}
	}
}