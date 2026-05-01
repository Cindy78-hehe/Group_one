package com.ndejje.nduupdates.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ndejje.nduupdates.R
import com.ndejje.nduupdates.Routes
import com.ndejje.nduupdates.viewmodel.AuthUiState
import com.ndejje.nduupdates.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel
) {
    val authState by viewModel.uiState.collectAsState()
    val loginState by viewModel.loginState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthUiState.Success) {
            val user = currentUser
            if (user != null) {
                when (user.role) {
                    "ADMIN" -> navController.navigate(Routes.ADMIN_DASHBOARD)
                    "STAFF" -> navController.navigate(Routes.LECTURER_DASHBOARD)
                    else -> navController.navigate(Routes.STUDENT_DASHBOARD)
                }
                viewModel.resetState()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.screenPadding)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.nduupdates333),
            contentDescription = stringResource(R.string.content_description_logo),
            modifier = Modifier
                .size(dimensionResource(R.dimen.logoSize))
                .padding(dimensionResource(R.dimen.spacingSmall)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(dimensionResource(R.dimen.spacingMedium)))
        Text(
            text = stringResource(R.string.label_login),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(dimensionResource(R.dimen.spacingLarge)))

        OutlinedTextField(
            value = loginState.email,
            onValueChange = { viewModel.onLoginEmailChange(it) },
            label = { Text(stringResource(R.string.label_email)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(Modifier.height(dimensionResource(R.dimen.spacingMedium)))

        OutlinedTextField(
            value = loginState.pass,
            onValueChange = { viewModel.onLoginPasswordChange(it) },
            label = { Text(stringResource(R.string.label_password)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(Modifier.height(dimensionResource(R.dimen.spacingSmall)))

        if (authState is AuthUiState.Error) {
            Text(
                text = (authState as AuthUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(dimensionResource(R.dimen.spacingSmall)))
        }

        Button(
            onClick = { viewModel.login() },
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.buttonHeight)),
            enabled = authState !is AuthUiState.Loading
        ) {
            if (authState is AuthUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_size_medium)),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(stringResource(R.string.btn_login), style = MaterialTheme.typography.labelLarge)
            }
        }
        Spacer(Modifier.height(dimensionResource(R.dimen.spacingMedium)))
        
        TextButton(onClick = { 
            viewModel.resetState()
            navController.navigate(Routes.REGISTER) 
        }) {
            Text(stringResource(R.string.link_register))
        }
    }
}

@Preview(showBackground = true, name = "Login Screen Preview")
@Composable
fun LoginScreenPreview() {
    com.ndejje.nduupdates.ui.theme.NduUpdatesTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(R.dimen.screenPadding)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.nduupdates333),
                    contentDescription = "NDU Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(8.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(dimensionResource(R.dimen.spacingMedium)))
                Text(
                    text = stringResource(R.string.label_login),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(dimensionResource(R.dimen.spacingLarge)))

                OutlinedTextField(
                    value = "", onValueChange = {},
                    label = { Text(stringResource(R.string.label_email)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(dimensionResource(R.dimen.spacingMedium)))

                OutlinedTextField(
                    value = "", onValueChange = {},
                    label = { Text(stringResource(R.string.label_password)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(dimensionResource(R.dimen.spacingMedium)))

                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(dimensionResource(R.dimen.buttonHeight))
                ) {
                    Text(stringResource(R.string.btn_login))
                }
                Spacer(Modifier.height(dimensionResource(R.dimen.spacingSmall)))
                TextButton(onClick = {}) {
                    Text(stringResource(R.string.link_register))
                }
            }
        }
    }
}
