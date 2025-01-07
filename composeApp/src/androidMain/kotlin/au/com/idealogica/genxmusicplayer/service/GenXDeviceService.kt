package au.com.idealogica.genxmusicplayer.service

import au.com.idealogica.genxmusicplayer.model.Song
import kotlinx.coroutines.flow.StateFlow

interface GenXDeviceService {
	fun bindService()
	val allSongsOnDevice: StateFlow<List<Song>>
}