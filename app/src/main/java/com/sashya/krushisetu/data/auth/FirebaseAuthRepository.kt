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
    // GET CURRENT USER PROFILE
    // =========================================================

    fun getCurrentUserProfile(
        onResult: (Result<UserProfile>) -> Unit
    ) {

        val auth = firebaseAuth

        if (auth == null) {
            onResult(
                Result.failure(
                    Exception(
                        FIREBASE_NOT_CONFIGURED_MESSAGE
                    )
                )
            )
            return
        }

        val database = firestore

        if (database == null) {
            onResult(
                Result.failure(
                    Exception(
                        "Database is not connected yet."
                    )
                )
            )
            return
        }

        val user = auth.currentUser

        if (user == null) {
            onResult(
                Result.failure(
                    Exception(
                        "No user is currently signed in."
                    )
                )
            )
            return
        }

        database
            .collection("users")
            .document(user.uid)
            .get()
            .addOnCompleteListener { task ->

                if (!task.isSuccessful) {

                    onResult(
                        Result.failure(
                            task.exception
                                ?: Exception(
                                    "Unable to load your profile."
                                )
                        )
                    )

                    return@addOnCompleteListener
                }

                val document = task.result

                if (document == null || !document.exists()) {

                    onResult(
                        Result.failure(
                            Exception(
                                "Your account profile could not be found."
                            )
                        )
                    )

                    return@addOnCompleteListener
                }

                // -------------------------------------------------
                // Read role
                // -------------------------------------------------

                val storedRole =
                    document.getString("role")
                        ?.let {
                            try {
                                UserRole.valueOf(
                                    it.uppercase()
                                )
                            } catch (
                                exception: IllegalArgumentException
                            ) {
                                UserRole.FARMER
                            }
                        }
                        ?: UserRole.FARMER

                // -------------------------------------------------
                // Read farm coordinates
                //
                // Firestore may return numbers as Long or Double,
                // so we convert using Number.
                // -------------------------------------------------

                val farmLatitude =
                    (document.get("farmLatitude") as? Number)
                        ?.toDouble()

                val farmLongitude =
                    (document.get("farmLongitude") as? Number)
                        ?.toDouble()

                // -------------------------------------------------
                // Build complete UserProfile
                // -------------------------------------------------

                val profile = UserProfile(

                    // Common information

                    name =
                        document.getString("name")
                            ?: user.displayName
                            ?: "",

                    email =
                        document.getString("email")
                            ?: user.email
                            ?: "",

                    phone =
                        document.getString("phone")
                            ?: "",

                    location =
                        document.getString("location")
                            ?: "",

                    role =
                        storedRole,

                    // -------------------------------------------------
                    // Farmer information
                    // -------------------------------------------------

                    village =
                        document.getString("village")
                            ?: "",

                    district =
                        document.getString("district")
                            ?: "",

                    farmLocation =
                        document.getString("farmLocation")
                            ?: "",

                    farmLatitude =
                        farmLatitude,

                    farmLongitude =
                        farmLongitude,

                    numberOfFarms =
                        document.getLong("numberOfFarms")
                            ?.toInt()
                            ?: 0,

                    totalAreaAcres =
                        (document.get("totalAreaAcres") as? Number)
                            ?.toDouble()
                            ?: 0.0,

                    // -------------------------------------------------
                    // Advisor information
                    // -------------------------------------------------

                    organizationName =
                        document.getString("organizationName")
                            ?: "",

                    expertise =
                        document.getString("expertise")
                            ?: "",

                    experience =
                        document.getString("experience")
                            ?: "",

                    // -------------------------------------------------
                    // Supplier information
                    // -------------------------------------------------

                    companyName =
                        document.getString("companyName")
                            ?: "",

                    branchLocations =
                        document.getString("branchLocations")
                            ?: "",

                    businessType =
                        document.getString("businessType")
                            ?: "",

                    contactPerson =
                        document.getString("contactPerson")
                            ?: ""
                )

                onResult(
                    Result.success(profile)
                )
            }
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

            val user = auth.currentUser

            if (user == null) {

                onResult(
                    AuthenticationResult.Failure(
                        "Unable to load your account. Please try again."
                    )
                )

                return@addOnCompleteListener
            }

            database
                .collection("users")
                .document(user.uid)
                .get()
                .addOnCompleteListener { profileTask ->

                    if (!profileTask.isSuccessful) {

                        auth.signOut()

                        onResult(
                            AuthenticationResult.Failure(
                                profileTask.exception
                                    ?.localizedMessage
                                    ?: "Unable to load your profile."
                            )
                        )

                        return@addOnCompleteListener
                    }

                    val document =
                        profileTask.result

                    if (
                        document == null ||
                        !document.exists()
                    ) {

                        auth.signOut()

                        onResult(
                            AuthenticationResult.Failure(
                                "Your account profile could not be found."
                            )
                        )

                        return@addOnCompleteListener
                    }

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

                    // -------------------------------------------------
                    // Validate selected role
                    // -------------------------------------------------

                    if (
                        !storedRole.equals(
                            selectedRole,
                            ignoreCase = true
                        )
                    ) {

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
                                profileTask.exception
                                    ?.localizedMessage
                                    ?: "Account was created, but the profile name could not be saved."
                            )
                        )

                        return@addOnCompleteListener
                    }

                    // =================================================
                    // SAVE USER PROFILE IN FIRESTORE
                    // =================================================

                    val profileData =
                        hashMapOf<String, Any?>(

                            // -----------------------------------------
                            // Common
                            // -----------------------------------------

                            "uid" to user.uid,

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

                            // IMPORTANT:
                            // These are the coordinates that HomeScreen
                            // will use for weather.

                            "farmLatitude" to profile.farmLatitude,

                            "farmLongitude" to profile.farmLongitude,

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
                                        role = profile.role.name
                                    )
                                )

                            } else {

                                onResult(
                                    AuthenticationResult.Failure(
                                        firestoreTask.exception
                                            ?.localizedMessage
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
