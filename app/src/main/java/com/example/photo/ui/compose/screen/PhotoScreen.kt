package com.example.photo.ui.compose.screen

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.photo.data.model.Media
import com.example.photo.data.model.MediaHeader
import com.example.photo.data.model.groupMediaByDate
import com.example.photo.ui.viewmodel.HomeViewModel


@Composable
fun PhotoScreen(viewModel: HomeViewModel = hiltViewModel(), modifier: Modifier = Modifier, onClickPhoto:() -> Unit) {

    val mediaState = viewModel.media.observeAsState()
    if (mediaState.value != null) {
        val groundMediaByDate = groupMediaByDate(mediaState.value!!)
        val listMedia = mutableListOf<MediaHeader>()

        for ((date, media) in groundMediaByDate) {
            listMedia.add(MediaHeader(true, date = date))

            media.forEach {
                listMedia.add(MediaHeader(false, media = it))
                Log.e("MediaHeader", "date : $date , media : ${it.toString()}")
            }
        }

        Column(
            modifier = modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .verticalScroll(rememberScrollState())
        ) {
            for ((date, media) in groundMediaByDate) {
                DateTitle(date = date)
                RowMedia(media)
            }
        }

    } else {
        Log.e("MediaHeader", "Emply Photo")
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) { Text(text = "Emply Photo") }
    }
}


@Composable
fun PhotoItem(media: Media) {
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
                        .padding(4.dp)
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
                .padding(4.dp)
        )
    }
}

fun extractFrameFromVideo(context: Context, videoUri: Uri, frameTimeUs: Long = 0L): Bitmap? {
    val retriever = MediaMetadataRetriever()
    return try {
        // Set the video data source
        retriever.setDataSource(context, videoUri)

        // Retrieve the frame at the specified time
        retriever.getFrameAtTime(frameTimeUs, MediaMetadataRetriever.OPTION_CLOSEST)
    } catch (e: Exception) {
        Log.e("ExtractFrameFromVideo", "Error: ${e.message}")
        null
    } finally {
        // Release the retriever
        retriever.release()
    }
}
@Composable
fun DateTitle(date: String) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 8.dp, bottom = 8.dp, top = 20.dp),
    ) {
        Text(text = date, fontSize = 20.sp)
    }
}

@Composable
fun RowMedia(medias: List<Media>) {
    var countItem = medias.size / 3
    val space = medias.size % 3
    if (space > 0) {
        countItem ++
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .height(countItem * 128.dp)
    ) {
        items(medias) {
            PhotoItem(it)
        }
    }
}
