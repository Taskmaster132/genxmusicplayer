package au.com.idealogica.genxmusicplayer.ui.player

import android.content.res.Configuration
import androidx.annotation.OptIn
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import au.com.idealogica.genxmusicplayer.R
import au.com.idealogica.genxmusicplayer.model.PlaylistSong
import au.com.idealogica.genxmusicplayer.model.PopupMenu
import au.com.idealogica.genxmusicplayer.model.Song
import au.com.idealogica.genxmusicplayer.ui.components.ExpandablePlaylistSong
import au.com.idealogica.genxmusicplayer.ui.mainactivity.MainActivityViewModel
import au.com.idealogica.genxmusicplayer.ui.player.search.PlayerSearchDialog
import au.com.idealogica.genxmusicplayer.ui.theme.GenXMusicPlayerTheme

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
	mainActivityViewModel: MainActivityViewModel,
	viewModel: PlayerViewModel,
	padding: PaddingValues
) {
	val player by mainActivityViewModel.player.collectAsStateWithLifecycle()
	val currentPlaylist by mainActivityViewModel.currentPlaylist.collectAsStateWithLifecycle()
	val currentSongIndex by mainActivityViewModel.currentSongIndex.collectAsStateWithLifecycle()
	val currentPlaylistName by mainActivityViewModel.currentPlaylistName.collectAsStateWithLifecycle()
	val showSearchDialog by viewModel.showSearchDialog.collectAsStateWithLifecycle()

	PlayerScreenColumn(
		player = player,
		songs = currentPlaylist,
		currentSongIndex = currentSongIndex,
		currentPlaylistName = currentPlaylistName,
		onAction = viewModel::handleUserAction
	)

	if (showSearchDialog) {
		val popupMenu = listOf(
			PopupMenu(
				label = "Play now",
				callback = { song -> mainActivityViewModel.addSongToPlayListAndPlayImmediately(song) }
			),
			PopupMenu(
				label = "Play next",
				callback = { song -> mainActivityViewModel.insertSongAsNextSongInPlaylist(song) }
			),
			PopupMenu(
				label = "Add to playlist",
				callback = { song -> mainActivityViewModel.addSongToPlaylist(song) }
			)
		)

		PlayerSearchDialog(
			mainActivityViewModel = mainActivityViewModel,
			playerViewModel = viewModel,
			padding = padding,
			popupMenu = popupMenu
		)
	}
}

@UnstableApi
@Composable
private fun PlayerScreenColumn(
	modifier: Modifier = Modifier,
	player: Player?,
	songs: List<PlaylistSong>,
	currentSongIndex: Int,
	currentPlaylistName: String,
	onAction: (PlayerScreenActions) -> Unit
) {
	val colourScheme = MaterialTheme.colorScheme

	val listState = rememberLazyListState()

	LaunchedEffect(currentSongIndex) {
		if (currentSongIndex >= 0) {
			listState.animateScrollToItem(currentSongIndex)
		}
	}

	Column(
		Modifier.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.heightIn(max = 200.dp),
			contentAlignment = Alignment.Center
		) {
			if (player != null) {
				AndroidView(
					factory = { context ->
						PlayerView(context).apply {
							controllerHideOnTouch = false
							controllerAutoShow = false
							showController()
							controllerShowTimeoutMs = 0
							setPlayer(player)
							defaultArtwork = AppCompatResources.getDrawable(context, R.drawable.notification_icon)
							setShowRewindButton(true)
							setShowFastForwardButton(true)
							setShowNextButton(true)
							setShowPreviousButton(true)
							setBackgroundColor(colourScheme.surface.toArgb())
							setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
						}
					}
				)
			} else {
				Icon(
					painter = painterResource(R.drawable.notification_icon),
					contentDescription = null,
					modifier = Modifier.fillMaxSize()
				)
			}
		}

		Spacer(modifier = Modifier.height(16.dp))

		Text(text = currentPlaylistName)

		Spacer(modifier = Modifier.height(16.dp))

		Row(
			modifier = Modifier.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.End
		) {
			IconButton(
				onClick = {
					onAction(PlayerScreenActions.SearchTapped)
				}
			) {
				Icon(
					imageVector = Icons.Default.Search,
					contentDescription = null
				)
			}

			if (songs.isNotEmpty()) {
				IconButton(
					onClick = {
						onAction(PlayerScreenActions.ShuffleTapped)
					}
				) {
					Icon(
						imageVector = Icons.Default.Shuffle,
						contentDescription = null
					)
				}

				IconButton(
					onClick = {
						onAction(PlayerScreenActions.SortTapped)
					}
				) {
					Icon(
						imageVector = Icons.AutoMirrored.Filled.Sort,
						contentDescription = null
					)
				}

				IconButton(
					onClick = {
						onAction(PlayerScreenActions.ClearTapped)
					}
				) {
					Icon(
						imageVector = Icons.Default.Clear,
						contentDescription = null
					)
				}
			}
		}

		if (songs.isEmpty()) {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.padding(start = 16.dp, end = 16.dp)
					.weight(1f),
				contentAlignment = Alignment.Center
			) {
				Text(text = "Select songs to play in the 'Playlists' tab")
			}
		} else {
			LazyColumn(
				modifier = Modifier
					.fillMaxWidth()
					.padding(start = 16.dp, end = 16.dp)
					.border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
				state = listState
			) {
				songs.forEachIndexed { index, playlistSong ->
					item(key = playlistSong.id) {
						ExpandablePlaylistSong(
							modifier = modifier,
							song = playlistSong.song,
							isCurrentlyPlaying = index == currentSongIndex,
							popupMenu = emptyList()
						)
					}

					if (index <= songs.lastIndex) {
						item {
							HorizontalDivider()
						}
					}
				}
			}
		}
	}
}

@OptIn(UnstableApi::class)
@Preview
@Composable
private fun PlayerScreenPreview() {
	GenXMusicPlayerTheme {
		Surface {
			PlayerScreenColumn(
				player = null,
				songs = List(100) { index -> PlaylistSong(Song("Song $index", "", "", "Album $index", "Artist $index", 0)) },
				currentSongIndex = 2,
				currentPlaylistName = "All songs on device",
				onAction = {}
			)
		}
	}
}

@OptIn(UnstableApi::class)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PlayerScreenDarkPreview() {
	GenXMusicPlayerTheme {
		Surface {
			PlayerScreenColumn(
				player = null,
				songs = List(100) { index -> PlaylistSong(Song("Song $index", "", "", "Album $index", "Artist $index", 0)) },
				currentSongIndex = 2,
				currentPlaylistName = "All songs on device",
				onAction = {}
			)
		}
	}
}