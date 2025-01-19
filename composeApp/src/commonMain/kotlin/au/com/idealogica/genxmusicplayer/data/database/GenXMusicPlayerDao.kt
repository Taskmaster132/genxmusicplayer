package au.com.idealogica.genxmusicplayer.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface GenXMusicPlayerDao {
	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insertPlaylist(playlist: PlaylistEntity): Long

	@Insert
	suspend fun insertSong(song: SongEntity): Long

	@Insert
	suspend fun insertPlaylistSongCrossRef(playlistSongCrossRef: PlaylistSongCrossRefEntity)

	@Transaction
	@Query("SELECT * FROM PlaylistEntity")
	suspend fun getPlaylistsWithSongs(): List<PlaylistWithSongsEntity>
}