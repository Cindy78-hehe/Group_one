package com.ndejje.nduupdates.view.dashboard

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ndejje.nduupdates.R
import com.ndejje.nduupdates.Routes
import com.ndejje.nduupdates.data.model.NoticeEntity
import com.ndejje.nduupdates.ui.theme.NDU_Dark_Purple
import com.ndejje.nduupdates.ui.theme.NDU_Light_Pink
import com.ndejje.nduupdates.viewmodel.AuthViewModel
import com.ndejje.nduupdates.viewmodel.NoticeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(
    navController: NavHostController,
    noticeViewModel: NoticeViewModel,
    authViewModel: AuthViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Home", "Notice", "News", "Events")
    val icons = listOf(Icons.Default.Home, Icons.AutoMirrored.Filled.List, Icons.Default.Info, Icons.Default.DateRange)
    var showProfileDialog by remember { mutableStateOf(false) }
    var showCommentsForNotice by remember { mutableStateOf<NoticeEntity?>(null) }
    
    val currentUser by authViewModel.currentUser.collectAsState()
    val notices by noticeViewModel.notices.collectAsState()

    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = { Text("Student Profile") },
            text = {
                Column {
                    Text("Name: ${currentUser?.username ?: "John Doe"}", fontWeight = FontWeight.Bold)
                    Text("Role: ${currentUser?.role ?: "Student"}")
                    Text("Email: ${currentUser?.email ?: "N/A"}")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showProfileDialog = false
                    authViewModel.logout()
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(0)
                    }
                }) {
                    Text("Logout", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text("Close")
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
                            painter = painterResource(id = R.drawable.nduupdate33),
                            contentDescription = "NDU Logo",
                            modifier = Modifier.size(40.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "NDU Updates",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showProfileDialog = true }) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
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
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            val type = when (selectedTab) {
                1 -> "Notice"
                2 -> "News"
                3 -> "Event"
                else -> null
            }

            if (selectedTab == 0) {
                HomeScreen()
            } else {
                val userRole = "Student"
                val filteredNotices = notices.filter { notice ->
                    val typeMatch = if (type == "Notice") {
                        notice.type == "Notice" || notice.type == ""
                    } else {
                        notice.type == type
                    }
                    val targetMatch = notice.targetRole == "All" || notice.targetRole == userRole
                    typeMatch && targetMatch
                }

                PostsScreen(
                    title = tabs[selectedTab],
                    notices = filteredNotices,
                    onViewComments = { showCommentsForNotice = it }
                )
            }
        }
    }

    if (showCommentsForNotice != null) {
        CommentsDialog(
            notice = showCommentsForNotice!!,
            noticeViewModel = noticeViewModel,
            currentUser = currentUser?.username ?: "User",
            onDismiss = { showCommentsForNotice = null }
        )
    }
}

@Composable
fun HomeScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Welcome to NDU Updates",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = NDU_Dark_Purple
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Stay informed with the latest information from Ndejje University.",
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
                        text = "Featured Update",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun PostsScreen(
    title: String,
    notices: List<NoticeEntity>,
    onViewComments: (NoticeEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NDU_Dark_Purple)
        Spacer(modifier = Modifier.height(16.dp))

        if (notices.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No $title found.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(notices) { update ->
                    PostCard(update, onComment = { onViewComments(update) })
                }
            }
        }
    }
}

@Composable
fun PostCard(notice: NoticeEntity, onComment: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, NDU_Light_Pink)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(NDU_Light_Pink, androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(notice.author.take(1), fontWeight = FontWeight.Bold, color = NDU_Dark_Purple)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(notice.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NDU_Dark_Purple)
                    Text(notice.author, fontSize = 12.sp, color = Color.Gray)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(notice.content, fontSize = 14.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onComment,
                colors = ButtonDefaults.buttonColors(containerColor = NDU_Light_Pink),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.stat_notify_chat),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = NDU_Dark_Purple
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Comment", color = NDU_Dark_Purple, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun SimplePlaceholderScreen(title: String) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(64.dp), tint = NDU_Light_Pink)
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NDU_Dark_Purple)
            Text("No new $title at the moment.", color = Color.Gray)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StudentDashboardScreenPreview() {
    com.ndejje.nduupdates.ui.theme.NduUpdatesTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            HomeScreen()
        }
    }
}
