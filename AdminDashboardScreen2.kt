package com.ndejje.nduupdates.view.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ndejje.nduupdates.R
import com.ndejje.nduupdates.Routes
import com.ndejje.nduupdates.data.model.CommentEntity
import com.ndejje.nduupdates.data.model.NoticeEntity
import com.ndejje.nduupdates.ui.theme.NDU_Dark_Purple
import com.ndejje.nduupdates.ui.theme.NDU_Light_Pink
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
    val tabs = listOf("Home", "Posts", "News", "Events")
    val icons = listOf(Icons.Default.Home, Icons.AutoMirrored.Filled.List, Icons.Default.Info, Icons.Default.DateRange)

    var showAddDialog by remember { mutableStateOf(false) }
    val notices by noticeViewModel.notices.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    var showCommentsForNotice by remember { mutableStateOf<NoticeEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "NDU Logo",
                            modifier = Modifier.size(40.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Admin Dashboard", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    TextButton(onClick = {
                        authViewModel.logout()
                        navController.navigate(Routes.WELCOME) {
                            popUpTo(0)
                        }
                    }) {
                        Text("Sign Out", color = Color.White)
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
                    Icon(Icons.Default.Add, contentDescription = "Add Post")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(Color.White)) {
            val type = when(selectedTab) {
                1 -> "Notice"
                2 -> "News"
                3 -> "Event"
                else -> null
            }
            
            if (selectedTab == 0) {
                AdminHomeScreen()
            } else {
                val filteredNotices = if (type == "Notice") {
                    notices.filter { it.type == "Notice" || it.type == "" }
                } else {
                    notices.filter { it.type == type }
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
            onPost = { title, content, target, postType ->
                val newNotice = NoticeEntity(
                    title = title,
                    content = content,
                    targetRole = target,
                    author = currentUser?.username ?: "Admin",
                    authorRole = "ADMIN",
                    type = postType,
                    timestamp = System.currentTimeMillis()
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
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Admin Control Panel",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = NDU_Dark_Purple
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Manage university updates, news, and events from here.",
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth().height(150.dp),
                colors = CardDefaults.cardColors(containerColor = NDU_Light_Pink.copy(alpha = 0.5f))
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "System Overview",
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
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Manage $title", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NDU_Dark_Purple)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (notices.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No $title posted yet.", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
                items(notices) { notice ->
                    AdminPostCard(
                        notice, 
                        onDelete = { noticeViewModel.deleteNotice(notice.id) },
                        onComment = { onViewComments(notice) }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminPostCard(notice: NoticeEntity, onDelete: () -> Unit, onComment: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, NDU_Light_Pink)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(notice.title, fontWeight = FontWeight.Bold, color = NDU_Dark_Purple, fontSize = 18.sp)
                    Text("By ${notice.author} • Target: ${notice.targetRole}", fontSize = 12.sp, color = Color.Gray)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(notice.content, fontSize = 14.sp, color = Color.DarkGray)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUpdateDialog(
    initialType: String,
    onDismiss: () -> Unit, 
    onPost: (String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var targetRole by remember { mutableStateOf("All") }
    var postType by remember { mutableStateOf(initialType) }
    
    val roles = listOf("All", "Student", "Lecturer")
    val types = listOf("Notice", "News", "Event")
    
    var roleExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Post New $postType", color = NDU_Dark_Purple, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = title, 
                    onValueChange = { title = it }, 
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NDU_Dark_Purple,
                        focusedLabelColor = NDU_Dark_Purple
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { typeExpanded = !typeExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = postType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Type") },
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
                            label = { Text("Audience") },
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
                
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = content, 
                    onValueChange = { content = it }, 
                    label = { Text("Content") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NDU_Dark_Purple,
                        focusedLabelColor = NDU_Dark_Purple
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank() && content.isNotBlank()) onPost(title, content, targetRole, postType) },
                colors = ButtonDefaults.buttonColors(containerColor = NDU_Dark_Purple)
            ) { Text("Post", color = Color.White) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = NDU_Dark_Purple) }
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
        title = { Text("Comments - ${notice.title}", fontWeight = FontWeight.Bold, color = NDU_Dark_Purple) },
        text = {
            Column(modifier = Modifier.heightIn(max = 400.dp)) {
                if (comments.isEmpty()) {
                    Text("No comments yet.", color = Color.Gray, modifier = Modifier.padding(vertical = 16.dp))
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(comments) { comment ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = NDU_Light_Pink.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(comment.author, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NDU_Dark_Purple)
                                    Text(comment.content, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = newComment,
                    onValueChange = { newComment = it },
                    label = { Text("Add a comment") },
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
            ) { Text("Send") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
