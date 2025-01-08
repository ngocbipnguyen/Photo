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
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.photo.R
import com.example.photo.data.model.Media
import com.example.photo.data.model.MediaHeader
import com.example.photo.data.model.groupMediaByDate
import com.example.photo.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

enum class PhotoPager(
    @StringRes val titleResId: Int,
    @DrawableRes val drawableResId: Int
) {
    PHOTO_LIST(R.string.photo_title, R.drawable.baseline_insert_photo_32),
    ALBUM_LIST(R.string.photo_album, R.drawable.baseline_photo_album_32),
    EXPLORE_LIST(R.string.explore_title, R.drawable.baseline_explore_32)
}


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onClickItem: (Int, Media) -> Unit,
    onClickItemAlbum: (List<Media>) -> Unit
) {

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
                HomePage(
                    modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
                    onClickItem = onClickItem,
                    onClickItemAlbum = onClickItemAlbum
                )
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
                HomePage(
                    modifier = Modifier.padding(paddingValues), onClickItem = onClickItem,
                    onClickItemAlbum = onClickItemAlbum
                )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    modifier: Modifier,
    pages: Array<PhotoPager> = PhotoPager.values(),
    onClickItem: (Int, Media) -> Unit,
    onClickItemAlbum: (List<Media>) -> Unit
) {

    val pageState = rememberPagerState(pageCount = { pages.size })
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        HorizontalPager(
            state = pageState,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .weight(1f),
            verticalAlignment = Alignment.Top
        ) { index ->
            when (pages[index]) {
                PhotoPager.PHOTO_LIST -> {
                    PhotoScreen(modifier = Modifier.fillMaxSize(), onClickPhoto = {
                        //todo
                        coroutineScope.launch {
                            pageState.scrollToPage(PhotoPager.PHOTO_LIST.ordinal)
                        }
                    }, onClickItem = onClickItem)
                }

                PhotoPager.ALBUM_LIST -> {
                    //todo album screen
                    AlbumScreen(
                        onClickAlbum = {
                            coroutineScope.launch {
                                pageState.scrollToPage(PhotoPager.ALBUM_LIST.ordinal)
                            }
                        },
                        onClickItem = onClickItemAlbum
                    )

                }

                PhotoPager.EXPLORE_LIST -> {
                    //todo explore screen
                    ExploreScreen(onClickExplore = {
                        coroutineScope.launch {
                            pageState.scrollToPage(PhotoPager.EXPLORE_LIST.ordinal)
                        }
                    })
                }
            }
        }

        TabRow(selectedTabIndex = pageState.currentPage) {
            pages.forEachIndexed { index, page ->
                val title = stringResource(id = page.titleResId)
                Tab(
                    selected = pageState.currentPage == index,
                    onClick = { coroutineScope.launch { pageState.animateScrollToPage(index) } },
                    text = { Text(text = title) },
                    icon = {
                        Icon(
                            painter = painterResource(id = page.drawableResId),
                            contentDescription = ""
                        )
                    },
                    unselectedContentColor = MaterialTheme.colorScheme.secondary
                )
            }
        }

    }

//    val mediaState = viewModel.media.observeAsState()
//    if (mediaState.value != null) {
//        val groundMediaByDate = groupMediaByDate(mediaState.value!!)
//        val listMedia = mutableListOf<MediaHeader>()
//
//        for ((date, media) in groundMediaByDate) {
//            listMedia.add(MediaHeader(true, date = date))
//
//            media.forEach {
//                listMedia.add(MediaHeader(false, media = it))
//                Log.e("MediaHeader", "date : $date , media : ${it.toString()}")
//            }
//        }
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .windowInsetsPadding(WindowInsets.statusBars)
//                .verticalScroll(rememberScrollState())
//        ) {
//            for ((date, media) in groundMediaByDate) {
//                DateTitle(date = date)
//                RowMedia(media)
//            }
//        }
//
//    } else {
//        Log.e("MediaHeader", "Emply Photo")
//        Column(
//            modifier = Modifier.padding(16.dp),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) { Text(text = "Emply Photo") }
//    }
}
//
//@Composable
//fun PhotoItem(media: Media) {
//    if (media.mimeType.contains("video/")) {
//        val bitmap = extractFrameFromVideo(LocalContext.current, media.uri)
//        if (bitmap != null) {
//            Box {
//                Image(
//                    bitmap = bitmap.asImageBitmap(),
//                    contentDescription = null,
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier
//                        .size(128.dp)
//                        .padding(4.dp)
//                )
//
//                Icon(
//                    Icons.Rounded.PlayArrow,
//                    contentDescription = "",
//                    modifier = Modifier
//                        .size(40.dp)
//                        .align(
//                            Alignment.Center
//                        )
//                )
//            }
//        }
//    } else {
//        AsyncImage(
//            model = media.uri,
//            contentDescription = null,
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .size(128.dp)
//                .padding(4.dp)
//        )
//    }
//}
//
//fun extractFrameFromVideo(context: Context, videoUri: Uri, frameTimeUs: Long = 0L): Bitmap? {
//    val retriever = MediaMetadataRetriever()
//    return try {
//        // Set the video data source
//        retriever.setDataSource(context, videoUri)
//
//        // Retrieve the frame at the specified time
//        retriever.getFrameAtTime(frameTimeUs, MediaMetadataRetriever.OPTION_CLOSEST)
//    } catch (e: Exception) {
//        Log.e("ExtractFrameFromVideo", "Error: ${e.message}")
//        null
//    } finally {
//        // Release the retriever
//        retriever.release()
//    }
//}
//@Composable
//fun DateTitle(date: String) {
//    Box(modifier = Modifier
//        .fillMaxWidth()
//        .padding(start = 8.dp, bottom = 8.dp, top = 20.dp),
//        ) {
//        Text(text = date, fontSize = 20.sp)
//    }
//}
//
//@Composable
//fun RowMedia(medias: List<Media>) {
//    var countItem = medias.size / 3
//    val space = medias.size % 3
//    if (space > 0) {
//        countItem ++
//    }
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(3),
//        modifier = Modifier
//            .height(countItem * 128.dp)
//    ) {
//        items(medias) {
//            PhotoItem(it)
//        }
//    }
//}

@Preview(showBackground = true)
@Composable
fun previewHomeScreen() {
//    HomeScreen()
}