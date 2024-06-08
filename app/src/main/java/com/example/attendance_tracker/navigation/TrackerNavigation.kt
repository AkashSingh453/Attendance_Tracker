package com.example.attendance_tracker.navigation

import android.window.SplashScreen
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.attendance_tracker.screens.BackupScreen
import com.example.attendance_tracker.screens.HomeScreen
import com.example.attendance_tracker.screens.HomeScreenViewModel
import com.example.attendance_tracker.screens.LoginScreen
import com.example.attendance_tracker.screens.LoginViewModel
import com.example.attendance_tracker.screens.SettingsScreen
import com.example.attendance_tracker.screens.SplashScreen
import com.example.attendance_tracker.screens.TrackViewModel

@Composable
fun TrackerNavigation() {
    val navController= rememberNavController()
    NavHost(navController = navController ,startDestination = TrackerScreens.SplashScreen.name)
    {

        composable(TrackerScreens.SplashScreen.name){
            SplashScreen(navController)
        }

        composable(TrackerScreens.HomeScreen.name){
            val trackerViewModel = hiltViewModel<TrackViewModel>()
            HomeScreen(navController,trackerViewModel)
        }
        composable(TrackerScreens.SettingsScreen.name){
            val trackerViewModel = hiltViewModel<TrackViewModel>()
            val homeScreenViewModel = hiltViewModel<HomeScreenViewModel>()
            SettingsScreen(navController,trackerViewModel,homeScreenViewModel)
        }
        composable(TrackerScreens.LoginScreen.name){
            val loginViewModel = hiltViewModel<LoginViewModel> ()
            LoginScreen(navController)
        }
        composable(TrackerScreens.BackupScreen.name){
            BackupScreen(navController)
        }
    }
}