package com.example.photo

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.photo.ui.compose.screen.HomeScreen

@Composable
fun PhotoApp() {
    val navigation = rememberNavController()
    ReplyNavHost(navigation)
}

@Composable
fun ReplyNavHost(navHost: NavHostController) {
    NavHost(navController = navHost, startDestination = Screen.Photo.route) {
        composable(route =  Screen.Photo.route) {
            HomeScreen()
        }
    }
}