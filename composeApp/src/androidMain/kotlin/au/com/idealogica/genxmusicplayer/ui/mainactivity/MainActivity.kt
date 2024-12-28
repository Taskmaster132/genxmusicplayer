package au.com.idealogica.genxmusicplayer.ui.mainactivity

import android.Manifest
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import au.com.idealogica.genxmusicplayer.model.PlaylistSong
import au.com.idealogica.genxmusicplayer.model.Song
import au.com.idealogica.genxmusicplayer.service.GenXMusicService
import au.com.idealogica.genxmusicplayer.service.GenXMusicServiceBridge
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : ComponentActivity(), GenXMusicServiceBridge {

	private lateinit var viewModel: MainActivityViewModel

	private var musicService: GenXMusicService? = null
	private val serviceBound = AtomicBoolean(false)

	private var nextSongIndex: Int = 0

	private val serviceConnection = object : ServiceConnection {
		override fun onServiceConnected(name: ComponentName, service: IBinder) {
			val binder = service as GenXMusicService.MusicBinder
			musicService = binder.getService().apply {
				viewModel.loadPlayer(this)
				println("TOM $nextSongIndex")
				viewModel.playSong(nextSongIndex)
			}
		}

		override fun onServiceDisconnected(name: ComponentName) {
			musicService = null
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		enableEdgeToEdge()
		super.onCreate(savedInstanceState)

		viewModel = getViewModel<MainActivityViewModel>().apply {
			setBridge(this@MainActivity)
		}

		doSomething()

		setContent {
			MaterialTheme {
				Scaffold { padding ->
					MainActivityScreen(
						viewModel = viewModel,
						padding = padding
					)
				}
			}
		}
	}

	private fun bindService() {
		val intent = Intent(this, GenXMusicService::class.java)
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
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

		viewModel.updateSongs(songs)
	}

	override fun playSong(playlistSong: PlaylistSong) {
		if (serviceBound.compareAndSet(false, true)) {
			println("TOM: ${playlistSong.index}")
			nextSongIndex = playlistSong.index
			bindService()
		} else {
			viewModel.playSong(playlistSong.index)
		}
	}

	override fun pauseSong() {
		if (musicService?.pauseSong() == true) {
		}
	}

	override fun resumeSong() {
		if (musicService?.resumeSong() == true) {
		}
	}

	override fun stopSong() {
		if (musicService?.stopSong() == true) {
		}
	}

	companion object {
		private const val MEDIA_REQUEST_CODE = 111
	}
}
