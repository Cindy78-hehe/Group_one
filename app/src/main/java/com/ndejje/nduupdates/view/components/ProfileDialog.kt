package com.ndejje.nduupdates.view.components

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
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.ndejje.nduupdates.data.model.UserEntity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.ndejje.nduupdates.R
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
    var profilePicUri by remember { mutableStateOf(user?.profilePictureUri?.toUri()) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> profilePicUri = uri }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(R.string.title_user_profile),
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
                        .size(dimensionResource(R.dimen.profile_image_size))
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
                            contentDescription = stringResource(R.string.content_description_profile),
                            modifier = Modifier
                                .size(dimensionResource(R.dimen.profile_image_size))
                                .clip(CircleShape)
                                .background(NDU_Light_Pink),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(dimensionResource(R.dimen.profile_image_size))
                                .clip(CircleShape)
                                .background(NDU_Light_Pink),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(dimensionResource(R.dimen.profile_image_size).times(0.6f)),
                                tint = NDU_Dark_Purple
                            )
                        }
                    }
                    
                    Surface(
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.spacing_32))
                            .clip(CircleShape),
                        color = NDU_Dark_Purple,
                        tonalElevation = dimensionResource(R.dimen.cardElevation)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(R.string.content_description_edit_picture),
                            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_8).div(1.33f)),
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_16)))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.label_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NDU_Dark_Purple,
                        focusedLabelColor = NDU_Dark_Purple
                    )
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_8)))
                
                Text(
                    text = stringResource(R.string.label_email_display, user?.email ?: "N/A"),
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )
                Text(
                    text = stringResource(R.string.label_role_display, user?.role ?: "N/A"),
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
                Text(stringResource(R.string.btn_save_changes))
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onLogout) {
                    Text(stringResource(R.string.btn_logout), color = Color.Red)
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        }
    )
}
