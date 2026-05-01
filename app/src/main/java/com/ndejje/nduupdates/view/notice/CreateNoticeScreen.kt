package com.ndejje.nduupdates.view.notice

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.ndejje.nduupdates.R
import com.ndejje.nduupdates.data.model.NoticeEntity
import com.ndejje.nduupdates.viewmodel.AuthViewModel
import com.ndejje.nduupdates.viewmodel.NoticeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNoticeScreen(
    navController: NavHostController,
    noticeViewModel: NoticeViewModel,
    authViewModel: AuthViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val createNoticeState by noticeViewModel.createNoticeState.collectAsState()
    
    CreateNoticeContent(
        state = createNoticeState,
        onTitleChange = noticeViewModel::onTitleChange,
        onContentChange = noticeViewModel::onContentChange,
        onTargetRoleChange = noticeViewModel::onTargetRoleChange,
        onBack = { navController.popBackStack() },
        onPost = {
            val user = currentUser
            if (user != null) {
                noticeViewModel.addNotice(
                    author = user.username,
                    authorRole = user.role
                )
                navController.popBackStack()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNoticeContent(
    state: com.ndejje.nduupdates.viewmodel.CreateNoticeState,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onTargetRoleChange: (String) -> Unit,
    onBack: () -> Unit,
    onPost: () -> Unit
) {
    val roleAll = stringResource(R.string.role_all)
    val roleStudent = stringResource(R.string.role_student)
    val roleStaff = stringResource(R.string.role_staff)
    
    var expanded by remember { mutableStateOf(false) }
    val roles = listOf(roleAll, roleStudent, roleStaff)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_create_notice), style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_description_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(dimensionResource(R.dimen.spacingMedium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacingMedium))
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = onTitleChange,
                label = { Text(stringResource(R.string.label_title)) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge
            )

            OutlinedTextField(
                value = state.content,
                onValueChange = onContentChange,
                label = { Text(stringResource(R.string.label_content)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.content_text_height)),
                minLines = 3,
                textStyle = MaterialTheme.typography.bodyLarge
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = state.targetRole,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.label_audience)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    roles.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role) },
                            onClick = {
                                onTargetRoleChange(role)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = onPost,
                modifier = Modifier.fillMaxWidth().height(dimensionResource(R.dimen.buttonHeight)),
                enabled = state.title.isNotBlank() && state.content.isNotBlank()
            ) {
                Text(stringResource(R.string.btn_post_notice), style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Preview(showBackground = true, name = "Create Notice Preview")
@Composable
fun CreateNoticeScreenPreview() {
    com.ndejje.nduupdates.ui.theme.NduUpdatesTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            CreateNoticeContent(
                state = com.ndejje.nduupdates.viewmodel.CreateNoticeState(),
                onTitleChange = {},
                onContentChange = {},
                onTargetRoleChange = {},
                onBack = {},
                onPost = {}
            )
        }
    }
}
