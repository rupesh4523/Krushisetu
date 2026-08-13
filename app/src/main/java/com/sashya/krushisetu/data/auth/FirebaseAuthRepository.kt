package com.sashya.krushisetu.data.auth

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

sealed interface AuthenticationResult {
    data class Success(val displayName: String?) : AuthenticationResult
    data class Failure(val message: String) : AuthenticationResult
}

class FirebaseAuthRepository(context: Context) {
    private val firebaseApp = FirebaseApp.initializeApp(context)
    private val firebaseAuth = firebaseApp?.let { FirebaseAuth.getInstance(it) }

    fun isUserSignedIn(): Boolean = firebaseAuth?.currentUser != null

    fun currentUserEmail(): String? = firebaseAuth?.currentUser?.email

    fun currentUserName(): String? = firebaseAuth?.currentUser?.displayName

    fun signIn(
        email: String,
        password: String,
        onResult: (AuthenticationResult) -> Unit
    ) {
        val auth = firebaseAuth ?: return onResult(AuthenticationResult.Failure(firebaseNotConfiguredMessage))
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(AuthenticationResult.Success(auth.currentUser?.displayName))
                } else {
                    onResult(AuthenticationResult.Failure(task.exception?.localizedMessage ?: "Unable to sign in. Please try again."))
                }
            }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        onResult: (AuthenticationResult) -> Unit
    ) {
        val auth = firebaseAuth ?: return onResult(AuthenticationResult.Failure(firebaseNotConfiguredMessage))
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    onResult(AuthenticationResult.Failure(task.exception?.localizedMessage ?: "Unable to create your account."))
                    return@addOnCompleteListener
                }

                val user = auth.currentUser
                if (user == null) {
                    onResult(AuthenticationResult.Failure("Account was created, but the profile could not be loaded. Please sign in again."))
                    return@addOnCompleteListener
                }

                val profile = UserProfileChangeRequest.Builder()
                    .setDisplayName(name.trim())
                    .build()
                user.updateProfile(profile).addOnCompleteListener { profileTask ->
                    if (profileTask.isSuccessful) {
                        onResult(AuthenticationResult.Success(name.trim()))
                    } else {
                        onResult(
                            AuthenticationResult.Failure(
                                profileTask.exception?.localizedMessage
                                    ?: "Account was created, but the profile name could not be saved."
                            )
                        )
                    }
                }
            }
    }

    fun signOut() {
        firebaseAuth?.signOut()
    }

    private companion object {
        const val firebaseNotConfiguredMessage =
            "Firebase is not connected yet. Add google-services.json and complete the Firebase setup steps."
    }
}
