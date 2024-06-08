package com.example.attendance_tracker.navigation

enum class TrackerScreens {
    SplashScreen,
    LoginScreen,
    HomeScreen,
    SettingsScreen,
    BackupScreen;
    companion object{
        fun fromRoute(route:String?): TrackerScreens
                = when(route?.substringBefore("/")){
            SplashScreen.name -> SplashScreen
            LoginScreen.name -> LoginScreen
            HomeScreen.name -> HomeScreen
            SettingsScreen.name -> SettingsScreen
            BackupScreen.name -> BackupScreen

            null -> HomeScreen
            else -> throw IllegalArgumentException("Route $route is not recognised")

        }
    }
}