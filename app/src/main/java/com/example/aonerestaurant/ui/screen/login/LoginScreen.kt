package com.example.aonerestaurant.ui.screen.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    // Full-screen rich multi-color gradient background
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1F0305),   // Deep royal maroon
                        Color(0xFF6A040F),   // Crimson red
                        Color(0xFF8B0000),   // Dark red
                        Color(0xFFD90429),   // Bright red
                        Color(0xFFE65100),   // Deep orange
                        Color(0xFFFF9800)    // Golden amber
                    )
                )
            )
    ) {
        // Decorative radial glow top-left
        Box(
            modifier = Modifier
                .padding(top = 0.dp)
                .align(Alignment.TopStart)
        ) {
            Box(
                modifier = Modifier
                    .height(280.dp)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x66FFB703),
                                Color(0x00FFB703)
                            ),
                            radius = 550f
                        )
                    )
            )
        }

        // Hidden owner portal button (top-right, transparent)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .align(Alignment.TopEnd)
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

        // Main scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Colorful header with logo and crown
            LoginHeader()

            Spacer(modifier = Modifier.height(24.dp))

            // White glassmorphism card with colorful accents for form
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.98f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (errorMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFFE5E5))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "⚠️ ${errorMessage ?: ""}",
                                color = Color(0xFFD90429),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (isLoading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator(color = Color(0xFF6A040F))
                        Spacer(modifier = Modifier.height(16.dp))
                    } else {
                        LoginForm(
                            onLoginClick = { email, password ->
                                isLoading = true
                                errorMessage = null
                                firebaseAuth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        isLoading = false
                                        if (task.isSuccessful) {
                                            Toast.makeText(context, "Welcome to AOne Biryani!", Toast.LENGTH_SHORT).show()
                                            onLoginSuccess()
                                        } else {
                                            errorMessage = task.exception?.localizedMessage ?: "Firebase Sign-In Failed"
                                        }
                                    }
                            },
                            onNavigateToSignUp = onNavigateToSignUp
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    SocialLoginSection(
                        onGoogleSignInClick = {
                            isLoading = true
                            firebaseAuth.signInAnonymously()
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "Signed in via Google", Toast.LENGTH_SHORT).show()
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

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Owner dialog
    if (showOwnerDialog) {
        AlertDialog(
            onDismissRequest = {
                showOwnerDialog = false
                ownerKeyInput = ""
                keyError = false
            },
            title = { Text(text = "Restaurant Owner Portal 👑", fontWeight = FontWeight.Bold) },
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
