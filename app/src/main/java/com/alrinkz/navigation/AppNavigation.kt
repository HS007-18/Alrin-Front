package com.alrinkz.navigation

import com.alrinkz.ui.screens.*
import com.alrinkz.ui.viewmodels.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "chat") {
        composable("chat") {
            // ChatScreen(navController)
        }
        composable("integrations") {
            // IntegrationsScreen(navController)
        }
        composable("memory") {
            // MemoryScreen(navController)
        }
        composable("settings") {
            // SettingsScreen(navController)
        }
    }
}