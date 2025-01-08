package com.example.photo.ui.compose.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.photo.data.model.Media
import com.example.photo.data.model.MediaHeader
import com.example.photo.data.model.groupMediaByAlbum
import com.example.photo.ui.viewmodel.AlbumViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlbumScreen(
    modifier: Modifier = Modifier,
    viewModel: AlbumViewModel = hiltViewModel(),
    onClickAlbum: () -> Unit,
    onClickItem: (List<Media>) -> Unit
) {
    val mediaState = viewModel.media.observeAsState()
    if (mediaState.value != null) {
        val groupMediaByAlbum = groupMediaByAlbum(mediaState.value!!)
        val listMedia = mutableListOf<MediaHeader>()

        for ((date, media) in groupMediaByAlbum) {
            listMedia.add(MediaHeader(true, date = date, media = media[0], count = media.size))
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2), modifier = modifier.fillMaxSize()
        ) {
            items(listMedia) {
                AlbumItem(
                    title = it.date.toString(),
                    media = it.media!!,
                    count = it.count,
                    Modifier.combinedClickable(
                        onClick = {
                            //todo get album.
                            val albums = mediaState.value!!.filter { media ->
                                media.album ==  it.date
                            }
                            onClickItem(albums)
                        },
                    )
                )
            }
        }
    }
}


@Composable
fun AlbumItem(title: String, media: Media, count: Int, modifier: Modifier = Modifier) {
    Column(modifier = Modifier
        .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween

    ) {
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
        ) {
            if (media.mimeType.contains("video/")) {
                val bitmap = extractFrameFromVideo(LocalContext.current, media.uri)
                if (bitmap != null) {
                    Box {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(128.dp)
                        )

                        Icon(
                            Icons.Rounded.PlayArrow,
                            contentDescription = "",
                            modifier = Modifier
                                .size(40.dp)
                                .align(
                                    Alignment.Center
                                )
                        )
                    }
                }
            } else {
                AsyncImage(
                    model = media.uri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(128.dp)
                )
            }
        }

        Text(text = title, textAlign = TextAlign.Start, fontSize = 18.sp)

        Text(text = count.toString(), textAlign = TextAlign.Start)
    }
}