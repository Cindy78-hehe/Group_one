package com.ndejje.nduupdates.view.components

import android.content.Intent
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.ndejje.nduupdates.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.ndejje.nduupdates.data.model.NoticeEntity
import com.ndejje.nduupdates.ui.theme.NDU_Dark_Purple
import com.ndejje.nduupdates.ui.theme.NDU_Light_Pink
import com.ndejje.nduupdates.ui.theme.NduUpdatesTheme
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
        stringResource(R.string.label_recent)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.spacing_4)),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.cardElevation)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, NDU_Light_Pink)
    ) {
        Column(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.card_inner_padding))
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
                            .size(dimensionResource(R.dimen.icon_size_xlarge))
                            .background(NDU_Light_Pink, androidx.compose.foundation.shape.CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(notice.author.take(1), fontWeight = FontWeight.Bold, color = NDU_Dark_Purple)
                    }
                    Spacer(Modifier.width(dimensionResource(R.dimen.spacing_12)))
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
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_size_medium))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.label_delete),
                            tint = Color.Red
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_12)))

            Text(
                text = notice.content,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            if (notice.attachmentUri != null) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_12)))
                if (notice.attachmentType == "IMAGE") {
                    AsyncImage(
                        model = notice.attachmentUri,
                        contentDescription = stringResource(R.string.content_description_attachment),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensionResource(R.dimen.attachment_image_height))
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
                            .padding(dimensionResource(R.dimen.spacing_12)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = NDU_Dark_Purple)
                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_8)))
                        Text(
                            stringResource(R.string.label_view_document),
                            fontSize = 14.sp,
                            color = NDU_Dark_Purple,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_16)))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (showTarget) {
                        Badge(containerColor = NDU_Light_Pink.copy(alpha = 0.5f)) {
                            Text(
                                text = stringResource(R.string.label_to, notice.targetRole),
                                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.badge_padding_horizontal)),
                                style = MaterialTheme.typography.labelSmall,
                                color = NDU_Dark_Purple
                            )
                        }
                    }
                }

                Button(
                    onClick = onComment,
                    colors = ButtonDefaults.buttonColors(containerColor = NDU_Light_Pink),
                    contentPadding = PaddingValues(
                        horizontal = dimensionResource(R.dimen.button_padding_horizontal),
                        vertical = dimensionResource(R.dimen.button_padding_vertical)
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.stat_notify_chat),
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_size_small)),
                        tint = NDU_Dark_Purple
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_8)))
                    Text(
                        stringResource(R.string.label_comments),
                        color = NDU_Dark_Purple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
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
