package com.example.attendance_tracker.Components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendance_tracker.navigation.TrackerScreens
import com.google.firebase.auth.FirebaseAuth

@Composable
fun TrackerLogo(modifier: Modifier = Modifier,) {
    Text(
        text = "Attendance Tracker",
        modifier = modifier.padding(16.dp),
        style = MaterialTheme.typography.displayMedium,
        color = Color(0xFF56CEC7)
    )
}

@Composable
fun EmailInput(
    modifier: Modifier = Modifier,
    emailState : MutableState<String>,
    labelId: String = "Email",
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default
){
    InputField(
        modifier= modifier,
        valueState = emailState,
        labelId = labelId,
        enabled = enabled,
        keyboardType = KeyboardType.Email,
        imeAction = imeAction,
        onAction = onAction

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(
    modifier: Modifier = Modifier,
    valueState: MutableState<String>,
    labelId: String,
    enabled: Boolean,
    isSingleLine : Boolean = true,
    keyboardType : KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = valueState.value,
        onValueChange = { valueState.value = it },
        label = { Text(text = labelId , color = Color(0xFF56CEC7)) },
        singleLine = isSingleLine,
        textStyle = TextStyle(fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground),
        modifier = modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        enabled = enabled,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color(0xFF56CEC7),
            unfocusedBorderColor = Color(0xFF56CEC7)),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction)

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordInput(
    modifier: Modifier,
    passwordState: MutableState<String>,
    labelId: String,
    enabled: Boolean,
    passwordvisibility: MutableState<Boolean>,
    imeAction: ImeAction = ImeAction.Done,
    onAction: KeyboardActions = KeyboardActions.Default
) {
    val visualTransformation = if( passwordvisibility.value) VisualTransformation.None else
        PasswordVisualTransformation()
    OutlinedTextField(
        value = passwordState.value,
        onValueChange = { passwordState.value = it },
        label = { Text(text = labelId ,  color = Color(0xFF56CEC7))},
        singleLine = true,
        textStyle = TextStyle(fontSize = 18.sp , color = Color.Black),
        modifier = modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color(0xFF56CEC7),
            unfocusedBorderColor = Color(0xFF56CEC7)),
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction  ),
        visualTransformation = visualTransformation,
        trailingIcon = {PasswordVisibility(passwordvisibility = passwordvisibility)},
        keyboardActions = onAction
    )

}


@Composable
fun PasswordVisibility(passwordvisibility: MutableState<Boolean>) {
    val visible = passwordvisibility.value
    IconButton(
        onClick = { passwordvisibility.value = !visible }
    ) {
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "Toggle Password Visibility",
            tint = Color(0xFF56CEC7) // Set the desired color here
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerAppBar(
    navController: NavController = NavController(LocalContext.current),
)  {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Logo Icon",
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .scale(1f),
                        tint = Color.Black
                    )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "ATTENDANCE TRACKER",
                    color = Color(0xFF56CEC7),
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
                )


            }
        },
        actions = {
            IconButton(onClick = {
                    navController.navigate(TrackerScreens.SettingsScreen.name)
            }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color(0xFF363C3B)
                    )
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFFD7DFDD)),
        modifier = Modifier.background(color = Color(0xFF17796D)),
    )
}

@Composable
fun FABContent(onTap: () -> Unit) {
    FloatingActionButton(
        onClick = { onTap() },
        modifier = Modifier.clip(CircleShape),
        shape = RoundedCornerShape(50.dp),
        containerColor = Color(0xFF6CE2DB)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Subject",
            tint = Color.White
        )
    }

}