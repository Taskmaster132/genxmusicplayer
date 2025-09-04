package au.com.idealogica.genxmusicplayer.ui.player.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import au.com.idealogica.genxmusicplayer.model.PopupMenu
import au.com.idealogica.genxmusicplayer.model.Song
import au.com.idealogica.genxmusicplayer.ui.components.ExpandablePlaylistSong
import au.com.idealogica.genxmusicplayer.ui.player.PlayerViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlayerSearchDialog(
	playerViewModel: PlayerViewModel,
	padding: PaddingValues,
	popupMenu: List<PopupMenu>
) {
	val viewModel: PlayerSearchDialogViewModel = koinViewModel()

	val searchStr by viewModel.searchStr.collectAsStateWithLifecycle()
	val visibleSongs by viewModel.visibleSongs.collectAsStateWithLifecycle()

	PlayerSearchColumn(
		padding = padding,
		searchStr = searchStr,
		visibleSongs = visibleSongs,
		popupMenu = popupMenu,
		onAction = { action ->
			when (action) {
				is PlayerSearchDialogActions.SearchStringUpdated -> viewModel.onAction(action)
				PlayerSearchDialogActions.DoneButtonTapped -> playerViewModel.hideSearchDialog()
			}
		}
	)
}

@Composable
private fun PlayerSearchColumn(
	padding: PaddingValues,
	searchStr: String,
	visibleSongs: List<Song>,
	popupMenu: List<PopupMenu>,
	onAction: (PlayerSearchDialogActions) -> Unit
) {
	val listState = rememberLazyListState()
	val keyboardController = LocalSoftwareKeyboardController.current

	Dialog(
		properties = DialogProperties(usePlatformDefaultWidth = false),
		onDismissRequest = { }
	) {
		Card(
			modifier = Modifier
				.fillMaxSize()
				.defaultMinSize(minHeight = 200.dp)
				.padding(padding),
			shape = RoundedCornerShape(4.dp),
			colors = CardDefaults.cardColors(
				containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
			)
		) {
			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(12.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Spacer(modifier = Modifier.height(16.dp))

				OutlinedTextField(
					modifier = Modifier.fillMaxWidth(),
					value = searchStr,
					onValueChange = { onAction(PlayerSearchDialogActions.SearchStringUpdated(it)) },
					placeholder = { Text("Search...") },
					leadingIcon = {
						Icon(
							imageVector = Icons.Default.Search,
							contentDescription = null,
							tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.66f)
						)
					},
					trailingIcon = {
						AnimatedVisibility(visible = searchStr.isNotEmpty()) {
							IconButton(
								onClick = { onAction(PlayerSearchDialogActions.SearchStringUpdated("")) }
							) {
								Icon(
									imageVector = Icons.Default.Close,
									contentDescription = "Clear",
									tint = MaterialTheme.colorScheme.onSurface
								)
							}
						}
					},
					singleLine = true,
					keyboardActions = KeyboardActions(
						onSearch = {
							keyboardController?.hide()
						}
					),
					keyboardOptions = KeyboardOptions(
						keyboardType = KeyboardType.Text,
						imeAction = ImeAction.Search
					)
				)

				Spacer(modifier = Modifier.height(16.dp))

				if (visibleSongs.isEmpty()) {
					Box(
						modifier = Modifier
							.fillMaxWidth()
							.padding(start = 16.dp, end = 16.dp)
							.weight(1f),
						contentAlignment = Alignment.Center
					) {
						if (searchStr.isNotEmpty()) {
							Text(text = "No songs matched your search query")
						} else {
							Text(text = "There appears to be no songs on your device")
						}
					}
				} else {
					LazyColumn(
						modifier = Modifier
							.fillMaxWidth()
							.weight(1f)
							.border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
						state = listState
					) {
						visibleSongs.forEachIndexed { index, song ->
							item(key = song.id) {
								ExpandablePlaylistSong(
									song = song,
									isCurrentlyPlaying = false,
									isFirst = index == 0,
									isLast = index == visibleSongs.lastIndex,
									popupMenu = popupMenu
								)
							}

							if (index <= visibleSongs.lastIndex) {
								item {
									HorizontalDivider()
								}
							}
						}
					}
				}

				Spacer(modifier = Modifier.height(16.dp))

				Button(
					modifier = Modifier.fillMaxWidth(),
					onClick = { onAction(PlayerSearchDialogActions.DoneButtonTapped) }
				) {
					Text(text = "Done")
				}
			}
		}
	}
}

@Preview
@Composable
private fun PlayerSearchDialogPreview() {
	MaterialTheme {
		Surface {
			PlayerSearchColumn(
				padding = PaddingValues(0.dp),
				searchStr = "",
				visibleSongs = List(100) { index -> Song(id = 1, "Song $index", "", "", "Album $index", "Artist $index", 0) },
				popupMenu = emptyList(),
				onAction = {}
			)
		}
	}
}