package com.example.attendance_tracker.screens


import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.attendance_tracker.R
import com.example.attendance_tracker.cancelNotification
import com.example.attendance_tracker.model.MTracker
import com.example.attendance_tracker.model.Tracker
import com.example.attendance_tracker.navigation.TrackerScreens
import com.example.attendance_tracker.sendNotification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.PropertyName
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: TrackViewModel = hiltViewModel(),
    fireviewModel: HomeScreenViewModel = hiltViewModel()
    ) {
    val auth = FirebaseAuth.getInstance()
    val User = auth.currentUser?.email?.split("@")?.get(0)

    var trackList = viewModel.TrackList.collectAsState().value

    var listOfTracker = emptyList<MTracker>()
    val currentUser = FirebaseAuth.getInstance().currentUser

    if (!fireviewModel.data.value.data.isNullOrEmpty()){
        listOfTracker = fireviewModel.data.value.data!!.toList().filter { mTrack ->
            mTrack.userId == currentUser?.uid.toString()
        }
        Log.d("Books" , "HomeContent : ${listOfTracker.toString()}")
    }
    val newData = remember {
        mutableStateOf(false)
    }
    val lifecycleOwner = LocalLifecycleOwner.current
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
        Text(text = "hi, ${User.toString()}",
            fontSize = 30.sp,
            color = Color(0xFF56CEC7)
            )
        Spacer(modifier = Modifier.height(30.dp))
        Row(horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            val new_Data = remember {
                mutableStateOf(false)
            }
            SyncButton(fireviewModel, trackList, new_Data, listOfTracker, navController)
            Spacer(modifier = Modifier.width(20.dp))
            Button(
                enabled = fireviewModel.data.value.data!!.isNotEmpty(),
                onClick = {
                    lifecycleOwner.lifecycleScope.launch {

                        listOfTracker.forEach { fireTrack ->
                            deleteFromFireBase(fireTrack)
                        }
                        viewModel.removeAllTrack()
                        delay(1000)
                        navController.navigate(TrackerScreens.HomeScreen.name){
                            popUpTo(TrackerScreens.SettingsScreen.name) {
                                inclusive = true
                            }
                        }
                    }
                } ,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFD7F74),
                    contentColor = Color.White )) {
                Text(text = "Clear Data")
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = {
            auth.signOut()
            navController.navigate(TrackerScreens.LoginScreen.name){
                popUpTo(TrackerScreens.HomeScreen.name) {
                    inclusive = true
                }
            }
            viewModel.removeAllTrack()
        } ,
            colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF911C12),
            contentColor = Color.White )) {
            Text(text = "LOGOUT")
            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "")
        }
    }
}

@Composable
private fun SyncButton(
    fireviewModel: HomeScreenViewModel,
    trackList: List<Tracker>,
    new_Data: MutableState<Boolean>,
    listOfTracker: List<MTracker>,
    navController: NavController
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val activity = context as? Activity
    Button(
        enabled = !fireviewModel.data.value.data!!.isNullOrEmpty(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF56CEC7),
            contentColor = Color.White ),
        onClick = {
            activity?.let {
                // Call the notification function
                sendNotification(it, "Syncing Data....", "Your Data is being Synced")

            }
            for (track in trackList) {
                new_Data.value = true
                for (firetrack in listOfTracker) {
                    if (track.Id.toString() == firetrack.id.toString()) {
                        new_Data.value = false
                        updateToFirebase(firetrack, track) {
                            navController.navigate(TrackerScreens.HomeScreen.name) {
                                popUpTo(TrackerScreens.SettingsScreen.name) {
                                    inclusive = true
                                }
                            }
                        }
                        break
                    }
                }
                if (new_Data.value == true) {
                    saveToFirebase(track = track) {
                        navController.navigate(TrackerScreens.HomeScreen.name) {
                            popUpTo(TrackerScreens.SettingsScreen.name) {
                                inclusive = true
                            }
                        }
                    }
                }
            }
            listOfTracker.forEach { fireTrack ->
                new_Data.value = false
                trackList.forEach { track ->
                    if (track.Id.toString() == fireTrack.id.toString()) new_Data.value = true
                }
                if (new_Data.value == false) {
                    deleteFromFireBase(fireTrack)
                }
            }
            Handler(Looper.getMainLooper()).postDelayed({
                cancelNotification(context, 1)
            }, 5000) // 2000 milliseconds delay
        }
    ) {
        Text(text = "Sync Now")
    }
}

fun updateToFirebase(
    _fireTrack: MTracker,
    _track: Tracker,
    onSuccess: () -> Unit ={}
){
    val trackToUpdate = hashMapOf(
        "subject" to _track.Subject,
        "number" to _track.Number
    ).toMap()
    _fireTrack.docid?.let {
        FirebaseFirestore.getInstance()
            .collection("tracks")
            .document(it)
            .update(trackToUpdate)
            .addOnCompleteListener {task ->
                if (task.isSuccessful){
                    Log.d("FirebaseUpdate" , "updateToFirebase : ${task}")
                    onSuccess.invoke()
                }
            }
            .addOnFailureListener {
                Log.w("Error", "Error Updating Document", it)
            }
    }
}


fun saveToFirebase(
    track: Tracker,
    onSuccess: () -> Unit ={}
) {


    val db = FirebaseFirestore.getInstance()
    val dbCollection = db.collection("tracks")
    val mTrack = MTracker(
        id = track.Id.toString(),
        subject = track.Subject,
        number= track.Number,
        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
    )
    if (mTrack.toString().isNotEmpty()){
        dbCollection.add(mTrack)
            .addOnSuccessListener { documentRef ->
                val docId = documentRef.id
                dbCollection.document(docId)
                    .update(hashMapOf( "docid" to docId ) as Map<String,Any>)
                    .addOnCompleteListener{task ->
                        if (task.isSuccessful){
                               Log.d("FirebaseSave" , "saveToFirebase : ${task}")
                                   onSuccess.invoke()
                        }
                    }
                    .addOnFailureListener{
                        Log.w("Error","Savetofirebase : " , it)
                    }
            }
    }
}


fun deleteFromFireBase(
    mTracker: MTracker,
    onSuccess: () -> Unit ={}
){
   if (!mTracker.docid.isNullOrEmpty()) {
       FirebaseFirestore.getInstance()
           .collection("tracks")
           .document(mTracker.docid!!)
           .delete()
           .addOnCompleteListener {
               if (it.isSuccessful) {
                   Log.d("FireBase", "deleteFromFireBase: ${mTracker.docid} ")
               }
           }
   }
}
