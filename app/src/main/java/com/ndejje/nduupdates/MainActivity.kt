package com.ndejje.nduupdates

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.ndejje.nduupdates.data.model.AppDatabase
import com.ndejje.nduupdates.data.repository.NoticeRepository
import com.ndejje.nduupdates.data.repository.UserRepository
import com.ndejje.nduupdates.ui.theme.NduUpdatesTheme
import com.ndejje.nduupdates.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize Room Database and Repositories
        val database = AppDatabase.getDatabase(this)
        val userRepository = UserRepository(database.userDao())
        val noticeRepository = NoticeRepository(database.noticeDao(), database.commentDao())
        val factory = ViewModelFactory(userRepository, noticeRepository)

        setContent {
            NduUpdatesTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavigation(factory = factory)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// PREVIEW
// Shows the Login screen's visual layout using static values.
// ─────────────────────────────────────────────────────────────
@Preview(showBackground = true, name = "Login Screen Preview")
@Composable
fun LoginScreenPreview() {
    NduUpdatesTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(R.dimen.screenPadding)),
                verticalArrangement   = Arrangement.Center,
                horizontalAlignment   = Alignment.CenterHorizontally
            ) {
                Text(
                    text      = stringResource(R.string.label_login),
                    style     = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacingLarge)))
                OutlinedTextField(
                    value         = "",
                    onValueChange = {},
                    label         = { Text(stringResource(R.string.label_email)) },
                    modifier      = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacingMedium)))
                OutlinedTextField(
                    value         = "",
                    onValueChange = {},
                    label         = { Text(stringResource(R.string.label_password)) },
                    modifier      = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacingMedium)))
                Button(
                    onClick  = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(R.dimen.buttonHeight))
                ) {
                    Text(stringResource(R.string.btn_login))
                }
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacingSmall)))
                TextButton(onClick = {}) {
                    Text(stringResource(R.string.link_register))
                }
            }
        }
    }
}
