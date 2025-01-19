package au.com.idealogica.genxmusicplayer.model

sealed interface PlaylistModification {
	data object NoAction : PlaylistModification
	data class AddSongAndPlayNow(val song: Song) : PlaylistModification
	data class AddSongAndPlayNext(val song: Song) : PlaylistModification
	data class AddSongToPlaylist(val song: Song) : PlaylistModification
	data object ClearPlaylist : PlaylistModification
	data class ShuffleModeChanged(val active: Boolean) : PlaylistModification
	data class PlaySong(val index: Int) : PlaylistModification
}