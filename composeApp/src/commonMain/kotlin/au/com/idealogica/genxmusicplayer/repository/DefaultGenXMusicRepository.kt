package au.com.idealogica.genxmusicplayer.repository

import au.com.idealogica.genxmusicplayer.data.database.GenXMusicPlayerDao
import au.com.idealogica.genxmusicplayer.model.Song
import au.com.idealogica.genxmusicplayer.service.NativeMusicService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DefaultGenXMusicRepository(
	private val genXMusicPlayerDao: GenXMusicPlayerDao
) : GenXMusicRepository {
	private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
	private var collectMusicJob: Job? = null

	private val _songsOnDevice = MutableStateFlow<List<Song>>(emptyList())
	override val songsOnDevice = _songsOnDevice.asStateFlow()

	override fun initGenXMusicService(service: NativeMusicService) {
		collectMusicJob?.cancel()

		collectMusicJob = serviceScope.launch {
			service.allSongsOnDevice.collect {
				updateSongs(it)
			}
		}
	}

	private fun updateSongs(songsOnDevice: List<Song>) {
		_songsOnDevice.update { songsOnDevice }
	}
}