package com.sashya.krushisetu

import android.content.Context

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

import com.sashya.krushisetu.data.auth.FirebaseAuthRepository
import com.sashya.krushisetu.data.model.UserProfile
import com.sashya.krushisetu.data.model.UserRole

import com.sashya.krushisetu.feature.advisory.AdvisoryScreen
import com.sashya.krushisetu.feature.advisory.AdvisorDashboardScreen
import com.sashya.krushisetu.feature.auth.AuthenticationScreen
import com.sashya.krushisetu.feature.consultation.ConsultationScreen
import com.sashya.krushisetu.feature.crops.CropsScreen
import com.sashya.krushisetu.feature.home.HomeScreen
import com.sashya.krushisetu.feature.onboarding.WelcomeScreen
import com.sashya.krushisetu.feature.plantscan.PlantScanScreen
import com.sashya.krushisetu.feature.profile.ProfileScreen

import com.sashya.krushisetu.feature.supplier.SupplierAnalyticsScreen
import com.sashya.krushisetu.feature.supplier.SupplierBottomBar
import com.sashya.krushisetu.feature.supplier.SupplierDashboardScreen
import com.sashya.krushisetu.feature.supplier.SupplierDeliveryScreen
import com.sashya.krushisetu.feature.supplier.SupplierOrdersScreen
import com.sashya.krushisetu.feature.supplier.SupplierPaymentsScreen
import com.sashya.krushisetu.feature.supplier.SupplierProductsScreen
import com.sashya.krushisetu.feature.supplier.SupplierProfileScreen

import com.sashya.krushisetu.ui.components.KrushiBottomBar
import com.sashya.krushisetu.ui.navigation.AppDestination
import com.sashya.krushisetu.feature.advisory.AdvisorBottomBar
import com.sashya.krushisetu.feature.advisory.AdvisorFarmersScreen
import com.sashya.krushisetu.feature.advisory.AdvisorConsultationsScreen
import com.sashya.krushisetu.feature.advisory.AdvisorScheduleScreen
import com.sashya.krushisetu.feature.advisory.AdvisorProfileScreen
import com.sashya.krushisetu.feature.advisory.AdvisorFarmerDetailsScreen
private enum class AppEntry {
    WELCOME,
    AUTHENTICATION,
    APP
}

@Composable
fun KrushiSetuApp() {

    val context = LocalContext.current

    val authRepository =
        remember {
            FirebaseAuthRepository(
                context.applicationContext
            )
        }

    // =========================================================
    // ROLE PREFERENCES
    // =========================================================

    val rolePreferences =
        remember {

            context.getSharedPreferences(
                "krushisetu_preferences",
                Context.MODE_PRIVATE
            )
        }

    // =========================================================
    // APP ENTRY
    // =========================================================

    var appEntryName by remember {

        mutableStateOf(

            if (authRepository.isUserSignedIn()) {

                AppEntry.APP.name

            } else {

                AppEntry.WELCOME.name
            }
        )
    }

    // =========================================================
    // FARMER DESTINATION
    // =========================================================

    var destinationName by remember {

        mutableStateOf(
            AppDestination.HOME.name
        )
    }

    // =========================================================
    // USER ROLE
    // =========================================================

    var userRoleName by remember {

        mutableStateOf(

            rolePreferences.getString(
                "user_role",
                UserRole.FARMER.name
            ) ?: UserRole.FARMER.name
        )
    }

    // =========================================================
    // CURRENT USER PROFILE
    // =========================================================

    var currentUserProfile by remember {

        mutableStateOf<UserProfile?>(null)
    }

    // =========================================================
    // LOAD CURRENT USER PROFILE
    // =========================================================

    LaunchedEffect(
        appEntryName,
        userRoleName
    ) {

        if (
            appEntryName == AppEntry.APP.name &&
            authRepository.isUserSignedIn()
        ) {

            authRepository.getCurrentUserProfile { result ->

                result.onSuccess { profile ->

                    currentUserProfile =
                        profile
                }
            }
        }
    }

    // =========================================================
    // SUPPLIER SCREEN
    // =========================================================

    var supplierScreen by remember {

        mutableStateOf(
            "DASHBOARD"
        )
    }

    // =========================================================
    // CONVERT STRING STATES
    // =========================================================

    val appEntry =
        AppEntry.valueOf(appEntryName)

    val destination =
        AppDestination.valueOf(destinationName)

    // =========================================================
    // MAIN APPLICATION FLOW
    // =========================================================

    when (appEntry) {

        // =====================================================
        // WELCOME
        // =====================================================

        AppEntry.WELCOME -> WelcomeScreen(

            onStart = {

                appEntryName =
                    AppEntry.AUTHENTICATION.name
            },

            onExploreAsGuest = {

                userRoleName =
                    UserRole.FARMER.name

                destinationName =
                    AppDestination.HOME.name

                appEntryName =
                    AppEntry.APP.name
            }
        )

        // =====================================================
        // AUTHENTICATION
        // =====================================================

        AppEntry.AUTHENTICATION -> AuthenticationScreen(

            authRepository = authRepository,

            onAuthenticated = { role ->

                userRoleName =
                    when (role.uppercase()) {

                        UserRole.FARMER.name ->
                            UserRole.FARMER.name

                        UserRole.ADVISOR.name ->
                            UserRole.ADVISOR.name

                        UserRole.SUPPLIER.name ->
                            UserRole.SUPPLIER.name

                        else ->
                            UserRole.FARMER.name
                    }

                rolePreferences
                    .edit()
                    .putString(
                        "user_role",
                        userRoleName
                    )
                    .apply()

                destinationName =
                    AppDestination.HOME.name

                supplierScreen =
                    "DASHBOARD"

                // -------------------------------------------------
                // Important:
                // Load the newly authenticated user's profile
                // immediately so HomeScreen gets farm coordinates.
                // -------------------------------------------------

                authRepository.getCurrentUserProfile { result ->

                    result.onSuccess { profile ->

                        currentUserProfile =
                            profile
                    }
                }

                appEntryName =
                    AppEntry.APP.name
            },

            onContinueAsGuest = {

                userRoleName =
                    UserRole.FARMER.name

                destinationName =
                    AppDestination.HOME.name

                supplierScreen =
                    "DASHBOARD"

                currentUserProfile =
                    null

                appEntryName =
                    AppEntry.APP.name
            }
        )

        // =====================================================
        // MAIN APPLICATION
        // =====================================================

        AppEntry.APP -> {

            val userRole =
                UserRole.valueOf(userRoleName)

            when (userRole) {

                // =================================================
                // FARMER
                // =================================================

                UserRole.FARMER -> Scaffold(

                    modifier =
                        Modifier.fillMaxSize(),

                    bottomBar = {

                        KrushiBottomBar(

                            currentDestination =
                                destination,

                            onDestinationSelected = {

                                destinationName =
                                    it.name
                            }
                        )
                    }

                ) { innerPadding ->

                    when (destination) {

                        // =========================================
                        // FARMER HOME
                        // =========================================

                        AppDestination.HOME -> HomeScreen(

                            modifier =
                                Modifier.padding(
                                    innerPadding
                                ),

                            farmerName =
                                authRepository.currentUserName()
                                    ?: currentUserProfile?.name
                                    ?: "Farmer",

                            // =====================================
                            // THIS IS THE IMPORTANT NEW PARAMETER
                            // =====================================

                            userProfile =
                                currentUserProfile,

                            onOpenCrops = {

                                destinationName =
                                    AppDestination.CROPS.name
                            },

                            onOpenAdvisory = {

                                destinationName =
                                    AppDestination.ADVISORY.name
                            },

                            onOpenPlantScan = {

                                destinationName =
                                    AppDestination.PLANT_SCAN.name
                            },

                            onOpenConsultation = {

                                destinationName =
                                    AppDestination.CONSULTATION.name
                            }
                        )

                        // =========================================
                        // FARMER CROPS
                        // =========================================

                        AppDestination.CROPS ->
                            CropsScreen(

                                modifier =
                                    Modifier.padding(
                                        innerPadding
                                    )
                            )

                        // =========================================
                        // FARMER ADVISORY
                        // =========================================

                        AppDestination.ADVISORY ->
                            AdvisoryScreen(

                                modifier =
                                    Modifier.padding(
                                        innerPadding
                                    ),

                                onOpenPlantScan = {

                                    destinationName =
                                        AppDestination.PLANT_SCAN.name
                                }
                            )

                        // =========================================
                        // FARMER PLANT SCAN
                        // =========================================

                        AppDestination.PLANT_SCAN ->
                            PlantScanScreen(

                                modifier =
                                    Modifier.padding(
                                        innerPadding
                                    ),

                                onOpenConsultation = {

                                    destinationName =
                                        AppDestination.CONSULTATION.name
                                }
                            )

                        // =========================================
                        // FARMER CONSULTATION
                        // =========================================

                        AppDestination.CONSULTATION ->
                            ConsultationScreen(

                                modifier =
                                    Modifier.padding(
                                        innerPadding
                                    )
                            )

                        // =========================================
                        // FARMER PROFILE
                        // =========================================

                        AppDestination.PROFILE ->
                            ProfileScreen(

                                modifier =
                                    Modifier.padding(
                                        innerPadding
                                    ),

                                userProfile =
                                    currentUserProfile,

                                signedInName =
                                    authRepository.currentUserName(),

                                signedInEmail =
                                    authRepository.currentUserEmail(),

                                onSignOut = {

                                    authRepository.signOut()

                                    rolePreferences
                                        .edit()
                                        .remove("user_role")
                                        .apply()

                                    userRoleName =
                                        UserRole.FARMER.name

                                    currentUserProfile =
                                        null

                                    destinationName =
                                        AppDestination.HOME.name

                                    appEntryName =
                                        AppEntry.AUTHENTICATION.name
                                },

                                onOpenLogin = {

                                    appEntryName =
                                        AppEntry.AUTHENTICATION.name
                                }
                            )
                    }
                }

                // =================================================
                // ADVISOR
                // =================================================

                UserRole.ADVISOR -> {

                    var advisorScreen by remember {
                        mutableStateOf("HOME")
                    }

                    var selectedFarmerName by remember {
                        mutableStateOf("")
                    }

                    var selectedFarmerLocation by remember {
                        mutableStateOf("")
                    }

                    var selectedFarmerCrop by remember {
                        mutableStateOf("")
                    }

                    Scaffold(

                        bottomBar = {

                            AdvisorBottomBar(
                                selectedTab = advisorScreen,
                                onHome = {
                                    advisorScreen = "HOME"
                                },
                                onFarmers = {
                                    advisorScreen = "FARMERS"
                                },
                                onConsultations = {
                                    advisorScreen = "CONSULTATIONS"
                                },
                                onSchedule = {
                                    advisorScreen = "SCHEDULE"
                                }
                            )
                        }

                    ) { innerPadding ->

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {

                            when (advisorScreen) {

                                "HOME" -> AdvisorDashboardScreen(
                                    advisorName =
                                        authRepository.currentUserName()
                                            ?: "Advisor",

                                    onOpenConsultations = {
                                        advisorScreen = "CONSULTATIONS"
                                    },

                                    onOpenProfile = {
                                        advisorScreen = "PROFILE"
                                    },

                                    onLogout = {
                                        authRepository.signOut()
                                        appEntryName =
                                            AppEntry.AUTHENTICATION.name
                                    }
                                )

                                "FARMERS" -> AdvisorFarmersScreen(
                                    onBack = {
                                        advisorScreen = "HOME"
                                    },
                                    onOpenFarmer = { name, location, crop ->

                                        selectedFarmerName = name
                                        selectedFarmerLocation = location
                                        selectedFarmerCrop = crop

                                        advisorScreen = "FARMER_DETAILS"
                                    },
                                    onOpenQueries = {
                                        advisorScreen = "CONSULTATIONS"
                                    }
                                )
                                "FARMER_DETAILS" -> AdvisorFarmerDetailsScreen(
                                    farmerName = selectedFarmerName,
                                    location = selectedFarmerLocation,
                                    crop = selectedFarmerCrop,
                                    onBack = {
                                        advisorScreen = "FARMERS"
                                    }
                                )

                                "CONSULTATIONS" -> AdvisorConsultationsScreen(
                                    onBack = {
                                        advisorScreen = "HOME"
                                    }
                                )

                                "SCHEDULE" -> AdvisorScheduleScreen(
                                    onBack = {
                                        advisorScreen = "HOME"
                                    }
                                )

                                "PROFILE" -> AdvisorProfileScreen(
                                    advisorName =
                                        authRepository.currentUserName()
                                            ?: "Advisor",

                                    advisorEmail =
                                        authRepository.currentUserEmail(),

                                    onBack = {
                                        advisorScreen = "HOME"
                                    },

                                    onLogout = {
                                        authRepository.signOut()
                                        appEntryName =
                                            AppEntry.AUTHENTICATION.name
                                    }
                                )
                            }
                        }
                    }
                }

                // =================================================
                // SUPPLIER
                // =================================================

                UserRole.SUPPLIER -> Scaffold(

                    bottomBar = {

                        SupplierBottomBar(

                            onHome = {

                                supplierScreen =
                                    "DASHBOARD"
                            },

                            onProducts = {

                                supplierScreen =
                                    "PRODUCTS"
                            },

                            onOrders = {

                                supplierScreen =
                                    "ORDERS"
                            },

                            onDelivery = {

                                supplierScreen =
                                    "DELIVERY"
                            },

                            onPayments = {

                                supplierScreen =
                                    "PAYMENTS"
                            }
                        )
                    }

                ) { innerPadding ->

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                innerPadding
                            )
                    ) {

                        when (supplierScreen) {

                            // =====================================
                            // SUPPLIER DASHBOARD
                            // =====================================

                            "DASHBOARD" ->
                                SupplierDashboardScreen(

                                    supplierName =
                                        authRepository.currentUserName()
                                            ?: "Supplier",

                                    onOpenProfile = {

                                        supplierScreen =
                                            "PROFILE"
                                    },

                                    onOpenProducts = {

                                        supplierScreen =
                                            "PRODUCTS"
                                    },

                                    onOpenOrders = {

                                        supplierScreen =
                                            "ORDERS"
                                    },

                                    onOpenDelivery = {

                                        supplierScreen =
                                            "DELIVERY"
                                    },

                                    onOpenPayments = {

                                        supplierScreen =
                                            "PAYMENTS"
                                    },

                                    onOpenAnalytics = {

                                        supplierScreen =
                                            "ANALYTICS"
                                    }
                                )

                            // =====================================
                            // SUPPLIER PRODUCTS
                            // =====================================

                            "PRODUCTS" ->
                                SupplierProductsScreen(

                                    onBack = {

                                        supplierScreen =
                                            "DASHBOARD"
                                    }
                                )

                            // =====================================
                            // SUPPLIER PROFILE
                            // =====================================

                            "PROFILE" ->
                                SupplierProfileScreen(

                                    supplierName =
                                        authRepository.currentUserName()
                                            ?: "Supplier",

                                    onBack = {

                                        supplierScreen =
                                            "DASHBOARD"
                                    },

                                    onSignOut = {

                                        authRepository.signOut()

                                        rolePreferences
                                            .edit()
                                            .remove("user_role")
                                            .apply()

                                        userRoleName =
                                            UserRole.FARMER.name

                                        supplierScreen =
                                            "DASHBOARD"

                                        destinationName =
                                            AppDestination.HOME.name

                                        currentUserProfile =
                                            null

                                        appEntryName =
                                            AppEntry.AUTHENTICATION.name
                                    }
                                )

                            // =====================================
                            // SUPPLIER ORDERS
                            // =====================================

                            "ORDERS" ->
                                SupplierOrdersScreen(

                                    onBack = {

                                        supplierScreen =
                                            "DASHBOARD"
                                    }
                                )

                            // =====================================
                            // SUPPLIER DELIVERY
                            // =====================================

                            "DELIVERY" ->
                                SupplierDeliveryScreen(

                                    onBack = {

                                        supplierScreen =
                                            "DASHBOARD"
                                    }
                                )

                            // =====================================
                            // SUPPLIER PAYMENTS
                            // =====================================

                            "PAYMENTS" ->
                                SupplierPaymentsScreen(

                                    onBack = {

                                        supplierScreen =
                                            "DASHBOARD"
                                    }
                                )

                            // =====================================
                            // SUPPLIER ANALYTICS
                            // =====================================

                            "ANALYTICS" ->
                                SupplierAnalyticsScreen(

                                    onBack = {

                                        supplierScreen =
                                            "DASHBOARD"
                                    }
                                )
                        }
                    }
                }
            }
        }
    }
}
