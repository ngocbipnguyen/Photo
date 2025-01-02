package com.example.photo.ui.compose.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.photo.ui.viewmodel.HomeViewModel

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
    val mediaState = viewModel.media
}