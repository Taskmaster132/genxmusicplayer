package au.com.idealogica.genxmusicplayer.service

import au.com.idealogica.genxmusicplayer.model.PlaylistSong

interface GenXMusicServiceBridge {
	fun playSong(playlistSong: PlaylistSong)
	fun pauseSong()
	fun resumeSong()
	fun stopSong()
}