package com.example.photo

import androidx.navigation.NamedNavArgument

sealed class Screen(val route: String, val argument: List<NamedNavArgument> = emptyList()) {
    object Photo: Screen("Email")
}