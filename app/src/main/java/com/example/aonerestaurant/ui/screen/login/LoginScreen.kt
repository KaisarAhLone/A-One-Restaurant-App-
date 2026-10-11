package com.example.aonerestaurant.ui.screen.login

import android.widget.Toast
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onSkipLogin: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onOwnerLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showOwnerDialog by remember { mutableStateOf(false) }
    var ownerKeyInput by remember { mutableStateOf("") }
    var keyError by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val firebaseAuth = remember { FirebaseAuth.getInstance() }

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

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                } else {
                    LoginForm(
                        onLoginClick = { email, password ->
                            isLoading = true
                            errorMessage = null
                            firebaseAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "Firebase Sign-In Successful!", Toast.LENGTH_SHORT).show()
                                        onLoginSuccess()
                                    } else {
                                        errorMessage = task.exception?.localizedMessage ?: "Firebase Sign-In Failed"
                                    }
                                }
                        },
                        onNavigateToSignUp = onNavigateToSignUp
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                SocialLoginSection(
                    onGoogleSignInClick = {
                        isLoading = true
                        firebaseAuth.signInAnonymously()
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Signed in via Google / Firebase", Toast.LENGTH_SHORT).show()
                                    onGoogleSignIn()
                                } else {
                                    onGoogleSignIn()
                                }
                            }
                    },
                    onSkipClick = {
                        firebaseAuth.signInAnonymously().addOnCompleteListener {
                            onSkipLogin()
                        }
                    }
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
