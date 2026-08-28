package com.sashya.krushisetu.data.auth

import android.content.Context

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

import com.sashya.krushisetu.data.model.UserProfile
import com.sashya.krushisetu.data.model.UserRole


// =============================================================
// AUTHENTICATION RESULT
// =============================================================

sealed interface AuthenticationResult {

    data class Success(
        val displayName: String?,
        val role: String
    ) : AuthenticationResult

    data class Failure(
        val message: String
    ) : AuthenticationResult
}


// =============================================================
// FIREBASE AUTH REPOSITORY
// =============================================================

class FirebaseAuthRepository(
    context: Context
) {

    // ---------------------------------------------------------
    // Firebase App
    // ---------------------------------------------------------

    private val firebaseApp: FirebaseApp? =
        FirebaseApp.initializeApp(context)


    // ---------------------------------------------------------
    // Firebase Authentication
    // ---------------------------------------------------------

    private val firebaseAuth: FirebaseAuth? =
        firebaseApp?.let {
            FirebaseAuth.getInstance(it)
        }


    // ---------------------------------------------------------
    // Firebase Firestore
    // ---------------------------------------------------------

    private val firestore: FirebaseFirestore? =
        firebaseApp?.let {
            FirebaseFirestore.getInstance(it)
        }


    // =========================================================
    // BASIC ACCOUNT INFORMATION
    // =========================================================

    fun isUserSignedIn(): Boolean {
        return firebaseAuth?.currentUser != null
    }


    fun currentUserEmail(): String? {
        return firebaseAuth?.currentUser?.email
    }


    fun currentUserName(): String? {
        return firebaseAuth?.currentUser?.displayName
    }


    // =========================================================
    // LOGIN
    // =========================================================

    fun signIn(
        email: String,
        password: String,
        selectedRole: String,
        onResult: (AuthenticationResult) -> Unit
    ) {

        val auth = firebaseAuth

        if (auth == null) {
            onResult(
                AuthenticationResult.Failure(
                    FIREBASE_NOT_CONFIGURED_MESSAGE
                )
            )
            return
        }


        val database = firestore

        if (database == null) {
            onResult(
                AuthenticationResult.Failure(
                    "Database is not connected yet."
                )
            )
            return
        }


        // -----------------------------------------------------
        // First authenticate using Firebase Authentication
        // -----------------------------------------------------

        auth.signInWithEmailAndPassword(
            email.trim(),
            password
        ).addOnCompleteListener { task ->

            if (!task.isSuccessful) {

                onResult(
                    AuthenticationResult.Failure(
                        task.exception?.localizedMessage
                            ?: "Unable to sign in. Please try again."
                    )
                )

                return@addOnCompleteListener
            }


            // -------------------------------------------------
            // Get authenticated Firebase user
            // -------------------------------------------------

            val user = auth.currentUser

            if (user == null) {

                onResult(
                    AuthenticationResult.Failure(
                        "Unable to load your account. Please try again."
                    )
                )

                return@addOnCompleteListener
            }


            // -------------------------------------------------
            // Load user's profile from Firestore
            // -------------------------------------------------

            database
                .collection("users")
                .document(user.uid)
                .get()
                .addOnCompleteListener { profileTask ->

                    if (!profileTask.isSuccessful) {

                        auth.signOut()

                        onResult(
                            AuthenticationResult.Failure(
                                profileTask.exception?.localizedMessage
                                    ?: "Unable to load your profile."
                            )
                        )

                        return@addOnCompleteListener
                    }


                    val document = profileTask.result


                    // -------------------------------------------------
                    // Profile must exist
                    // -------------------------------------------------

                    if (document == null || !document.exists()) {

                        auth.signOut()

                        onResult(
                            AuthenticationResult.Failure(
                                "Your account profile could not be found."
                            )
                        )

                        return@addOnCompleteListener
                    }


                    // -------------------------------------------------
                    // Get stored role
                    // -------------------------------------------------

                    val storedRole =
                        document.getString("role")


                    if (storedRole.isNullOrBlank()) {

                        auth.signOut()

                        onResult(
                            AuthenticationResult.Failure(
                                "Your account role is missing. Please contact support."
                            )
                        )

                        return@addOnCompleteListener
                    }


                    // =================================================
                    // CRITICAL ROLE VALIDATION
                    // =================================================

                    if (
                        !storedRole.equals(
                            selectedRole,
                            ignoreCase = true
                        )
                    ) {

                        // ---------------------------------------------
                        // IMPORTANT:
                        // Authentication succeeded, but the selected
                        // role is wrong.
                        //
                        // We immediately sign the user out.
                        // ---------------------------------------------

                        auth.signOut()

                        onResult(
                            AuthenticationResult.Failure(
                                "Please select the correct role. " +
                                        "This account is registered as " +
                                        formatRole(storedRole) +
                                        "."
                            )
                        )

                        return@addOnCompleteListener
                    }


                    // -------------------------------------------------
                    // Login successful
                    // -------------------------------------------------

                    onResult(
                        AuthenticationResult.Success(
                            displayName = user.displayName,
                            role = storedRole
                        )
                    )
                }
        }
    }


    // =========================================================
    // REGISTER
    // =========================================================

    fun register(
        profile: UserProfile,
        password: String,
        onResult: (AuthenticationResult) -> Unit
    ) {

        val auth = firebaseAuth

        if (auth == null) {

            onResult(
                AuthenticationResult.Failure(
                    FIREBASE_NOT_CONFIGURED_MESSAGE
                )
            )

            return
        }


        val database = firestore

        if (database == null) {

            onResult(
                AuthenticationResult.Failure(
                    "Database is not connected yet."
                )
            )

            return
        }


        // =====================================================
        // CREATE FIREBASE ACCOUNT
        // =====================================================

        auth.createUserWithEmailAndPassword(
            profile.email.trim(),
            password
        ).addOnCompleteListener { task ->

            if (!task.isSuccessful) {

                onResult(
                    AuthenticationResult.Failure(
                        task.exception?.localizedMessage
                            ?: "Unable to create your account."
                    )
                )

                return@addOnCompleteListener
            }


            // -------------------------------------------------
            // Get newly created user
            // -------------------------------------------------

            val user = auth.currentUser

            if (user == null) {

                onResult(
                    AuthenticationResult.Failure(
                        "Account was created, but the profile could not be loaded."
                    )
                )

                return@addOnCompleteListener
            }


            // =================================================
            // DETERMINE DISPLAY NAME
            // =================================================

            val displayName =
                when {

                    profile.name.isNotBlank() ->
                        profile.name.trim()

                    profile.contactPerson.isNotBlank() ->
                        profile.contactPerson.trim()

                    profile.companyName.isNotBlank() ->
                        profile.companyName.trim()

                    else ->
                        profile.email.substringBefore("@")
                }


            // =================================================
            // SAVE DISPLAY NAME IN FIREBASE AUTH
            // =================================================

            val authProfile =
                UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()


            user.updateProfile(authProfile)
                .addOnCompleteListener { profileTask ->

                    if (!profileTask.isSuccessful) {

                        onResult(
                            AuthenticationResult.Failure(
                                profileTask.exception?.localizedMessage
                                    ?: "Account was created, but the profile name could not be saved."
                            )
                        )

                        return@addOnCompleteListener
                    }


                    // =================================================
                    // SAVE USER PROFILE IN FIRESTORE
                    // =================================================

                    val profileData =
                        hashMapOf(

                            // -----------------------------------------
                            // Common
                            // -----------------------------------------

                            "uid" to user.uid,

                            // IMPORTANT:
                            // Firestore stores this as String
                            "role" to profile.role.name,

                            "name" to displayName,

                            "email" to profile.email.trim(),

                            "phone" to profile.phone.trim(),

                            "location" to profile.location.trim(),


                            // -----------------------------------------
                            // Farmer
                            // -----------------------------------------

                            "village" to profile.village.trim(),

                            "district" to profile.district.trim(),

                            "farmLocation" to profile.farmLocation.trim(),

                            "numberOfFarms" to profile.numberOfFarms,

                            "totalAreaAcres" to profile.totalAreaAcres,


                            // -----------------------------------------
                            // Advisor
                            // -----------------------------------------

                            "organizationName" to
                                    profile.organizationName.trim(),

                            "expertise" to
                                    profile.expertise.trim(),

                            "experience" to
                                    profile.experience.trim(),


                            // -----------------------------------------
                            // Supplier
                            // -----------------------------------------

                            "companyName" to
                                    profile.companyName.trim(),

                            "branchLocations" to
                                    profile.branchLocations.trim(),

                            "businessType" to
                                    profile.businessType.trim(),

                            "contactPerson" to
                                    profile.contactPerson.trim()
                        )


                    database
                        .collection("users")
                        .document(user.uid)
                        .set(profileData)
                        .addOnCompleteListener { firestoreTask ->

                            if (firestoreTask.isSuccessful) {

                                onResult(
                                    AuthenticationResult.Success(
                                        displayName = displayName,

                                        // Return actual stored role
                                        role = profile.role.name
                                    )
                                )

                            } else {

                                onResult(
                                    AuthenticationResult.Failure(
                                        firestoreTask.exception?.localizedMessage
                                            ?: "Account created, but your profile could not be saved."
                                    )
                                )
                            }
                        }
                }
        }
    }


    // =========================================================
    // SIGN OUT
    // =========================================================

    fun signOut() {
        firebaseAuth?.signOut()
    }


    // =========================================================
    // HELPER: FORMAT ROLE
    // =========================================================

    private fun formatRole(
        role: String
    ): String {

        return when (role.uppercase()) {

            UserRole.FARMER.name ->
                "Farmer"

            UserRole.ADVISOR.name ->
                "Advisor"

            UserRole.SUPPLIER.name ->
                "Supplier"

            else ->
                role
        }
    }


    // =========================================================
    // CONSTANTS
    // =========================================================

    private companion object {

        const val FIREBASE_NOT_CONFIGURED_MESSAGE =
            "Firebase is not connected yet. Add google-services.json and complete the Firebase setup steps."
    }
}