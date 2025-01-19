package au.com.idealogica.genxmusicplayer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import au.com.idealogica.genxmusicplayer.model.PopupMenu
import au.com.idealogica.genxmusicplayer.model.Song

@Composable
fun ExpandablePlaylistSong(
	modifier: Modifier = Modifier,
	song: Song,
	isCurrentlyPlaying: Boolean,
	isFirst: Boolean,
	isLast: Boolean,
	popupMenu: List<PopupMenu>
) {
	var expanded by remember { mutableStateOf(false) }
	var menuShowing by remember { mutableStateOf(false) }

	val (backgroundColour, textColour) = if (isCurrentlyPlaying) {
		Pair(MaterialTheme.colorScheme.inverseSurface, MaterialTheme.colorScheme.inverseOnSurface)
	} else {
		Pair(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.onSurface)
	}

	val topRadius = if (isFirst) 8.dp else 0.dp
	val bottomRadius = if (isLast) 8.dp else 0.dp

	Box(
		modifier = modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(topStart = topRadius, topEnd = topRadius, bottomStart = bottomRadius, bottomEnd = bottomRadius)),
	) {
		Column(
			modifier = modifier
				.fillMaxWidth()
				.background(backgroundColour)
				.padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
				.clickable {
					menuShowing = true
				}
		) {
			Row(
				modifier = modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					modifier = modifier
						.weight(1f)
						.padding(end = 8.dp),
					text = song.name,
					color = textColour
				)

				if (expanded) {
					IconButton(
						modifier = modifier.size(48.dp),
						onClick = { expanded = false }
					) {
						Icon(
							imageVector = Icons.Default.KeyboardArrowUp,
							contentDescription = "Collapse",
							tint = textColour
						)
					}
				} else {
					IconButton(
						modifier = modifier.size(48.dp),
						onClick = { expanded = true }
					) {
						Icon(
							imageVector = Icons.Default.KeyboardArrowDown,
							contentDescription = "Collapse",
							tint = textColour
						)
					}
				}
			}

			if (expanded) {
				Text(
					modifier = modifier.padding(start = 16.dp, end = 16.dp),
					text = "Artist: ${song.artist}",
					color = textColour
				)

				Text(
					modifier = modifier.padding(start = 16.dp, end = 16.dp),
					text = "Album: ${song.album}",
					color = textColour
				)
			}
		}

		DropdownMenu(
			modifier = modifier,
			expanded = menuShowing,
			onDismissRequest = { menuShowing = false},
			containerColor = MaterialTheme.colorScheme.inverseSurface
		) {
			popupMenu.map { menu ->
				DropdownMenuItem(
					modifier = modifier,
					onClick = {
						menuShowing = false
						menu.callback(song)
					},
					text = {
						Text(
							text = menu.label,
							color = MaterialTheme.colorScheme.inverseOnSurface
						)
					}
				)
			}
		}
	}
}

@Preview
@Composable
fun ExpandablePlaylistSongPreview() {
	MaterialTheme {
		Surface {
			Column {
				ExpandablePlaylistSong(
					song = Song(
						name = "Lady Madonna",
						path = "",
						artist = "The Beatles",
						cdTrackNumber = "",
						album = "1",
						duration = 180000
					),
					isCurrentlyPlaying = false,
					isFirst = true,
					isLast = false,
					popupMenu = emptyList()
				)
				ExpandablePlaylistSong(
					song = Song(
						name = "Let it be",
						path = "",
						artist = "The Beatles",
						cdTrackNumber = "",
						album = "1",
						duration = 180000
					),
					isCurrentlyPlaying = true,
					isFirst = false,
					isLast = false,
					popupMenu = emptyList()
				)
				ExpandablePlaylistSong(
					song = Song(
						name = "Yesterday",
						path = "",
						artist = "The Beatles",
						cdTrackNumber = "",
						album = "1",
						duration = 180000
					),
					isCurrentlyPlaying = false,
					isFirst = false,
					isLast = true,
					popupMenu = emptyList()
				)
			}
		}
	}
}