package com.ndejje.nduupdates.view.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ndejje.nduupdates.data.model.UserEntity
import com.ndejje.nduupdates.ui.theme.NDU_Dark_Purple
import com.ndejje.nduupdates.ui.theme.NDU_Light_Pink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDialog(
    user: UserEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String?) -> Unit,
    onLogout: () -> Unit
) {
    var name by remember { mutableStateOf(user?.username ?: "") }
    var profilePicUri by remember { mutableStateOf<Uri?>(user?.profilePictureUri?.let { Uri.parse(it) }) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> profilePicUri = uri }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "User Profile",
                fontWeight = FontWeight.Bold,
                color = NDU_Dark_Purple,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.BottomEnd
                ) {
                    if (profilePicUri != null) {
                        AsyncImage(
                            model = profilePicUri,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(NDU_Light_Pink),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(NDU_Light_Pink),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                tint = NDU_Dark_Purple
                            )
                        }
                    }
                    
                    Surface(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape),
                        color = NDU_Dark_Purple,
                        tonalElevation = 4.dp
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Change Picture",
                            modifier = Modifier.padding(6.dp),
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NDU_Dark_Purple,
                        focusedLabelColor = NDU_Dark_Purple
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Email: ${user?.email ?: "N/A"}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )
                Text(
                    text = "Role: ${user?.role ?: "N/A"}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(name, profilePicUri?.toString())
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NDU_Dark_Purple)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onLogout) {
                    Text("Logout", color = Color.Red)
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
