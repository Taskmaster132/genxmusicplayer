package au.com.idealogica.genxmusicplayer.repository

import au.com.idealogica.genxmusicplayer.model.Song
import au.com.idealogica.genxmusicplayer.service.NativeMusicService
import kotlinx.coroutines.flow.StateFlow

interface GenXMusicRepository {
	fun initGenXMusicService(service: NativeMusicService)
	val songsOnDevice: StateFlow<List<Song>>
}