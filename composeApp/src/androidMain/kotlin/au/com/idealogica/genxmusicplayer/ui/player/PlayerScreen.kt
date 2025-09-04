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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
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
import au.com.idealogica.genxmusicplayer.model.CurrentPlaylistSong
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
	val shuffled by mainActivityViewModel.shuffled.collectAsStateWithLifecycle()
	val showSearchDialog by viewModel.showSearchDialog.collectAsStateWithLifecycle()

	PlayerScreenColumn(
		player = player,
		songs = currentPlaylist,
		currentSongIndex = currentSongIndex,
		currentPlaylistName = currentPlaylistName,
		shuffled = shuffled,
		onAction = { action ->
			when (action) {
				PlayerScreenActions.ClearTapped -> mainActivityViewModel.clearPlaylist()
				PlayerScreenActions.AddTapped -> viewModel.searchTapped()
				is PlayerScreenActions.ShuffleTapped -> mainActivityViewModel.toggleShuffle(action.shuffle)
				is PlayerScreenActions.PlayTapped -> mainActivityViewModel.playSong(action.index)
			}
		}
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
	songs: List<CurrentPlaylistSong>,
	currentSongIndex: Int,
	currentPlaylistName: String,
	shuffled: Boolean,
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
					onAction(PlayerScreenActions.AddTapped)
				}
			) {
				Icon(
					imageVector = Icons.Default.Add,
					contentDescription = "Add songs to playlist"
				)
			}

			if (songs.isNotEmpty()) {
				IconToggleButton(
					checked = shuffled,
					onCheckedChange = { checked ->
						onAction(PlayerScreenActions.ShuffleTapped(checked))
					}
				) {
					Icon(
						imageVector = Icons.Default.Shuffle,
						contentDescription = "Shuffle"
					)
				}

				IconButton(
					onClick = {
						onAction(PlayerScreenActions.ClearTapped)
					}
				) {
					Icon(
						imageVector = Icons.Default.Clear,
						contentDescription = "Clear"
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
				Text(text = "Add songs to play manually or select a playlist from the 'Playlists' tab")
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
						val popupMenu = listOf(
							PopupMenu(
								label = "Play",
								callback = { _ -> onAction(PlayerScreenActions.PlayTapped(index)) }
							)
						)

						ExpandablePlaylistSong(
							modifier = modifier,
							song = playlistSong.song,
							isCurrentlyPlaying = index == currentSongIndex,
							isFirst = index == 0,
							isLast = index == songs.lastIndex,
							popupMenu = popupMenu
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
				songs = List(100) { index -> CurrentPlaylistSong(Song(id = 1, "Song $index", "", "", "Album $index", "Artist $index", 0)) },
				currentSongIndex = 2,
				currentPlaylistName = "All songs on device",
				shuffled = false,
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
				songs = List(100) { index -> CurrentPlaylistSong(Song(id = 1, "Song $index", "", "", "Album $index", "Artist $index", 0)) },
				currentSongIndex = 2,
				currentPlaylistName = "All songs on device",
				shuffled = false,
				onAction = {}
			)
		}
	}
}