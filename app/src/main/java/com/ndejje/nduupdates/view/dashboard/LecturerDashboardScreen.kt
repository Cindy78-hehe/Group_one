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
import com.ndejje.nduupdates.data.model.NoticeEntity
import com.ndejje.nduupdates.ui.theme.NDU_Dark_Purple
import com.ndejje.nduupdates.ui.theme.NDU_Light_Pink
import com.ndejje.nduupdates.viewmodel.AuthViewModel
import com.ndejje.nduupdates.viewmodel.NoticeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LecturerDashboardScreen(
    navController: NavHostController,
    noticeViewModel: NoticeViewModel,
    authViewModel: AuthViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Home", "My Posts", "News", "Events")
    val icons = listOf(Icons.Default.Home, Icons.AutoMirrored.Filled.List, Icons.Default.Info, Icons.Default.DateRange)
    var showProfileDialog by remember { mutableStateOf(false) }

    val currentUser by authViewModel.currentUser.collectAsState()
    val notices by noticeViewModel.notices.collectAsState()

    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = { Text("Lecturer Profile") },
            text = {
                Column {
                    Text("Name: ${currentUser?.username ?: "Lecturer Name"}", fontWeight = FontWeight.Bold)
                    Text("Role: ${currentUser?.role ?: "Lecturer"}")
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
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "NDU Logo",
                            modifier = Modifier.size(40.dp),
                            tint = Color.Unspecified
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
        },
        floatingActionButton = {
            if (selectedTab == 1) { // Only show on Posts tab
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate(Routes.CREATE_NOTICE) },
                    containerColor = NDU_Dark_Purple,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("New Notice")
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
            when (selectedTab) {
                0 -> LecturerHomeScreen()
                1 -> LecturerPostsScreen(notices, noticeViewModel)
                2 -> SimplePlaceholderScreen("University News")
                3 -> SimplePlaceholderScreen("Upcoming Events")
            }
        }
    }
}

@Composable
fun LecturerHomeScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Lecturer Dashboard",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = NDU_Dark_Purple
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Manage your announcements and stay updated with campus news.",
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
                        text = "Recent Activity Summary",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LecturerPostsScreen(notices: List<NoticeEntity>, viewModel: NoticeViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All My Updates") }
    val filters = listOf("All My Updates", "Academic Notices", "Department News")

    // For lecturers, maybe they want to see all or just theirs. 
    // For now, let's just show all but label them.
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedFilter,
                onValueChange = {},
                readOnly = true,
                label = { Text("Filter My Posts") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NDU_Dark_Purple,
                    focusedLabelColor = NDU_Dark_Purple,
                    cursorColor = NDU_Dark_Purple
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                filters.forEach { filter ->
                    DropdownMenuItem(
                        text = { Text(filter) },
                        onClick = {
                            selectedFilter = filter
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (notices.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("You haven't posted any updates yet.")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(notices) { notice ->
                    LecturerPostCard(notice, onDelete = { viewModel.deleteNotice(notice.id) })
                }
            }
        }
    }
}

@Composable
fun LecturerPostCard(notice: NoticeEntity, onDelete: () -> Unit) {
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(notice.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NDU_Dark_Purple)
                    Text("Target: ${notice.targetRole}", fontSize = 12.sp, color = Color.Gray)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(notice.content, fontSize = 14.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { /* TODO: Edit */ }) {
                    Text("Edit", color = NDU_Dark_Purple)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { /* TODO: View Comments */ },
                    colors = ButtonDefaults.buttonColors(containerColor = NDU_Light_Pink),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.stat_notify_chat),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = NDU_Dark_Purple
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Comments", color = NDU_Dark_Purple, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LecturerDashboardScreenPreview() {
    com.ndejje.nduupdates.ui.theme.NduUpdatesTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LecturerHomeScreen()
        }
    }
}
