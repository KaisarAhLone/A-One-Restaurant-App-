package com.example.aonerestaurant.ui.screen.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onSkipLogin: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onOwnerLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showOwnerDialog by remember { mutableStateOf(false) }
    var ownerKeyInput by remember { mutableStateOf("") }
    var keyError by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                IconButton(
                    onClick = { showOwnerDialog = true },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Owner Login",
                        tint = Color.Transparent
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                LoginHeader()

                Spacer(modifier = Modifier.height(36.dp))

                LoginForm(
                    onLoginClick = { email, password ->
                        onLoginSuccess()
                    },
                    onNavigateToSignUp = onNavigateToSignUp
                )

                Spacer(modifier = Modifier.height(28.dp))

                SocialLoginSection(
                    onGoogleSignInClick = onGoogleSignIn,
                    onSkipClick = onSkipLogin
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (showOwnerDialog) {
            AlertDialog(
                onDismissRequest = {
                    showOwnerDialog = false
                    ownerKeyInput = ""
                    keyError = false
                },
                title = { Text(text = "Restaurant Owner Portal") },
                text = {
                    Column {
                        Text(text = "Enter security key to access owner dashboard:")
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = ownerKeyInput,
                            onValueChange = {
                                ownerKeyInput = it
                                keyError = false
                            },
                            label = { Text(text = "Security Key") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            isError = keyError,
                            supportingText = if (keyError) {
                                { Text(text = "Incorrect Key") }
                            } else null
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (ownerKeyInput == "KAIsarAone") {
                                showOwnerDialog = false
                                ownerKeyInput = ""
                                keyError = false
                                onOwnerLoginSuccess()
                            } else {
                                keyError = true
                            }
                        }
                    ) {
                        Text(text = "Login")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showOwnerDialog = false
                            ownerKeyInput = ""
                            keyError = false
                        }
                    ) {
                        Text(text = "Cancel")
                    }
                }
            )
        }
    }
}
