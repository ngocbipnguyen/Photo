package com.example.photo.ui.compose.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.photo.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(modifier: Modifier = Modifier, viewModel: HomeViewModel = hiltViewModel()) {

    Scaffold { paddingValues ->
        HomePage(viewModel = viewModel, modifier = Modifier.padding(paddingValues))
    }

}

@Composable
fun HomePage(viewModel: HomeViewModel, modifier: Modifier) {

}