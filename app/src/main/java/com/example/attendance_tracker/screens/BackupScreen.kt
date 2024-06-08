package com.example.attendance_tracker.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.attendance_tracker.model.MTracker
import com.example.attendance_tracker.model.Tracker
import com.example.attendance_tracker.navigation.TrackerScreens
import com.example.attendance_tracker.utils.uuidFromString
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@Preview
@Composable
fun BackupScreen(
    navController: NavController = NavController(LocalContext.current),
    fireviewModel: HomeScreenViewModel = hiltViewModel(),
    trackViewModel: TrackViewModel = hiltViewModel()
    ) {
    val auth = FirebaseAuth.getInstance()
    val User = auth.currentUser?.email?.split("@")?.get(0)
    var listOfTracker = emptyList<MTracker>()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val text = remember {
        mutableStateOf("")
    }
    val enabled = remember {
        mutableStateOf(false)
    }

    if (!fireviewModel.data.value.data.isNullOrEmpty()){
        listOfTracker = fireviewModel.data.value.data!!.toList().filter { mTrack ->
            mTrack.userId == currentUser?.uid.toString()
        }
        Log.d("DataFound" , "HomeContent : ${listOfTracker.toString()}")
    }
    if (fireviewModel.data.value.data.isNullOrEmpty()){
        enabled.value = false
        text.value = "Finding Backup"
    }
    else if (!fireviewModel.data.value.data.isNullOrEmpty()  && listOfTracker.isNullOrEmpty()){
        enabled.value = false
        text.value = "No BackUp was found on this Account." +
                            "\nPlease Continue"
    }else{
        enabled.value = true
        text.value = "BackUp has been found on this Account." +
                 "\nPress Backup to Backup the previous data" +
                  "\nPress continue to continue>> without back up"
    }

    BackUpContent(
        User.toString() ,
        text.value ,
        enabled.value,
        onBackUp =  {
                  trackViewModel.removeAllTrack()
                  listOfTracker.forEach { fireTrack ->
                      trackViewModel.addTrack(Tracker(uuidFromString(fireTrack.id) , fireTrack.subject!! , fireTrack.number!!))
                  }
            navController.navigate(TrackerScreens.HomeScreen.name) {
                popUpTo(TrackerScreens.LoginScreen.name) {
                    inclusive = true
                }
            }
        } ,
        {
            trackViewModel.removeAllTrack()
            navController.navigate(TrackerScreens.HomeScreen.name){
            popUpTo(TrackerScreens.LoginScreen.name) {
                inclusive = true
            }
            }
        }
    )

}

@Composable
fun BackUpContent(
    user : String,
    text : String,
    enabled : Boolean,
    onBackUp: () -> Unit,
    onContinueUp: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "",
            modifier = Modifier.size(150.dp)
        )

        Text(text = "Hi, ${user}",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF56CEC7)
        )
        val lifecycleOwner = LocalLifecycleOwner.current

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = text,
            fontSize = 15.sp,
            modifier = Modifier.padding(4.dp), color = Color(0xFF6F6A6A),
            )

        Spacer(modifier = Modifier.height(30.dp))

        Row(horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF56CEC7),
                    contentColor = Color.White ),
                onClick = { lifecycleOwner.lifecycleScope.launch {
                delay(1000) // Delay for 2000 milliseconds (2 seconds)
                onContinueUp.invoke() }
            }) {
            Text(text = "Continue>>")
            }
            Button(
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF56CEC7),
                    contentColor = Color.White ),
                onClick = {
                    lifecycleOwner.lifecycleScope.launch {
                        delay(2000) // Delay for 2000 milliseconds (2 seconds)
                        onBackUp.invoke()
                    }
                }) {
                Text(text = "BackUp!!")
            }

        }
    }
}
