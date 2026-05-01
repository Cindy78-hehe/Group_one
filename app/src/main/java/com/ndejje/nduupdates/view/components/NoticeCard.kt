package com.ndejje.nduupdates.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ndejje.nduupdates.R
import com.ndejje.nduupdates.data.model.NoticeEntity
import com.ndejje.nduupdates.ui.theme.NDU_Dark_Purple
import com.ndejje.nduupdates.ui.theme.NDU_Light_Pink
import com.ndejje.nduupdates.ui.theme.NduUpdatesTheme
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import android.content.Intent
import android.net.Uri

import androidx.compose.ui.res.painterResource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NoticeCard(
    notice: NoticeEntity,
    showDelete: Boolean = false,
    showTarget: Boolean = false,
    onDelete: () -> Unit = {},
    onComment: () -> Unit = {}
) {
    val context = LocalContext.current
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val dateString = try {
        sdf.format(Date(notice.timestamp))
    } catch (e: Exception) {
        "Recently"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, NDU_Light_Pink)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(NDU_Light_Pink, androidx.compose.foundation.shape.CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(notice.author.take(1), fontWeight = FontWeight.Bold, color = NDU_Dark_Purple)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = notice.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NDU_Dark_Purple,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${notice.author} • $dateString",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }

                if (showDelete) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = notice.content,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            if (notice.attachmentUri != null) {
                Spacer(modifier = Modifier.height(12.dp))
                if (notice.attachmentType == "IMAGE") {
                    AsyncImage(
                        model = notice.attachmentUri,
                        contentDescription = "Attachment",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NDU_Light_Pink.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = notice.attachmentUri.toUri()
                                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    // Handle error
                                }
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = NDU_Dark_Purple)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View Document", fontSize = 14.sp, color = NDU_Dark_Purple, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (showTarget) {
                        Badge(containerColor = NDU_Light_Pink.copy(alpha = 0.5f)) {
                            Text(
                                text = "To: ${notice.targetRole}",
                                modifier = Modifier.padding(horizontal = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = NDU_Dark_Purple
                            )
                        }
                    }
                }

                Button(
                    onClick = onComment,
                    colors = ButtonDefaults.buttonColors(containerColor = NDU_Light_Pink),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.stat_notify_chat),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = NDU_Dark_Purple
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Comments", color = NDU_Dark_Purple, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoticeCardPreview() {
    NduUpdatesTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            NoticeCard(
                notice = NoticeEntity(
                    title = "Holiday Announcement",
                    content = "Campus will be closed for the Easter break starting this Friday.",
                    author = "Dean of Students",
                    authorRole = "Admin",
                    targetRole = "All"
                ),
                showTarget = true,
                showDelete = true
            )
        }
    }
}
