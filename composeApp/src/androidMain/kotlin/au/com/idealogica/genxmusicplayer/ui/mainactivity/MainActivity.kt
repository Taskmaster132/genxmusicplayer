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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import au.com.idealogica.genxmusicplayer.model.Song
import au.com.idealogica.genxmusicplayer.repository.GenXMusicRepository
import au.com.idealogica.genxmusicplayer.service.GenXDeviceService
import au.com.idealogica.genxmusicplayer.service.GenXMusicService
import au.com.idealogica.genxmusicplayer.service.NativeMusicService
import au.com.idealogica.genxmusicplayer.ui.navigation.GenXRoutes
import au.com.idealogica.genxmusicplayer.ui.navigation.TopLevelRoute
import au.com.idealogica.genxmusicplayer.ui.player.PlayerScreen
import au.com.idealogica.genxmusicplayer.ui.player.PlayerViewModel
import au.com.idealogica.genxmusicplayer.ui.playlists.PlaylistsScreen
import au.com.idealogica.genxmusicplayer.ui.theme.GenXMusicPlayerTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.koin.android.ext.android.inject
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity(), GenXDeviceService, NativeMusicService {

	private val genxMusicRepository: GenXMusicRepository by inject()
	private val mainActivityViewModel: MainActivityViewModel by inject()
	private var musicService: GenXMusicService? = null

	private val _allSongsOnDevice = MutableStateFlow<List<Song>>(emptyList())
	override val allSongsOnDevice = _allSongsOnDevice
		.onStart {
			loadAllSongsOnDevice()
		}
		.stateIn(
			scope = lifecycleScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = emptyList()
		)

	private val serviceConnection = object : ServiceConnection {
		override fun onServiceConnected(name: ComponentName, service: IBinder) {
			val binder = service as GenXMusicService.MusicBinder
			musicService = binder.getService().apply {
				mainActivityViewModel.serviceBound(this)
			}
		}

		override fun onServiceDisconnected(name: ComponentName) {
			musicService = null
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		mainActivityViewModel.deviceService = this
		genxMusicRepository.initGenXMusicService(this)

		val topLevelRoutes = listOf(
			TopLevelRoute("Player", GenXRoutes.Player, Icons.AutoMirrored.Filled.QueueMusic),
			TopLevelRoute("Playlists", GenXRoutes.Playlists, Icons.AutoMirrored.Filled.PlaylistAdd)
		)

		enableEdgeToEdge()
		setContent {
			val navController = rememberNavController()

			GenXMusicPlayerTheme {
				Scaffold(
					modifier = Modifier.fillMaxSize(),
					bottomBar = {
						BottomAppBar {
							NavigationBar {
								val navBackStackEntry by navController.currentBackStackEntryAsState()
								val currentDestination = navBackStackEntry?.destination

								topLevelRoutes.forEach { screen ->
									NavigationBarItem(
										icon = { Icon(screen.icon, contentDescription = null) },
										label = { Text(screen.name) },
										selected = currentDestination?.hierarchy?.any { it.hasRoute(screen.route::class) } == true,
										onClick = {
											navController.navigate(screen.route) {
												// Pop up to the start destination of the graph to
												// avoid building up a large stack of destinations
												// on the back stack as users select items
												popUpTo(navController.graph.findStartDestination().id) {
													saveState = true
												}
												// Avoid multiple copies of the same destination when
												// reselecting the same item
												launchSingleTop = true
												// Restore state when reselecting a previously selected item
												restoreState = true
											}
										}
									)
								}
							}
						}
					}
				) { innerPadding ->
					NavHost(
						modifier = Modifier.padding(innerPadding),
						navController = navController,
						startDestination = GenXRoutes.GenXGraph
					) {
						navigation<GenXRoutes.GenXGraph>(
							startDestination = GenXRoutes.Player
						) {
							composable<GenXRoutes.Player> {
								val viewModel = koinViewModel<PlayerViewModel>()

								PlayerScreen(
									mainActivityViewModel = mainActivityViewModel,
									viewModel = viewModel,
									padding = innerPadding
								)
							}
							composable<GenXRoutes.Playlists> { PlaylistsScreen() }
						}
					}
				}
			}
		}
	}

	override fun bindService() {
		val intent = Intent(this, GenXMusicService::class.java)
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
	}

	private fun loadAllSongsOnDevice() {
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
				val title = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
				val path = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
				val cdTrackNumber = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.CD_TRACK_NUMBER))
				val album = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
				val artist = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
				val duration = it.getInt(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))

				songs.add(
					Song(
						name = title,
						path = path,
						cdTrackNumber = cdTrackNumber ?: "",
						album = album ?: "",
						artist = artist ?: "",
						duration = duration
					)
				)
			}
		}

		_allSongsOnDevice.update { songs }
	}

	companion object {
		private const val MEDIA_REQUEST_CODE = 111
	}
}
