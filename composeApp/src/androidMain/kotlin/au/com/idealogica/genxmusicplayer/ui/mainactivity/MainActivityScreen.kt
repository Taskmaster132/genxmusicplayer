package au.com.idealogica.genxmusicplayer.ui.mainactivity

import android.graphics.Color
import androidx.annotation.OptIn
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import au.com.idealogica.genxmusicplayer.R
import au.com.idealogica.genxmusicplayer.model.PlaylistSong

@OptIn(UnstableApi::class)
@Composable
fun MainActivityScreen(
	viewModel: MainActivityViewModel,
	padding: PaddingValues
) {
	val player by viewModel.player.collectAsStateWithLifecycle()
	val songs by viewModel.songs.collectAsStateWithLifecycle()
	val currentSong by viewModel.currentSong.collectAsStateWithLifecycle()

	MainActivityScreenColumn(
		padding = padding,
		player = player,
		songs = songs,
		currentSong = currentSong,
		onEvent = viewModel::onEvent
	)
}

@UnstableApi
@Composable
private fun MainActivityScreenColumn(
	padding: PaddingValues,
	player: Player?,
	songs: List<PlaylistSong>,
	currentSong: PlaylistSong?,
	onEvent: (MainActivityScreenEvents) -> Unit
) {
	Column(
		Modifier
			.fillMaxWidth()
			.padding(
				start = padding.calculateStartPadding(LocalLayoutDirection.current),
				end = padding.calculateEndPadding(LocalLayoutDirection.current)
			),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(padding.calculateTopPadding())
				.background(color = colorResource(R.color.genx_black)),
		)
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.heightIn(max = 400.dp)
				.background(color = colorResource(R.color.genx_black)),
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
							setBackgroundColor(Color.BLACK)
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

		LazyColumn(
			modifier = Modifier
				.fillMaxWidth()
		) {
			songs.forEachIndexed { index, playlistSong ->
				item(key = playlistSong.song.id) {
					if (index < 1) {
						HorizontalDivider(modifier = Modifier.fillMaxWidth())
					}
					TextButton(
						modifier = Modifier.fillMaxWidth(),
						onClick = { onEvent(MainActivityScreenEvents.SongTapped(playlistSong)) },
					) {
						Text(
							modifier = Modifier
								.padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 4.dp),
							text = playlistSong.song.name,
						)
					}
					HorizontalDivider(modifier = Modifier.fillMaxWidth())
				}
			}
		}
	}
}

@OptIn(UnstableApi::class)
@Preview
@Composable
fun MainActivityScreenPreview() {
	val context = LocalContext.current
	MaterialTheme {
		Surface {
			MainActivityScreenColumn(
				padding = PaddingValues(0.dp),
				player = ExoPlayer.Builder(context).build(),
				songs = emptyList(),
				currentSong = null,
				onEvent = {}
			)
		}
	}
}