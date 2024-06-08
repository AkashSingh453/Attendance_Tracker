package com.example.attendance_tracker.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.attendance_tracker.Components.FABContent
import com.example.attendance_tracker.Components.TrackerAppBar
import com.example.attendance_tracker.model.MTracker
import com.example.attendance_tracker.model.Tracker
import com.example.attendance_tracker.navigation.TrackerScreens
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID
import javax.security.auth.Subject


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController = NavController(LocalContext.current),
    viewModel: TrackViewModel = hiltViewModel(),
    fireViewModel: HomeScreenViewModel = hiltViewModel()
) {
    val trackList = viewModel.TrackList.collectAsState().value
    val enterSubject = rememberSaveable {
        mutableStateOf(false)
    }
    var subject : String = ""
    val EditOn = rememberSaveable {
        mutableStateOf(false)
    }
    val uuid = remember {
        mutableStateOf<UUID>(UUID.randomUUID())
    }
    val Number = remember {
        mutableStateOf(0)
    }
    Scaffold(
        topBar = {
            TrackerAppBar(navController = navController)
        },
        floatingActionButton = {
            if (!enterSubject.value) {
                FABContent {
                    enterSubject.value = !enterSubject.value
                    EditOn.value = false
                }
            }

        }) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                Spacer(modifier = Modifier.height(70.dp))
                if (trackList.isNotEmpty()) {
                    LazyColumn {
                        items(trackList) { track ->
                            SubjectCard(
                                viewModel,
                                track,
                                onEdit = { _uuid, _subject, _number ->
                                    subject = _subject
                                    uuid.value = _uuid
                                    Number.value = _number
                                    enterSubject.value = !enterSubject.value
                                    EditOn.value = true
                                }
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Add Subjects by pressing the add(+) Button \nat Bottom.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            if (enterSubject.value) {
            InputSubject(
                subject = subject,
                modifier = Modifier.align(Alignment.BottomCenter),
                onSave = {subject ->
                    if (EditOn.value) {
                        viewModel.updateTrack(Tracker(uuid.value, subject, Number.value ))
                    }else {
                        viewModel.addTrack(Tracker(UUID.randomUUID(), subject, 0 ))
                    }
                    enterSubject.value = !enterSubject.value
                },
                onCancel = {
                    enterSubject.value = !enterSubject.value
                })
        }

        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputSubject(
    subject : String? = null,
    modifier: Modifier = Modifier,
    onSave : (String) -> Unit,
    onCancel : () -> Unit,

){
    val _subject = rememberSaveable {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    if (subject != null) _subject.value = subject
    Card(modifier = modifier
        .padding(10.dp)
        .fillMaxWidth(0.9f)
        .height(200.dp)
        .border(BorderStroke(2.dp, Color(0xFFCEDDA4)), shape = RoundedCornerShape(10.dp))
        , colors = CardDefaults.cardColors(containerColor = Color(0xFFF3FCE8)),
    ) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            OutlinedTextField(
                value = _subject.value,
                onValueChange = {_subject.value = it},
                label = { Text(text = "Enter The Course" , color = Color(0xFF56CEC7)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        onSave(_subject.value)
                    }
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF56CEC7),
                    unfocusedBorderColor = Color(0xFF56CEC7) ,
                    textColor = Color(0xFF485353)
                ),
                textStyle = TextStyle(
                    color = Color(0xFF485353), // Ensure the text color is set here as well
                    fontSize = 18.sp
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = {
                    onCancel.invoke()
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFD7F74),
                        contentColor = Color.White )
                ) {
                   Text(text = "Cancel")
                }
               Button(onClick = {
                   if (_subject.value.isNotEmpty()) {
                       onSave(_subject.value)
                   }
               },
                   colors = ButtonDefaults.buttonColors(
                       containerColor = Color(0xFF56CEC7),
                       contentColor = Color.White )
               ) {
                   Text(text = "Save")
               }

            }
        }

    }
}


@Composable
fun SubjectCard(
    viewModel: TrackViewModel = hiltViewModel(),
    track : Tracker,
    onEdit: (UUID,String,Int) -> Unit,
    fireviewModel: HomeScreenViewModel = hiltViewModel()
){
    val enabled :Boolean  = if(track.Number > 0) true else false
    val isFlipped =  remember { mutableStateOf(false) }

    var listOfTracker = emptyList<MTracker>()
    val currentUser = FirebaseAuth.getInstance().currentUser

    if (!fireviewModel.data.value.data.isNullOrEmpty()){
        listOfTracker = fireviewModel.data.value.data!!.toList().filter { mTrack ->
            mTrack.userId == currentUser?.uid.toString()
        }
        Log.d("Books" , "HomeContent : ${listOfTracker.toString()}")
    }

    Card(modifier = Modifier
        .padding(5.dp)
        .fillMaxWidth()
        .height(100.dp)
        .pointerInput(Unit) {
            detectTapGestures(
                onLongPress = {
                    isFlipped.value = !isFlipped.value
                }
            )
        },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE7F0C6)),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Crossfade(targetState = isFlipped, label = "longPressToFlip"){ flipped ->
            if (!flipped.value) {
                NormalCard(enabled = enabled, track, viewModel)
            }else {
                FlippedCard(
                    onDelete = {
                        viewModel.removeTracker(track)
                        listOfTracker.forEach { _fireTrack ->
                            if (_fireTrack.id.toString() == track.Id.toString()){
                               deleteFromFireBase(_fireTrack)
                            }
                        }
                        isFlipped.value = !isFlipped.value
                               },
                    onEdit = {
                        onEdit( track.Id,track.Subject, track.Number)
                        isFlipped.value = !isFlipped.value
                    },
                    track
                )
            }
        }


    }
}

@Composable
fun FlippedCard(
    onDelete: () -> Unit,
    onEdit: (String) -> Unit,
    track : Tracker,
) {
    Column(modifier = Modifier
        .fillMaxHeight()
        .padding(5.dp),
        verticalArrangement = Arrangement.SpaceAround
    ) {

        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround) {

            Button(
                onClick = { onEdit(track.Subject) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF56CEC7),
                    contentColor = Color.White )
            ) {
                Text(text = "Edit")
            }
            Button(onClick = { onDelete.invoke() },
                colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFD7F74),
                contentColor = Color.White )) {
                Text(text = "Delete")
            }

        }
    }
}

@Composable
fun NormalCard(
    enabled : Boolean,
    track : Tracker,
    viewModel: TrackViewModel = hiltViewModel()
){
    Column(modifier = Modifier
        .fillMaxHeight()
        .padding(5.dp),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround) {
            Box(modifier = Modifier
                .width(200.dp)) {
                Text(text = track.Subject, fontSize = 18.sp, color = Color(0xFF485353) , overflow = TextOverflow.Ellipsis , maxLines = 2 )
            }
            Box(modifier = Modifier
                .width(70.dp))
            {
                Text(text = "Classes", fontSize = 10.sp, color = Color(0xFF616F6F))
                Text(text = "Missed", fontSize = 10.sp, color = Color(0xFF616F6F), modifier = Modifier.align(
                    Alignment.BottomStart) )
                Text(text = ": ${track.Number.toString()}", fontSize = 18.sp, color = Color(0xFF485353) , modifier = Modifier.align(
                    Alignment.CenterEnd))
            }
        }

        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround) {

            IconButton(
                onClick = {
                    viewModel.updateTrack(Tracker(track.Id,track.Subject,track.Number - 1))
                },
                enabled = enabled
            ) {
                Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "Decrease Classes", tint = Color(0xFF485353))
            }
            IconButton(
                onClick = {
                    viewModel.updateTrack(Tracker(track.Id,track.Subject,track.Number + 1))
                },

                ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Classes", tint = Color(0xFF485353))
            }
        }
    }
}