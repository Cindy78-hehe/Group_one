package com.ndejje.nduupdates.view.dashboard

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.ndejje.nduupdates.R
import com.ndejje.nduupdates.Routes
import com.ndejje.nduupdates.data.model.CommentEntity
import com.ndejje.nduupdates.data.model.NoticeEntity
import com.ndejje.nduupdates.ui.theme.NDU_Dark_Purple
import com.ndejje.nduupdates.ui.theme.NDU_Light_Pink
import com.ndejje.nduupdates.view.components.NoticeCard
import com.ndejje.nduupdates.view.components.ProfileDialog
import com.ndejje.nduupdates.viewmodel.AuthViewModel
import com.ndejje.nduupdates.viewmodel.NoticeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavHostController,
    noticeViewModel: NoticeViewModel,
    authViewModel: AuthViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.label_home),
        stringResource(R.string.label_posts),
        stringResource(R.string.label_news),
        stringResource(R.string.label_events)
    )
    val icons = listOf(Icons.Default.Home, Icons.AutoMirrored.Filled.List, Icons.Default.Info, Icons.Default.DateRange)

    var showAddDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    val notices by noticeViewModel.notices.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    var showCommentsForNotice by remember { mutableStateOf<NoticeEntity?>(null) }

    if (showProfileDialog) {
        ProfileDialog(
            user = currentUser,
            onDismiss = { showProfileDialog = false },
            onSave = { name, uri ->
                authViewModel.updateProfile(name, uri)
            },
            onLogout = {
                showProfileDialog = false
                authViewModel.logout()
                navController.navigate(Routes.WELCOME) {
                    popUpTo(0)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.nduupdates333),
                            contentDescription = stringResource(R.string.content_description_logo),
                            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_xlarge)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_12)))
                        Text(stringResource(R.string.title_admin_dashboard), color = Color.White, style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = {
                    IconButton(onClick = { showProfileDialog = true }) {
                        if (currentUser?.profilePictureUri != null) {
                            AsyncImage(
                                model = currentUser?.profilePictureUri,
                                contentDescription = stringResource(R.string.content_description_profile),
                                modifier = Modifier
                                    .size(dimensionResource(R.dimen.top_bar_icon_size))
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = stringResource(R.string.content_description_profile),
                                tint = Color.White,
                                modifier = Modifier.size(dimensionResource(R.dimen.icon_size_large))
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NDU_Dark_Purple)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        label = { Text(title) },
                        icon = {
                            Icon(
                                icons[index],
                                contentDescription = title
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NDU_Dark_Purple,
                            selectedTextColor = NDU_Dark_Purple,
                            indicatorColor = NDU_Light_Pink,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Black
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedTab != 0) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = NDU_Dark_Purple,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.btn_post))
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(Color.White)) {
            val typeFilter = when(selectedTab) {
                1 -> "Notice"
                2 -> "News"
                3 -> "Event"
                else -> null
            }
            
            if (selectedTab == 0) {
                AdminHomeScreen()
            } else {
                val filteredNotices = if (typeFilter == "Notice") {
                    notices.filter { it.type == "Notice" || it.type == "" }
                } else {
                    notices.filter { it.type == typeFilter }
                }
                AdminPostsScreen(
                    title = tabs[selectedTab],
                    notices = filteredNotices, 
                    noticeViewModel = noticeViewModel,
                    onViewComments = { showCommentsForNotice = it }
                )
            }
        }
    }

    if (showAddDialog) {
        val initialType = when(selectedTab) {
            2 -> "News"
            3 -> "Event"
            else -> "Notice"
        }
        AddUpdateDialog(
            initialType = initialType,
            onDismiss = { showAddDialog = false },
            onPost = { title, content, target, postType, attachmentUri, attachmentType ->
                val newNotice = NoticeEntity(
                    title = title,
                    content = content,
                    targetRole = target,
                    author = currentUser?.username ?: "Admin",
                    authorRole = "ADMIN",
                    type = postType,
                    timestamp = System.currentTimeMillis(),
                    attachmentUri = attachmentUri,
                    attachmentType = attachmentType
                )
                noticeViewModel.addNotice(newNotice)
                showAddDialog = false
            }
        )
    }

    if (showCommentsForNotice != null) {
        CommentsDialog(
            notice = showCommentsForNotice!!,
            noticeViewModel = noticeViewModel,
            currentUser = currentUser?.username ?: "Admin",
            onDismiss = { showCommentsForNotice = null }
        )
    }
}

@Composable
fun AdminHomeScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(dimensionResource(R.dimen.card_inner_padding)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_20)))
            Text(
                stringResource(R.string.title_admin_control_panel),
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = NDU_Dark_Purple
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_8)))
            Text(
                stringResource(R.string.desc_admin_panel),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.card_inner_padding))
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_32)))
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth().height(150.dp),
                colors = CardDefaults.cardColors(containerColor = NDU_Light_Pink.copy(alpha = 0.5f))
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(R.string.title_system_overview),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun AdminPostsScreen(
    title: String,
    notices: List<NoticeEntity>, 
    noticeViewModel: NoticeViewModel,
    onViewComments: (NoticeEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(dimensionResource(R.dimen.card_inner_padding))) {
        Text(stringResource(R.string.title_manage_items, title), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NDU_Dark_Purple)
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.card_inner_padding)))
        
        if (notices.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.msg_no_items_posted, title), color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_12)), modifier = Modifier.fillMaxSize()) {
                items(notices) { notice ->
                    NoticeCard(
                        notice = notice, 
                        showDelete = true,
                        showTarget = true,
                        onDelete = { noticeViewModel.deleteNotice(notice.id) },
                        onComment = { onViewComments(notice) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUpdateDialog(
    initialType: String,
    onDismiss: () -> Unit, 
    onPost: (String, String, String, String, String?, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var targetRole by remember { mutableStateOf("All") }
    var postType by remember { mutableStateOf(initialType) }
    var attachmentUri by remember { mutableStateOf<Uri?>(null) }
    var attachmentType by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> 
            if (uri != null) {
                attachmentUri = uri
                attachmentType = "IMAGE"
            }
        }
    )
    
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                attachmentUri = uri
                attachmentType = "FILE"
            }
        }
    )
    
    val roles = listOf("All", "Student", "Lecturer")
    val types = listOf(
        stringResource(R.string.label_notice),
        stringResource(R.string.label_news),
        stringResource(R.string.label_event)
    )
    
    var roleExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.title_post_new, postType), color = NDU_Dark_Purple, fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn {
                item {
                    OutlinedTextField(
                        value = title, 
                        onValueChange = { title = it }, 
                        label = { Text(stringResource(R.string.label_title)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NDU_Dark_Purple,
                            focusedLabelColor = NDU_Dark_Purple
                        )
                    )
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_12)))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_8))) {
                        ExposedDropdownMenuBox(
                            expanded = typeExpanded,
                            onExpandedChange = { typeExpanded = !typeExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = postType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.label_type)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NDU_Dark_Purple,
                                    focusedLabelColor = NDU_Dark_Purple
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = typeExpanded,
                                onDismissRequest = { typeExpanded = false }
                            ) {
                                types.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type) },
                                        onClick = {
                                            postType = type
                                            typeExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        ExposedDropdownMenuBox(
                            expanded = roleExpanded,
                            onExpandedChange = { roleExpanded = !roleExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = targetRole,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.label_audience)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NDU_Dark_Purple,
                                    focusedLabelColor = NDU_Dark_Purple
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = roleExpanded,
                                onDismissRequest = { roleExpanded = false }
                            ) {
                                roles.forEach { role ->
                                    DropdownMenuItem(
                                        text = { Text(role) },
                                        onClick = {
                                            targetRole = role
                                            roleExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_12)))
                    OutlinedTextField(
                        value = content, 
                        onValueChange = { content = it }, 
                        label = { Text(stringResource(R.string.label_content)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NDU_Dark_Purple,
                            focusedLabelColor = NDU_Dark_Purple
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_16)))
                    
                    Text(stringResource(R.string.label_attachments), fontWeight = FontWeight.Bold, color = NDU_Dark_Purple)
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_8)))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_8))) {
                        OutlinedButton(
                            onClick = { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null)
                            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_4)))
                            Text(stringResource(R.string.label_image), fontSize = 12.sp)
                        }
                        OutlinedButton(
                            onClick = { filePicker.launch("*/*") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.AttachFile, contentDescription = null)
                            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_4)))
                            Text(stringResource(R.string.label_file), fontSize = 12.sp)
                        }
                    }
                    
                    if (attachmentUri != null) {
                        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_8)))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(NDU_Light_Pink.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(dimensionResource(R.dimen.spacing_8))
                        ) {
                            Icon(
                                if (attachmentType == "IMAGE") Icons.Default.Image else Icons.Default.Description,
                                contentDescription = null,
                                tint = NDU_Dark_Purple,
                                modifier = Modifier.size(dimensionResource(R.dimen.icon_size_xlarge).div(2))
                            )
                            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_8)))
                            Text(
                                stringResource(R.string.label_attachment_selected),
                                modifier = Modifier.weight(1f),
                                fontSize = 12.sp
                            )
                            IconButton(onClick = { attachmentUri = null; attachmentType = null }, modifier = Modifier.size(dimensionResource(R.dimen.icon_size_medium))) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Red, modifier = Modifier.size(dimensionResource(R.dimen.icon_size_small)))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onPost(title, content, targetRole, postType, attachmentUri?.toString(), attachmentType) 
                    }
                },
                enabled = title.isNotBlank() && content.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NDU_Dark_Purple)
            ) { Text(stringResource(R.string.btn_post), color = Color.White) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.btn_cancel), color = NDU_Dark_Purple) }
        }
    )
}

@Composable
fun CommentsDialog(
    notice: NoticeEntity,
    noticeViewModel: NoticeViewModel,
    currentUser: String,
    onDismiss: () -> Unit
) {
    val comments by noticeViewModel.getCommentsForNotice(notice.id).collectAsState(initial = emptyList())
    var newComment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.title_comments_with_name, notice.title), fontWeight = FontWeight.Bold, color = NDU_Dark_Purple) },
        text = {
            Column(modifier = Modifier.heightIn(max = 400.dp)) {
                if (comments.isEmpty()) {
                    Text(stringResource(R.string.msg_no_comments), color = Color.Gray, modifier = Modifier.padding(vertical = dimensionResource(R.dimen.spacing_16)))
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_8))
                    ) {
                        items(comments) { comment ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = NDU_Light_Pink.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(dimensionResource(R.dimen.spacing_8))) {
                                    Text(comment.author, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NDU_Dark_Purple)
                                    Text(comment.content, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_16)))
                OutlinedTextField(
                    value = newComment,
                    onValueChange = { newComment = it },
                    label = { Text(stringResource(R.string.label_add_comment)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NDU_Dark_Purple,
                        focusedLabelColor = NDU_Dark_Purple
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newComment.isNotBlank()) {
                        noticeViewModel.addComment(
                            CommentEntity(
                                noticeId = notice.id,
                                author = currentUser,
                                content = newComment
                            )
                        )
                        newComment = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NDU_Dark_Purple)
            ) { Text(stringResource(R.string.btn_send)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.btn_close)) }
        }
    )
}
