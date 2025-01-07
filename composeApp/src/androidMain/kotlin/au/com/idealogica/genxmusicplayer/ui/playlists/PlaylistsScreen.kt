package au.com.idealogica.genxmusicplayer.ui.playlists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlaylistsScreen() {
	val viewModel: PlaylistsViewModel = koinViewModel()

	PlaylistsScreenColumn(

	)
}

@Composable
private fun PlaylistsScreenColumn(
	modifier: Modifier = Modifier
) {
	Column(
		modifier
			.fillMaxSize()
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Text("Hi Tom")
	}
}

@Preview
@Composable
private fun PlaylistsScreenPreview() {
	MaterialTheme {
		Surface {
			PlaylistsScreenColumn(

			)
		}
	}
}