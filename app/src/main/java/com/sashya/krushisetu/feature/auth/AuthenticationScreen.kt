package com.sashya.krushisetu.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sashya.krushisetu.data.auth.AuthenticationResult
import com.sashya.krushisetu.data.auth.FirebaseAuthRepository
import com.sashya.krushisetu.ui.theme.FieldCream
import com.sashya.krushisetu.ui.theme.LeafGreen
import com.sashya.krushisetu.ui.theme.MutedText

private enum class AuthenticationMode {
    LOGIN,
    REGISTER
}

@Composable
fun AuthenticationScreen(
    authRepository: FirebaseAuthRepository,
    onAuthenticated: (String) -> Unit,
    onContinueAsGuest: () -> Unit
) {
    var modeName by remember { mutableStateOf(AuthenticationMode.LOGIN.name) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Language selection
    var selectedLanguage by remember { mutableStateOf("English") }
    var languageExpanded by remember { mutableStateOf(false) }

    // Role selection
    var selectedRole by remember { mutableStateOf("Farmer") }
    var roleExpanded by remember { mutableStateOf(false) }

    // Existing password visibility
    var passwordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val mode = AuthenticationMode.valueOf(modeName)
    val isRegistering = mode == AuthenticationMode.REGISTER

    fun handleResult(result: AuthenticationResult) {
        isLoading = false
        when (result) {
            is AuthenticationResult.Success -> onAuthenticated(selectedRole)
            is AuthenticationResult.Failure -> errorMessage = result.message
        }
    }

    fun submit() {
        val validationError = validateForm(name, email, password, isRegistering)

        if (validationError != null) {
            errorMessage = validationError
            return
        }

        errorMessage = null
        isLoading = true

        if (isRegistering) {
            authRepository.register(
                name,
                email,
                password,
                ::handleResult
            )
        } else {
            authRepository.signIn(
                email,
                password,
                ::handleResult
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FieldCream)
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🌾", fontSize = 60.sp)

        Text(
            text = "KrushiSetu",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = LeafGreen
        )

        Text(
            text = if (isRegistering) {
                "Create your farmer account"
            } else {
                "Welcome back, farmer"
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MutedText,
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(Modifier.height(28.dp))

        if (errorMessage != null) {
            ErrorMessage(errorMessage!!)
            Spacer(Modifier.height(12.dp))
        }

        if (isRegistering) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Full name") },
                placeholder = { Text("Example: Suresh Patil") }
            )

            Spacer(Modifier.height(12.dp))
        }

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Email address") },
            placeholder = { Text("name@example.com") }
        )

        Spacer(Modifier.height(12.dp))

        // Password - restored from your original UI
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Password") },
            supportingText = {
                if (isRegistering) {
                    Text("Use at least 6 characters")
                }
            },
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                TextButton(
                    onClick = {
                        passwordVisible = !passwordVisible
                    }
                ) {
                    Text(
                        if (passwordVisible) {
                            "Hide"
                        } else {
                            "Show"
                        }
                    )
                }
            }
        )

        Spacer(Modifier.height(12.dp))

        // ---------------------------------------------------------
        // LANGUAGE SELECTION
        // Added below password
        // ---------------------------------------------------------

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = {
                    languageExpanded = true
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🌐  $selectedLanguage"
                    )

                    Text(
                        text = if (languageExpanded) "▲" else "▼",
                        color = LeafGreen
                    )
                }
            }

            DropdownMenu(
                expanded = languageExpanded,
                onDismissRequest = {
                    languageExpanded = false
                }
            ) {
                DropdownMenuItem(
                    text = {
                        Text("English")
                    },
                    onClick = {
                        selectedLanguage = "English"
                        languageExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("हिन्दी")
                    },
                    onClick = {
                        selectedLanguage = "हिन्दी"
                        languageExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("मराठी")
                    },
                    onClick = {
                        selectedLanguage = "मराठी"
                        languageExpanded = false
                    }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // ---------------------------------------------------------
        // ROLE SELECTION
        // Added below language
        // ---------------------------------------------------------

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = {
                    roleExpanded = true
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "👤  $selectedRole"
                    )

                    Text(
                        text = if (roleExpanded) "▲" else "▼",
                        color = LeafGreen
                    )
                }
            }

            DropdownMenu(
                expanded = roleExpanded,
                onDismissRequest = {
                    roleExpanded = false
                }
            ) {
                DropdownMenuItem(
                    text = {
                        Text("Farmer")
                    },
                    onClick = {
                        selectedRole = "Farmer"
                        roleExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Advisor")
                    },
                    onClick = {
                        selectedRole = "Advisor"
                        roleExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Supplier")
                    },
                    onClick = {
                        selectedRole = "Supplier"
                        roleExpanded = false
                    }
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Existing Sign In / Create Account button
        Button(
            onClick = ::submit,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LeafGreen
            )
        ) {
            Text(
                text = when {
                    isLoading -> "Please wait..."
                    isRegistering -> "Create account"
                    else -> "Sign in"
                },
                modifier = Modifier.padding(vertical = 5.dp),
                fontWeight = FontWeight.Bold
            )
        }

        // Existing Login / Register switch
        TextButton(
            onClick = {
                modeName = if (isRegistering) {
                    AuthenticationMode.LOGIN.name
                } else {
                    AuthenticationMode.REGISTER.name
                }

                errorMessage = null
            }
        ) {
            Text(
                if (isRegistering) {
                    "Already have an account? Sign in"
                } else {
                    "New farmer? Create an account"
                }
            )
        }

        Spacer(Modifier.height(12.dp))

        // Existing Guest button
        OutlinedButton(
            onClick = onContinueAsGuest,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Explore the prototype as guest")
        }

        // Existing Firebase information
        Text(
            text = "Email and password are protected by Firebase Authentication.",
            style = MaterialTheme.typography.labelSmall,
            color = MutedText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
private fun ErrorMessage(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEDEA)
        )
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(14.dp),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private fun validateForm(
    name: String,
    email: String,
    password: String,
    isRegistering: Boolean
): String? {
    if (isRegistering && name.trim().isEmpty()) {
        return "Please enter your name."
    }

    if (!email.contains("@") || !email.contains(".")) {
        return "Enter a valid email address."
    }

    if (password.length < 6) {
        return "Password must contain at least 6 characters."
    }

    return null
}
