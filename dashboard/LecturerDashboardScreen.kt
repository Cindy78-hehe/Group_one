package com.ndejje.nduupdates.view.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ndejje.nduupdates.R
import com.ndejje.nduupdates.Routes
import com.ndejje.nduupdates.data.model.NoticeEntity
import com.ndejje.nduupdates.view.components.NoticeCard
import com.ndejje.nduupdates.viewmodel.NoticeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LecturerDashboardScreen(
    navController: NavHostController,
    viewModel: NoticeViewModel
) {
    val notices by viewModel.notices.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lecturer Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    TextButton(onClick = {
                        navController.navigate(Routes.WELCOME) {
                            popUpTo(0)
                        }
                    }) {
                        Text("Logout", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Routes.CREATE_NOTICE) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Text("Create New Notice")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(dimensionResource(R.dimen.spacingMedium))
        ) {
            Text(
                text = "Recent Notices",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.spacingSmall))
            )

            if (notices.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No notices yet.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacingSmall)),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(notices) { notice ->
                        NoticeCard(
                            notice = notice,
                            showDelete = true,
                            onDelete = { viewModel.deleteNotice(notice.id) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Lecturer Dashboard Preview")
@Composable
fun LecturerDashboardScreenPreview() {
    com.ndejje.nduupdates.ui.theme.NduUpdatesTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    @OptIn(ExperimentalMaterial3Api::class)
                    TopAppBar(
                        title = { Text("Lecturer Dashboard") },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(onClick = {}) {
                        Text("Create New Notice")
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Recent Notices",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val mockNotices = listOf(
                        NoticeEntity(id = 1, title = "Graduation List", content = "The graduation list is out, check it on the portal.", author = "Admin", authorRole = "ADMIN"),
                        NoticeEntity(id = 2, title = "Holiday Notice", content = "Campus will be closed from Friday to Monday.", author = "Admin", authorRole = "ADMIN")
                    )
                    
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(mockNotices) { notice ->
                            NoticeCard(notice = notice, showDelete = true)
                        }
                    }
                }
            }
        }
    }
}
