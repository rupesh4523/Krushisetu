package com.sashya.krushisetu

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.sashya.krushisetu.data.auth.FirebaseAuthRepository
import com.sashya.krushisetu.feature.advisory.AdvisoryScreen
import com.sashya.krushisetu.feature.advisory.AdvisorDashboardScreen
import com.sashya.krushisetu.feature.supplier.SupplierDashboardScreen
import com.sashya.krushisetu.feature.auth.AuthenticationScreen
import com.sashya.krushisetu.feature.consultation.ConsultationScreen
import com.sashya.krushisetu.feature.crops.CropsScreen
import com.sashya.krushisetu.feature.home.HomeScreen
import com.sashya.krushisetu.feature.onboarding.WelcomeScreen
import com.sashya.krushisetu.feature.profile.ProfileScreen
import com.sashya.krushisetu.feature.plantscan.PlantScanScreen
import com.sashya.krushisetu.ui.components.KrushiBottomBar
import com.sashya.krushisetu.ui.navigation.AppDestination
import com.sashya.krushisetu.feature.supplier.SupplierDashboardScreen
import com.sashya.krushisetu.feature.supplier.SupplierProfileScreen

private enum class AppEntry {
    WELCOME,
    AUTHENTICATION,
    APP
}
private enum class UserRole {
    FARMER,
    ADVISOR,
    SUPPLIER
}

@Composable
fun KrushiSetuApp() {
    val context = LocalContext.current
    val authRepository = remember { FirebaseAuthRepository(context.applicationContext) }
    var appEntryName by remember {
        mutableStateOf(
            if (authRepository.isUserSignedIn()) AppEntry.APP.name else AppEntry.WELCOME.name
        )
    }
    var destinationName by remember { mutableStateOf(AppDestination.HOME.name) }
    var userRoleName by remember {
        mutableStateOf(UserRole.FARMER.name)
    }
    var supplierScreen by remember {
        mutableStateOf("DASHBOARD")
    }
    val appEntry = AppEntry.valueOf(appEntryName)
    val destination = AppDestination.valueOf(destinationName)

    when (appEntry) {
        AppEntry.WELCOME -> WelcomeScreen(
            onStart = { appEntryName = AppEntry.AUTHENTICATION.name },
            onExploreAsGuest = { appEntryName = AppEntry.APP.name }
        )
        AppEntry.AUTHENTICATION -> AuthenticationScreen(
            authRepository = authRepository,
            onAuthenticated = { role ->

                userRoleName = when (role) {
                    "Farmer" -> UserRole.FARMER.name
                    "Advisor" -> UserRole.ADVISOR.name
                    "Supplier" -> UserRole.SUPPLIER.name
                    else -> UserRole.FARMER.name
                }

                appEntryName = AppEntry.APP.name
            },
            onContinueAsGuest = { appEntryName = AppEntry.APP.name }
        )
        AppEntry.APP -> when (UserRole.valueOf(userRoleName)) {

            UserRole.FARMER -> Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    KrushiBottomBar(
                        currentDestination = destination,
                        onDestinationSelected = {
                            destinationName = it.name
                        }
                    )
                }
            ) { innerPadding ->

                when (destination) {

                    AppDestination.HOME -> HomeScreen(
                        modifier = Modifier.padding(innerPadding),
                        farmerName = authRepository.currentUserName() ?: "Suresh",
                        onOpenCrops = {
                            destinationName = AppDestination.CROPS.name
                        },
                        onOpenAdvisory = {
                            destinationName = AppDestination.ADVISORY.name
                        },
                        onOpenPlantScan = {
                            destinationName = AppDestination.PLANT_SCAN.name
                        },
                        onOpenConsultation = {
                            destinationName = AppDestination.CONSULTATION.name
                        }
                    )

                    AppDestination.CROPS -> CropsScreen(
                        modifier = Modifier.padding(innerPadding)
                    )

                    AppDestination.ADVISORY -> AdvisoryScreen(
                        modifier = Modifier.padding(innerPadding),
                        onOpenPlantScan = {
                            destinationName = AppDestination.PLANT_SCAN.name
                        }
                    )

                    AppDestination.PLANT_SCAN -> PlantScanScreen(
                        modifier = Modifier.padding(innerPadding),
                        onOpenConsultation = {
                            destinationName = AppDestination.CONSULTATION.name
                        }
                    )

                    AppDestination.CONSULTATION -> ConsultationScreen(
                        modifier = Modifier.padding(innerPadding)
                    )

                    AppDestination.PROFILE -> ProfileScreen(
                        modifier = Modifier.padding(innerPadding),
                        signedInName = authRepository.currentUserName(),
                        signedInEmail = authRepository.currentUserEmail(),
                        onSignOut = {
                            authRepository.signOut()
                            appEntryName = AppEntry.AUTHENTICATION.name
                        },
                        onOpenLogin = {
                            appEntryName = AppEntry.AUTHENTICATION.name
                        }
                    )
                }
            }

            UserRole.ADVISOR -> AdvisorDashboardScreen()

            UserRole.SUPPLIER -> when (supplierScreen) {

                "DASHBOARD" -> SupplierDashboardScreen(
                    supplierName = authRepository.currentUserName() ?: "Supplier",
                    onOpenProfile = {
                        supplierScreen = "PROFILE"
                    }
                )

                "PROFILE" ->  SupplierProfileScreen(
                    supplierName = authRepository.currentUserName() ?: "Supplier",
                    onBack = {
                        supplierScreen = "DASHBOARD"
                    }
                )
            }
        }
    }
}


