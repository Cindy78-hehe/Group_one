package com.ndejje.nduupdates.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: AuthViewModel
) {
    val authState by viewModel.uiState.collectAsState()
    
    var fullNameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var confirmPassInput by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        if (authState is AuthUiState.Success) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(Routes.REGISTER) { inclusive = true }
            }
            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(dimensionResource(R.dimen.screenPadding)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.nduupdate33),
            contentDescription = "NDU Logo",
            modifier = Modifier
                .size(100.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(dimensionResource(R.dimen.spacingMedium)))
        Text(
            text = stringResource(R.string.label_register),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(dimensionResource(R.dimen.spacingLarge)))

        OutlinedTextField(
            value = fullNameInput,
            onValueChange = { fullNameInput = it },
            label = { Text(stringResource(R.string.label_full_name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(dimensionResource(R.dimen.spacingMedium)))

        OutlinedTextField(
            value = emailInput,
            onValueChange = { emailInput = it },
            label = { Text(stringResource(R.string.label_email)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(Modifier.height(dimensionResource(R.dimen.spacingMedium)))

        OutlinedTextField(
            value = passwordInput,
            onValueChange = { passwordInput = it },
            label = { Text(stringResource(R.string.label_password)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(Modifier.height(dimensionResource(R.dimen.spacingMedium)))

        OutlinedTextField(
            value = confirmPassInput,
            onValueChange = { confirmPassInput = it },
            label = { Text(stringResource(R.string.label_confirm_password)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(Modifier.height(dimensionResource(R.dimen.spacingMedium)))

        if (authState is AuthUiState.Error) {
            Text(
                text = (authState as AuthUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(dimensionResource(R.dimen.spacingSmall)))
        }

        Button(
            onClick = {
                val email = emailInput.trim().lowercase()
                
                // Basic validation
                if (fullNameInput.isBlank() || emailInput.isBlank() || passwordInput.isBlank()) {
                    // Ideally, you'd show a UI message here, but for now we'll just return
                    return@Button
                }
                
                if (passwordInput != confirmPassInput) {
                    // Handle password mismatch
                    return@Button
                }

                val dbRole = when {
                    email.endsWith("@admin.gmail.com") -> "ADMIN"
                    email.endsWith("@lect.gmail.com") -> "STAFF"
                    email.endsWith("@stud.gmail.com") -> "STUDENT"
                    else -> "STUDENT" // Default role
                }
                viewModel.register(fullNameInput, email, passwordInput, dbRole)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.buttonHeight)),
            enabled = authState !is AuthUiState.Loading
        ) {
            if (authState is AuthUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(stringResource(R.string.btn_register))
            }
        }
        Spacer(Modifier.height(dimensionResource(R.dimen.spacingMedium)))
        
        TextButton(onClick = { 
            viewModel.resetState()
            navController.navigate(Routes.LOGIN) 
        }) {
            Text(stringResource(R.string.link_back_to_login))
        }
    }
}

@Preview(showBackground = true, name = "Register Screen Preview")
@Composable
fun RegisterScreenPreview() {
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
                    painter = painterResource(id = R.drawable.nduupdate33),
                    contentDescription = "NDU Logo",
                    modifier = Modifier
                        .size(100.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(dimensionResource(R.dimen.spacingMedium)))
                Text(
                    text = stringResource(R.string.label_register),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(dimensionResource(R.dimen.spacingLarge)))

                OutlinedTextField(
                    value = "", onValueChange = {},
                    label = { Text(stringResource(R.string.label_full_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(dimensionResource(R.dimen.spacingMedium)))

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
                    Text(stringResource(R.string.btn_register))
                }
                Spacer(Modifier.height(dimensionResource(R.dimen.spacingSmall)))
                TextButton(onClick = {}) {
                    Text(stringResource(R.string.link_back_to_login))
                }
            }
        }
    }
}
