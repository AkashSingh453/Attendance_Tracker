package com.example.attendance_tracker.screens

import android.view.animation.OvershootInterpolator
import android.window.SplashScreen
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendance_tracker.Components.TrackerLogo
import com.example.attendance_tracker.navigation.TrackerScreens
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController  = NavController(LocalContext.current) ){
    val scale = remember{
        Animatable(0f)
    }
    LaunchedEffect(key1 = true){
        scale.animateTo(
            targetValue = 0.9f,
            animationSpec = tween(durationMillis = 800,
                easing = {
                    OvershootInterpolator(8f)
                        .getInterpolation(it)
                })
        )
        delay(2000L)
        if (FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()){
            navController.navigate(TrackerScreens.LoginScreen.name){
                popUpTo(TrackerScreens.SplashScreen.name ){
                    inclusive = true
                }
            }
        }
        else {
            navController.navigate(TrackerScreens.HomeScreen.name){
                popUpTo(TrackerScreens.SplashScreen.name ){
                    inclusive = true
                }
            }
        }
    }
    Column(modifier = Modifier.fillMaxSize().background(color = Color(0xFFF1F8E9)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(modifier = Modifier
            .padding(15.dp)
            .height(600.dp)
            .scale(scale.value)
            .size(300.dp),
            shape = RoundedCornerShape(1200.dp),
            color = Color(0xFFF3FCE8),
            border = BorderStroke( width = 2.dp, color = Color.LightGray)
        ) {
            Column(
                modifier = Modifier.padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TrackerLogo()
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Red.copy(alpha = 0.5f)
                )
            }
        }
    }
}

