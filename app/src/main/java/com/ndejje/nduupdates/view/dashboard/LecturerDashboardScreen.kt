package com.ndejje.nduupdates.view.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.ndejje.nduupdates.R
import com.ndejje.nduupdates.Routes
import com.ndejje.nduupdates.data.model.NoticeEntity
import com.ndejje.nduupdates.ui.theme.NDU_Dark_Purple
import com.ndejje.nduupdates.ui.theme.NDU_Light_Pink
import com.ndejje.nduupdates.view.components.NoticeCard
import com.ndejje.nduupdates.view.components.ProfileDialog
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
    val tabs = listOf(
        stringResource(R.string.label_home),
        stringResource(R.string.label_notice),
        stringResource(R.string.label_news),
        stringResource(R.string.label_events)
    )
    val icons = listOf(Icons.Default.Home, Icons.AutoMirrored.Filled.List, Icons.Default.Info, Icons.Default.DateRange)
    var showProfileDialog by remember { mutableStateOf(false) }
    var showCommentsForNotice by remember { mutableStateOf<NoticeEntity?>(null) }

    val currentUser by authViewModel.currentUser.collectAsState()
    val notices by noticeViewModel.notices.collectAsState()

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
                        Text(
                            text = stringResource(R.string.title_ndu_updates),
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge
                        )
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
                LecturerHomeScreen()
            } else {
                val userRole = "Lecturer"
                val filteredNotices = notices.filter { notice ->
                    val typeMatch = if (type == "Notice") {
                        notice.type == "Notice" || notice.type == ""
                    } else {
                        notice.type == type
                    }
                    val targetMatch = notice.targetRole == "All" || notice.targetRole == userRole
                    typeMatch && targetMatch
                }

                LecturerPostsScreen(
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
fun LecturerHomeScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(dimensionResource(R.dimen.card_inner_padding)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_20)))
            Text(
                stringResource(R.string.title_lecturer_dashboard),
                style = MaterialTheme.typography.headlineMedium,
                color = NDU_Dark_Purple
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_8)))
            Text(
                stringResource(R.string.msg_welcome_back_lecturer),
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
                        text = stringResource(R.string.label_featured_update),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun LecturerPostsScreen(
    title: String,
    notices: List<NoticeEntity>,
    onViewComments: (NoticeEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(dimensionResource(R.dimen.card_inner_padding))) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = NDU_Dark_Purple)
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.card_inner_padding)))

        if (notices.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.msg_no_items_found, title), color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.card_inner_padding)),
                modifier = Modifier.fillMaxSize()
            ) {
                items(notices) { update ->
                    NoticeCard(
                        notice = update,
                        onComment = { onViewComments(update) }
                    )
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
