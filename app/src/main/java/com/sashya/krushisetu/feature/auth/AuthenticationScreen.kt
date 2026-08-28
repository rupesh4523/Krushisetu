package com.sashya.krushisetu.feature.auth

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sashya.krushisetu.data.auth.AuthenticationResult
import com.sashya.krushisetu.data.auth.FirebaseAuthRepository
import com.sashya.krushisetu.data.model.UserProfile
import com.sashya.krushisetu.data.model.UserRole
import com.sashya.krushisetu.ui.theme.FieldCream
import com.sashya.krushisetu.ui.theme.LeafGreen
import com.sashya.krushisetu.ui.theme.MutedText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

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

    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    // =========================================================
    // AUTHENTICATION MODE
    // =========================================================

    var modeName by remember {
        mutableStateOf(AuthenticationMode.LOGIN.name)
    }

    // =========================================================
    // ROLE
    // =========================================================

    var selectedRole by remember {
        mutableStateOf(UserRole.FARMER)
    }

    var roleMenuExpanded by remember {
        mutableStateOf(false)
    }

    // =========================================================
    // COMMON FIELDS
    // =========================================================

    var name by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var phone by remember {
        mutableStateOf("")
    }

    var location by remember {
        mutableStateOf("")
    }

    // =========================================================
    // FARMER FIELDS
    // =========================================================

    var village by remember {
        mutableStateOf("")
    }

    var district by remember {
        mutableStateOf("")
    }

    var farmLocation by remember {
        mutableStateOf("")
    }

    var numberOfFarms by remember {
        mutableStateOf("")
    }

    var totalAreaAcres by remember {
        mutableStateOf("")
    }

    // =========================================================
    // ADVISOR FIELDS
    // =========================================================

    var organizationName by remember {
        mutableStateOf("")
    }

    var expertise by remember {
        mutableStateOf("")
    }

    var experience by remember {
        mutableStateOf("")
    }

    // =========================================================
    // SUPPLIER FIELDS
    // =========================================================

    var companyName by remember {
        mutableStateOf("")
    }

    var branchLocations by remember {
        mutableStateOf("")
    }

    var businessType by remember {
        mutableStateOf("")
    }

    var contactPerson by remember {
        mutableStateOf("")
    }

    // =========================================================
    // UI STATE
    // =========================================================

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    val mode = AuthenticationMode.valueOf(modeName)

    val isRegistering =
        mode == AuthenticationMode.REGISTER

    // =========================================================
    // HANDLE FIREBASE RESULT
    // =========================================================

    fun handleResult(
        result: AuthenticationResult
    ) {

        isLoading = false

        when (result) {

            is AuthenticationResult.Success -> {

                onAuthenticated(
                    result.role
                )
            }

            is AuthenticationResult.Failure -> {

                errorMessage =
                    result.message
            }
        }
    }

    // =========================================================
    // SUBMIT
    // =========================================================

    fun submit() {

        // =====================================================
        // LOGIN
        // =====================================================

        if (!isRegistering) {

            if (email.trim().isEmpty()) {

                errorMessage =
                    "Please enter your email address."

                return
            }

            if (password.length < 6) {

                errorMessage =
                    "Password must contain at least 6 characters."

                return
            }

            errorMessage = null

            isLoading = true

            authRepository.signIn(
                email = email.trim(),
                password = password,
                selectedRole = selectedRole.name,
                onResult = ::handleResult
            )

            return
        }

        // =====================================================
        // REGISTRATION VALIDATION
        // =====================================================

        val validationError =
            validateRegistration(

                selectedRole = selectedRole,

                name = name,

                email = email,

                password = password,

                phone = phone,

                village = village,

                district = district,

                farmLocation = farmLocation,

                numberOfFarms = numberOfFarms,

                totalAreaAcres = totalAreaAcres,

                organizationName = organizationName,

                expertise = expertise,

                experience = experience,

                companyName = companyName,

                branchLocations = branchLocations,

                businessType = businessType,

                contactPerson = contactPerson
            )

        if (validationError != null) {

            errorMessage =
                validationError

            return
        }

        errorMessage = null

        isLoading = true

        // =====================================================
        // FARMER REGISTRATION
        // =====================================================

        if (selectedRole == UserRole.FARMER) {

            coroutineScope.launch {

                /*
                 * Build a better address for the geocoder.
                 *
                 * Example:
                 *
                 * Farm location = Baramati
                 * Village       = Malegaon
                 * District      = Pune
                 *
                 * Search:
                 *
                 * Baramati, Malegaon, Pune,
                 * Maharashtra, India
                 */

                val searchAddress =
                    buildString {

                        if (farmLocation.trim().isNotEmpty()) {

                            append(
                                farmLocation.trim()
                            )
                        }

                        if (village.trim().isNotEmpty()) {

                            if (isNotEmpty()) {
                                append(", ")
                            }

                            append(
                                village.trim()
                            )
                        }

                        if (district.trim().isNotEmpty()) {

                            if (isNotEmpty()) {
                                append(", ")
                            }

                            append(
                                district.trim()
                            )
                        }

                        if (isNotEmpty()) {
                            append(", ")
                        }

                        append("Maharashtra, India")
                    }

                // =================================================
                // FIND FARM COORDINATES
                // =================================================

                val coordinates =
                    withContext(Dispatchers.IO) {

                        getCoordinatesFromAddress(
                            context = context,
                            addressText = searchAddress
                        )
                    }

                // =================================================
                // LOCATION NOT FOUND
                // =================================================

                if (coordinates == null) {

                    isLoading = false

                    errorMessage =
                        "We could not find the farm location. " +
                                "Please enter a recognizable location " +
                                "such as your village, town, or nearby landmark."

                    return@launch
                }

                // =================================================
                // CREATE FARMER PROFILE
                // =================================================

                val profile =
                    UserProfile(

                        // Common
                        name = name.trim(),

                        email = email.trim(),

                        phone = phone.trim(),

                        location =
                            "${village.trim()}, ${district.trim()}",

                        role = selectedRole,

                        // =================================================
                        // FARMER
                        // =================================================

                        village =
                            village.trim(),

                        district =
                            district.trim(),

                        farmLocation =
                            farmLocation.trim(),

                        farmLatitude =
                            coordinates.first,

                        farmLongitude =
                            coordinates.second,

                        numberOfFarms =
                            numberOfFarms.toIntOrNull()
                                ?: 0,

                        totalAreaAcres =
                            totalAreaAcres.toDoubleOrNull()
                                ?: 0.0,

                        // =================================================
                        // ADVISOR
                        // =================================================

                        organizationName =
                            organizationName.trim(),

                        expertise =
                            expertise.trim(),

                        experience =
                            experience.trim(),

                        // =================================================
                        // SUPPLIER
                        // =================================================

                        companyName =
                            companyName.trim(),

                        branchLocations =
                            branchLocations.trim(),

                        businessType =
                            businessType.trim(),

                        contactPerson =
                            contactPerson.trim()
                    )

                // =================================================
                // REGISTER FARMER
                // =================================================

                authRepository.register(

                    profile = profile,

                    password = password,

                    onResult = ::handleResult
                )
            }

        } else {

            // =====================================================
            // ADVISOR / SUPPLIER REGISTRATION
            // =====================================================

            val profile =
                UserProfile(

                    // =================================================
                    // COMMON
                    // =================================================

                    name =
                        when (selectedRole) {

                            UserRole.ADVISOR ->
                                name.trim()

                            UserRole.SUPPLIER ->
                                contactPerson.trim()

                            UserRole.FARMER ->
                                name.trim()
                        },

                    email =
                        email.trim(),

                    phone =
                        phone.trim(),

                    location =
                        location.trim(),

                    role =
                        selectedRole,

                    // =================================================
                    // FARMER
                    // =================================================

                    village =
                        village.trim(),

                    district =
                        district.trim(),

                    farmLocation =
                        farmLocation.trim(),

                    numberOfFarms =
                        numberOfFarms.toIntOrNull()
                            ?: 0,

                    totalAreaAcres =
                        totalAreaAcres.toDoubleOrNull()
                            ?: 0.0,

                    // =================================================
                    // ADVISOR
                    // =================================================

                    organizationName =
                        organizationName.trim(),

                    expertise =
                        expertise.trim(),

                    experience =
                        experience.trim(),

                    // =================================================
                    // SUPPLIER
                    // =================================================

                    companyName =
                        companyName.trim(),

                    branchLocations =
                        branchLocations.trim(),

                    businessType =
                        businessType.trim(),

                    contactPerson =
                        contactPerson.trim()
                )

            authRepository.register(

                profile = profile,

                password = password,

                onResult = ::handleResult
            )
        }
    }

    // =========================================================
    // SCREEN
    // =========================================================

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(FieldCream)
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                horizontal = 24.dp,
                vertical = 20.dp
            ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        // =====================================================
        // LOGO
        // =====================================================

        Text(
            text = "🌾",
            fontSize = 60.sp
        )

        Text(
            text = "KrushiSetu",
            style =
                MaterialTheme.typography.headlineMedium,
            fontWeight =
                FontWeight.ExtraBold,
            color = LeafGreen
        )

        Text(

            text =
                if (isRegistering)
                    "Create your KrushiSetu account"
                else
                    "Welcome back, farmer",

            style =
                MaterialTheme.typography.bodyLarge,

            color = MutedText,

            modifier =
                Modifier.padding(top = 6.dp)
        )

        Spacer(
            Modifier.height(24.dp)
        )

        // =====================================================
        // ERROR
        // =====================================================

        if (errorMessage != null) {

            ErrorMessage(
                errorMessage!!
            )

            Spacer(
                Modifier.height(12.dp)
            )
        }

        // =====================================================
        // ROLE SELECTION
        // =====================================================

        Text(

            text = "Select role",

            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),

            fontWeight =
                FontWeight.SemiBold
        )

        Column(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            OutlinedButton(

                onClick = {
                    roleMenuExpanded = true
                },

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(14.dp)
            ) {

                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text =
                            roleDisplayName(
                                selectedRole
                            )
                    )

                    Text("▼")
                }
            }

            DropdownMenu(

                expanded =
                    roleMenuExpanded,

                onDismissRequest = {
                    roleMenuExpanded = false
                },

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                UserRole.values()
                    .forEach { role ->

                        DropdownMenuItem(

                            text = {

                                Text(
                                    roleDisplayName(
                                        role
                                    )
                                )
                            },

                            onClick = {

                                selectedRole =
                                    role

                                roleMenuExpanded =
                                    false

                                errorMessage =
                                    null
                            }
                        )
                    }
            }
        }

        Spacer(
            Modifier.height(18.dp)
        )

        // =====================================================
        // LOGIN
        // =====================================================

        if (!isRegistering) {

            OutlinedTextField(

                value = email,

                onValueChange = {
                    email = it
                },

                modifier =
                    Modifier.fillMaxWidth(),

                singleLine = true,

                label = {
                    Text("Email address")
                },

                placeholder = {
                    Text("name@example.com")
                }
            )

            Spacer(
                Modifier.height(12.dp)
            )

            OutlinedTextField(

                value = password,

                onValueChange = {
                    password = it
                },

                modifier =
                    Modifier.fillMaxWidth(),

                singleLine = true,

                label = {
                    Text("Password")
                },

                visualTransformation =
                    if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),

                trailingIcon = {

                    TextButton(

                        onClick = {
                            passwordVisible =
                                !passwordVisible
                        }
                    ) {

                        Text(

                            if (passwordVisible)
                                "Hide"
                            else
                                "Show"
                        )
                    }
                }
            )

        } else {

            // =================================================
            // REGISTRATION
            // =================================================

            // =================================================
            // COMMON NAME
            // =================================================

            if (selectedRole != UserRole.SUPPLIER) {

                OutlinedTextField(

                    value = name,

                    onValueChange = {
                        name = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    singleLine = true,

                    label = {
                        Text("Full name")
                    },

                    placeholder = {
                        Text(
                            "Example: Suresh Patil"
                        )
                    }
                )

                Spacer(
                    Modifier.height(12.dp)
                )
            }

            // =================================================
            // EMAIL
            // =================================================

            OutlinedTextField(

                value = email,

                onValueChange = {
                    email = it
                },

                modifier =
                    Modifier.fillMaxWidth(),

                singleLine = true,

                label = {
                    Text("Email address")
                },

                placeholder = {
                    Text("name@example.com")
                }
            )

            Spacer(
                Modifier.height(12.dp)
            )

            // =================================================
            // PASSWORD
            // =================================================

            OutlinedTextField(

                value = password,

                onValueChange = {
                    password = it
                },

                modifier =
                    Modifier.fillMaxWidth(),

                singleLine = true,

                label = {
                    Text("Password")
                },

                supportingText = {
                    Text(
                        "Use at least 6 characters"
                    )
                },

                visualTransformation =
                    if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),

                trailingIcon = {

                    TextButton(

                        onClick = {
                            passwordVisible =
                                !passwordVisible
                        }
                    ) {

                        Text(

                            if (passwordVisible)
                                "Hide"
                            else
                                "Show"
                        )
                    }
                }
            )

            Spacer(
                Modifier.height(12.dp)
            )

            // =================================================
            // PHONE
            // =================================================

            OutlinedTextField(

                value = phone,

                onValueChange = {
                    phone = it
                },

                modifier =
                    Modifier.fillMaxWidth(),

                singleLine = true,

                label = {
                    Text("Contact number")
                },

                placeholder = {
                    Text(
                        "Example: 9876543210"
                    )
                }
            )

            Spacer(
                Modifier.height(18.dp)
            )

            // =================================================
            // FARMER REGISTRATION
            // =================================================

            if (selectedRole == UserRole.FARMER) {

                RegistrationSectionTitle(
                    "Farm details"
                )

                // Village

                OutlinedTextField(

                    value = village,

                    onValueChange = {
                        village = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    singleLine = true,

                    label = {
                        Text("Village")
                    }
                )

                Spacer(
                    Modifier.height(12.dp)
                )

                // District

                OutlinedTextField(

                    value = district,

                    onValueChange = {
                        district = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    singleLine = true,

                    label = {
                        Text("District")
                    }
                )

                Spacer(
                    Modifier.height(12.dp)
                )

                // Farm location

                OutlinedTextField(

                    value = farmLocation,

                    onValueChange = {
                        farmLocation = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    singleLine = true,

                    label = {
                        Text("Farm location")
                    },

                    placeholder = {
                        Text(
                            "Example: Baramati"
                        )
                    },

                    supportingText = {
                        Text(
                            "Enter a recognizable village, town, area, or landmark."
                        )
                    }
                )

                Spacer(
                    Modifier.height(12.dp)
                )

                // Number of farms

                OutlinedTextField(

                    value = numberOfFarms,

                    onValueChange = {
                        numberOfFarms = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    singleLine = true,

                    label = {
                        Text("Number of farms")
                    }
                )

                Spacer(
                    Modifier.height(12.dp)
                )

                // Total area

                OutlinedTextField(

                    value = totalAreaAcres,

                    onValueChange = {
                        totalAreaAcres = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    singleLine = true,

                    label = {
                        Text("Total area (acre)")
                    }
                )
            }

            // =================================================
            // ADVISOR REGISTRATION
            // =================================================

            if (selectedRole == UserRole.ADVISOR) {

                RegistrationSectionTitle(
                    "Professional details"
                )

                // University / Company

                OutlinedTextField(

                    value =
                        organizationName,

                    onValueChange = {
                        organizationName = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    singleLine = true,

                    label = {
                        Text(
                            "University / Company name"
                        )
                    }
                )

                Spacer(
                    Modifier.height(12.dp)
                )

                // Expertise

                OutlinedTextField(

                    value = expertise,

                    onValueChange = {
                        expertise = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    singleLine = true,

                    label = {
                        Text(
                            "Expertise / Field"
                        )
                    },

                    placeholder = {
                        Text(
                            "Example: Crop disease management"
                        )
                    }
                )

                Spacer(
                    Modifier.height(12.dp)
                )

                // Experience

                OutlinedTextField(

                    value = experience,

                    onValueChange = {
                        experience = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    singleLine = true,

                    label = {
                        Text("Experience")
                    },

                    placeholder = {
                        Text(
                            "Example: 5 years"
                        )
                    }
                )

                Spacer(
                    Modifier.height(12.dp)
                )

                // Location

                OutlinedTextField(

                    value = location,

                    onValueChange = {
                        location = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    singleLine = true,

                    label = {
                        Text("Location")
                    }
                )
            }

            // =================================================
            // SUPPLIER REGISTRATION
            // =================================================

            if (selectedRole == UserRole.SUPPLIER) {

                RegistrationSectionTitle(
                    "Company details"
                )

                // Company

                OutlinedTextField(

                    value = companyName,

                    onValueChange = {
                        companyName = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    singleLine = true,

                    label = {
                        Text("Company name")
                    }
                )

                Spacer(
                    Modifier.height(12.dp)
                )

                // Branch locations

                OutlinedTextField(

                    value =
                        branchLocations,

                    onValueChange = {
                        branchLocations = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    singleLine = true,

                    label = {
                        Text("Branch locations")
                    }
                )

                Spacer(
                    Modifier.height(12.dp)
                )

                // Business type

                OutlinedTextField(

                    value =
                        businessType,

                    onValueChange = {
                        businessType = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    singleLine = true,

                    label = {
                        Text("Business type")
                    },

                    placeholder = {
                        Text(
                            "Example: Agricultural supplies"
                        )
                    }
                )

                Spacer(
                    Modifier.height(12.dp)
                )

                // Contact person

                OutlinedTextField(

                    value =
                        contactPerson,

                    onValueChange = {
                        contactPerson = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    singleLine = true,

                    label = {
                        Text("Contact person")
                    }
                )

                Spacer(
                    Modifier.height(12.dp)
                )

                // Location

                OutlinedTextField(

                    value = location,

                    onValueChange = {
                        location = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    singleLine = true,

                    label = {
                        Text("Location")
                    }
                )
            }

            Spacer(
                Modifier.height(20.dp)
            )
        }

        // =====================================================
        // SUBMIT BUTTON
        // =====================================================

        Button(

            onClick = ::submit,

            enabled = !isLoading,

            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(16.dp),

            colors =
                ButtonDefaults.buttonColors(
                    containerColor = LeafGreen
                )
        ) {

            Text(

                text = when {

                    isLoading ->
                        "Please wait..."

                    isRegistering ->
                        "Create account"

                    else ->
                        "Sign in"
                },

                modifier =
                    Modifier.padding(
                        vertical = 5.dp
                    ),

                fontWeight =
                    FontWeight.Bold
            )
        }

        // =====================================================
        // LOGIN / REGISTER TOGGLE
        // =====================================================

        TextButton(

            onClick = {

                modeName =
                    if (isRegistering)
                        AuthenticationMode.LOGIN.name
                    else
                        AuthenticationMode.REGISTER.name

                errorMessage = null
            }
        ) {

            Text(

                if (isRegistering)
                    "Already have an account? Sign in"
                else
                    "New user? Create an account"
            )
        }

        Spacer(
            Modifier.height(12.dp)
        )

        // =====================================================
        // GUEST
        // =====================================================

        OutlinedButton(

            onClick =
                onContinueAsGuest,

            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(16.dp)
        ) {

            Text(
                "Explore the prototype as guest"
            )
        }

        // =====================================================
        // FIREBASE INFORMATION
        // =====================================================

        Text(

            text =
                "Your account information is securely handled by Firebase Authentication.",

            style =
                MaterialTheme.typography.labelSmall,

            color =
                MutedText,

            textAlign =
                TextAlign.Center,

            modifier =
                Modifier.padding(
                    top = 16.dp
                )
        )

        Spacer(
            Modifier.height(20.dp)
        )
    }
}

// =============================================================
// REGISTRATION SECTION TITLE
// =============================================================

@Composable
private fun RegistrationSectionTitle(
    title: String
) {

    Text(

        text = title,

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    bottom = 12.dp
                ),

        style =
            MaterialTheme.typography.titleMedium,

        fontWeight =
            FontWeight.Bold,

        color =
            LeafGreen
    )
}

// =============================================================
// ERROR MESSAGE
// =============================================================

@Composable
private fun ErrorMessage(
    message: String
) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(14.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFFFFEDEA)
            )
    ) {

        Text(

            text = message,

            modifier =
                Modifier.padding(14.dp),

            color =
                MaterialTheme.colorScheme.error,

            style =
                MaterialTheme.typography.bodySmall
        )
    }
}

// =============================================================
// ROLE DISPLAY NAME
// =============================================================

private fun roleDisplayName(
    role: UserRole
): String {

    return when (role) {

        UserRole.FARMER ->
            "Farmer"

        UserRole.ADVISOR ->
            "Advisor"

        UserRole.SUPPLIER ->
            "Supplier"
    }
}

// =============================================================
// REGISTRATION VALIDATION
// =============================================================

private fun validateRegistration(

    selectedRole: UserRole,

    name: String,

    email: String,

    password: String,

    phone: String,

    village: String,

    district: String,

    farmLocation: String,

    numberOfFarms: String,

    totalAreaAcres: String,

    organizationName: String,

    expertise: String,

    experience: String,

    companyName: String,

    branchLocations: String,

    businessType: String,

    contactPerson: String

): String? {

    // =========================================================
    // COMMON VALIDATION
    // =========================================================

    if (
        selectedRole != UserRole.SUPPLIER &&
        name.trim().isEmpty()
    ) {

        return "Please enter your full name."
    }

    if (
        selectedRole == UserRole.SUPPLIER &&
        companyName.trim().isEmpty()
    ) {

        return "Please enter the company name."
    }

    if (
        !email.contains("@") ||
        !email.contains(".")
    ) {

        return "Enter a valid email address."
    }

    if (password.length < 6) {

        return "Password must contain at least 6 characters."
    }

    if (phone.trim().isEmpty()) {

        return "Please enter your contact number."
    }

    // =========================================================
    // ROLE-SPECIFIC VALIDATION
    // =========================================================

    when (selectedRole) {

        // =====================================================
        // FARMER
        // =====================================================

        UserRole.FARMER -> {

            if (village.trim().isEmpty()) {

                return "Please enter your village."
            }

            if (district.trim().isEmpty()) {

                return "Please enter your district."
            }

            if (farmLocation.trim().isEmpty()) {

                return "Please enter your farm location."
            }

            val farms =
                numberOfFarms.toIntOrNull()

            if (
                farms == null ||
                farms <= 0
            ) {

                return "Enter a valid number of farms."
            }

            val area =
                totalAreaAcres.toDoubleOrNull()

            if (
                area == null ||
                area <= 0
            ) {

                return "Enter a valid total farm area."
            }
        }

        // =====================================================
        // ADVISOR
        // =====================================================

        UserRole.ADVISOR -> {

            if (
                organizationName.trim().isEmpty()
            ) {

                return "Please enter your university or company name."
            }

            if (
                expertise.trim().isEmpty()
            ) {

                return "Please enter your expertise or field."
            }

            if (
                experience.trim().isEmpty()
            ) {

                return "Please enter your experience."
            }
        }

        // =====================================================
        // SUPPLIER
        // =====================================================

        UserRole.SUPPLIER -> {

            if (
                branchLocations.trim().isEmpty()
            ) {

                return "Please enter the branch locations."
            }

            if (
                businessType.trim().isEmpty()
            ) {

                return "Please enter the business type."
            }

            if (
                contactPerson.trim().isEmpty()
            ) {

                return "Please enter the contact person."
            }
        }
    }

    return null
}

// =============================================================
// GEOCODE FARM LOCATION
// =============================================================

private fun getCoordinatesFromAddress(
    context: Context,
    addressText: String
): Pair<Double, Double>? {

    return try {

        val geocoder =
            Geocoder(
                context,
                Locale.getDefault()
            )

        @Suppress("DEPRECATION")
        val addresses: List<Address> =
            geocoder.getFromLocationName(
                addressText,
                1
            ) ?: emptyList()

        val address =
            addresses.firstOrNull()
                ?: return null

        Pair(
            address.latitude,
            address.longitude
        )

    } catch (
        exception: Exception
    ) {

        null
    }
}