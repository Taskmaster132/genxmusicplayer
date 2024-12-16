package au.com.idealogica.genxmusicplayer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import au.com.idealogica.genxmusicplayer.model.PlayerState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(platform: Platform) {
	val currentSong by platform.currentSong.collectAsStateWithLifecycle()
	val songs by platform.songs.collectAsStateWithLifecycle()
	val playerState by platform.playerState.collectAsStateWithLifecycle()

	SideEffect {  }
	MaterialTheme {
		Scaffold { padding ->
			Column(
				Modifier
					.fillMaxWidth()
					.padding(padding),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				val songName = currentSong?.song?.name ?: ""
				val label = if (songName.isEmpty()) "No song selected" else "Current song: $songName"
				Text(text = currentSong.toString())
				Spacer(modifier = Modifier.height(16.dp))
				Row(
					modifier = Modifier.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.Center
				) {
					if (playerState == PlayerState.PLAYING) {
						Button(onClick = { platform.pauseSong() }) {
							Icon(imageVector = platform.pauseIcon, contentDescription = "Pause")
						}
					} else {
						Button(onClick = {
							if (playerState == PlayerState.STOPPED) {
								platform.replaySong()
							} else {
								platform.resumeSong()
							}
						}) {
							Icon(imageVector = platform.playIcon, contentDescription = "Play")
						}
					}
					Spacer(modifier = Modifier.width(16.dp))
					Button(onClick = { platform.stopSong() }) {
						Icon(imageVector = platform.stopIcon, contentDescription = "Stop")
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
								onClick = { platform.playSong(playlistSong) },
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
	}
}