package com.example.photo.ui.compose.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.photo.data.model.Media
import com.example.photo.data.model.MediaHeader
import com.example.photo.data.model.groupMediaByDate
import com.example.photo.ui.viewmodel.HomeViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

@Composable
fun HomeScreen(modifier: Modifier = Modifier, viewModel: HomeViewModel = hiltViewModel()) {

    val context = LocalContext.current
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
    } else {
        listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    //check permissions.
    val permissionsState = remember {
        mutableStateMapOf<String, Boolean>().apply {
            permissions.forEach {
                this[it] = ContextCompat.checkSelfPermission(
                    context,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    // request permission
    val multiplePermissionsLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { permissionResult ->
            permissionResult.forEach { (permission, isGranted) ->
                permissionsState[permission] = isGranted
            }
        }



    Scaffold { paddingValues ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (permissionsState[Manifest.permission.READ_MEDIA_IMAGES] == true || permissionsState[Manifest.permission.READ_MEDIA_VIDEO] == true) {
                HomePage(viewModel = viewModel, modifier = Modifier.padding(paddingValues))
            } else {

                multiplePermissionsLauncher.launch(permissions.toTypedArray())

//                Column(
//                    modifier = Modifier.padding(paddingValues),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    CircularProgressIndicator(
//                        modifier = Modifier.width(64.dp),
//                        color = MaterialTheme.colorScheme.secondary,
//                        trackColor = MaterialTheme.colorScheme.surfaceVariant
//                    )
//                }
            }
        } else {
            if (permissionsState[Manifest.permission.READ_EXTERNAL_STORAGE] == true) {
                HomePage(viewModel = viewModel, modifier = Modifier.padding(paddingValues))
            } else {
                multiplePermissionsLauncher.launch(permissions.toTypedArray())
//                Column(
//                    modifier = Modifier.padding(paddingValues),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    CircularProgressIndicator(
//                        modifier = Modifier.width(64.dp),
//                        color = MaterialTheme.colorScheme.secondary,
//                        trackColor = MaterialTheme.colorScheme.surfaceVariant
//                    )
//                }
            }

        }

    }
}

@Composable
fun HomePage(viewModel: HomeViewModel, modifier: Modifier) {
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
            modifier = Modifier
                .background(Color.LightGray).
            fillMaxHeight()
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
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(128.dp)
                .padding(4.dp)
        )
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
        .padding(16.dp),
        ) {
        Text(text = date, fontSize = 20.sp)
    }
}

@Composable
fun RowMedia(medias: List<Media>) {
    val scrollState = rememberScrollState()
    LazyVerticalGrid(columns = GridCells.Fixed(3)) {
        items(medias) {
            PhotoItem(it)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun previewHomeScreen() {
    HomeScreen()
}