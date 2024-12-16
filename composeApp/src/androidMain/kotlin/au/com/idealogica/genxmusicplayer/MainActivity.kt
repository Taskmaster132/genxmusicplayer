package au.com.idealogica.genxmusicplayer

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import au.com.idealogica.genxmusicplayer.model.PlayerState
import au.com.idealogica.genxmusicplayer.model.PlaylistSong
import au.com.idealogica.genxmusicplayer.model.Song
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException

class MainActivity : ComponentActivity() {

	private val mediaPlayer: ExoPlayer by lazy { ExoPlayer.Builder(this).build() }
	private val platform: AndroidPlatform by lazy { AndroidPlatform(this) }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		doSomething()

		mediaPlayer.addListener(object: Player.Listener {
			override fun onPlaybackStateChanged(playbackState: Int) {
				if (playbackState == Player.STATE_ENDED) {
					platform.playNextSong()
				}
			}
		})

		setContent {
			App(platform)
		}
	}

	private fun doSomething() {
		if (permissionHasBeenGranted()) {
			accessMediaStore()
			return
		}

		val permissions = getArrayOfPermissionsToRequest()
		if (shouldShowRationale()) {
			AlertDialog.Builder(this)
				.setTitle("Permission Required")
				.setMessage("This app needs access to your external storage to read music files.")
				.setPositiveButton("OK") { _, _ ->
					// Request the permission
					ActivityCompat.requestPermissions(
						this,
						permissions,
						MEDIA_REQUEST_CODE
					)
				}
				.setNegativeButton("Cancel", null)
				.show()
			return
		}

		ActivityCompat.requestPermissions(this, permissions, MEDIA_REQUEST_CODE)
	}

	private fun permissionHasBeenGranted(): Boolean {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
		} else {
			ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
		}
	}

	private fun shouldShowRationale(): Boolean {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_AUDIO)
		} else {
			ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
		}
	}

	private fun getArrayOfPermissionsToRequest(): Array<String> {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
		} else {
			arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
		}
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray, deviceId: Int) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)

		if (requestCode == MEDIA_REQUEST_CODE) {
			if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				accessMediaStore()
			}
		}
	}

	private fun accessMediaStore() {
		val contentResolver = contentResolver
		val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
		val cursor = contentResolver.query(uri, null, null, null, null)

		val songs = mutableListOf<Song>()
		cursor?.use {
			while (it.moveToNext()) {
				val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
				val title = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
				val path = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
				val cdTrackNumber = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.CD_TRACK_NUMBER))
				val album = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
				val artist = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
				val author = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.AUTHOR))
				val composer = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPOSER))
				val genre = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
					it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE))
				} else {
					"Unknown"
				}
				val year = it.getInt(it.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR))
				val duration = it.getInt(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))

				songs.add(
					Song(
						id = id,
						name = title,
						path = path,
						cdTrackNumber = cdTrackNumber ?: "",
						album = album ?: "",
						artist = artist ?: "",
						author = author ?: "",
						composer = composer ?: "",
						genre = genre ?: "",
						year = year,
						duration = duration
					)
				)
			}
		}

		platform.updateSongs(songs)
	}

	fun playSong(playlistSong: PlaylistSong) {
		try {
			val mediaItem = MediaItem.fromUri(playlistSong.song.path)
			mediaPlayer.setMediaItem(mediaItem)
			mediaPlayer.prepare()
			mediaPlayer.play()
			platform.updateCurrentSong(playlistSong)
			platform.updatePlaying(PlayerState.PLAYING)
		} catch (e: IOException) {
			println("Error playing song: $e")
		}
	}

	fun pauseSong() {
		if (mediaPlayer.isPlaying) {
			mediaPlayer.pause()
			platform.updatePlaying(PlayerState.PAUSED)
		}
	}

	fun resumeSong() {
		if (!mediaPlayer.isPlaying) {
			mediaPlayer.play()
			platform.updatePlaying(PlayerState.PLAYING)
		}
	}

	fun stopSong() {
		if (mediaPlayer.isPlaying) {
			mediaPlayer.stop()
			platform.updatePlaying(PlayerState.STOPPED)
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		mediaPlayer.release()
	}

	companion object {
		private const val MEDIA_REQUEST_CODE = 111
	}
}

@Preview
@Composable
fun AppAndroidPreview() {
	App(
		object : Platform {
			override val currentSong: StateFlow<PlaylistSong?>
				get() = TODO("Not yet implemented")
			override val songs: StateFlow<List<PlaylistSong>>
				get() = TODO("Not yet implemented")
			override val playerState: StateFlow<PlayerState>
				get() = TODO("Not yet implemented")
			override val playSong: (PlaylistSong) -> Unit
				get() = TODO("Not yet implemented")
			override val replaySong: () -> Unit
				get() = TODO("Not yet implemented")
			override val pauseSong: () -> Unit
				get() = TODO("Not yet implemented")
			override val resumeSong: () -> Unit
				get() = TODO("Not yet implemented")
			override val stopSong: () -> Unit
				get() = TODO("Not yet implemented")
			override val playIcon: ImageVector
				get() = Icons.Filled.PlayArrow
			override val pauseIcon: ImageVector
				get() = Icons.Filled.Pause
			override val stopIcon: ImageVector
				get() = Icons.Filled.Stop
		}
	)
}