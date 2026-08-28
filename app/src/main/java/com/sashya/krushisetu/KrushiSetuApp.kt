package com.sashya.krushisetu

import androidx.compose.foundation.layout.Box
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


private enum class AppEntry {
    WELCOME,
    AUTHENTICATION,
    APP
}


@Composable
fun KrushiSetuApp() {

    val context = LocalContext.current

    val authRepository = remember {
        FirebaseAuthRepository(context.applicationContext)
    }

    var appEntryName by remember {
        mutableStateOf(
            if (authRepository.isUserSignedIn()) {
                AppEntry.APP.name
            } else {
                AppEntry.WELCOME.name
            }
        )
    }

    var destinationName by remember {
        mutableStateOf(AppDestination.HOME.name)
    }

    var userRoleName by remember {
        mutableStateOf(UserRole.FARMER.name)
    }

    var supplierScreen by remember {
        mutableStateOf("DASHBOARD")
    }

    val appEntry = AppEntry.valueOf(appEntryName)

    val destination = AppDestination.valueOf(destinationName)


    when (appEntry) {

        // =========================================================
        // WELCOME
        // =========================================================

        AppEntry.WELCOME -> WelcomeScreen(

            onStart = {
                appEntryName = AppEntry.AUTHENTICATION.name
            },

            onExploreAsGuest = {
                appEntryName = AppEntry.APP.name
            }
        )


        // =========================================================
        // AUTHENTICATION
        // =========================================================

        AppEntry.AUTHENTICATION -> AuthenticationScreen(

            authRepository = authRepository,

            onAuthenticated = {
                appEntryName = AppEntry.APP.name
                userRoleName = it
            },

            onContinueAsGuest = {
                appEntryName = AppEntry.APP.name
            }
        )


        // =========================================================
        // MAIN APPLICATION
        // =========================================================

        AppEntry.APP -> {

            val userRole = UserRole.valueOf(userRoleName)

            when (userRole) {


                // =====================================================
                // FARMER
                // =====================================================

                UserRole.FARMER -> Scaffold(

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

                            farmerName =
                                authRepository.currentUserName()
                                    ?: "Farmer",

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


                        AppDestination.CROPS -> CropsScreen(
                            modifier = Modifier.padding(innerPadding)
                        )


                        AppDestination.ADVISORY -> AdvisoryScreen(

                            modifier = Modifier.padding(innerPadding),

                            onOpenPlantScan = {
                                destinationName =
                                    AppDestination.PLANT_SCAN.name
                            }
                        )


                        AppDestination.PLANT_SCAN -> PlantScanScreen(

                            modifier = Modifier.padding(innerPadding),

                            onOpenConsultation = {
                                destinationName =
                                    AppDestination.CONSULTATION.name
                            }
                        )


                        AppDestination.CONSULTATION -> ConsultationScreen(
                            modifier = Modifier.padding(innerPadding)
                        )


                        AppDestination.PROFILE -> ProfileScreen(

                            modifier = Modifier.padding(innerPadding),

                            signedInName =
                                authRepository.currentUserName(),

                            signedInEmail =
                                authRepository.currentUserEmail(),

                            onSignOut = {

                                authRepository.signOut()

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


                // =====================================================
                // ADVISOR
                // =====================================================

                UserRole.ADVISOR -> AdvisorDashboardScreen()


                // =====================================================
                // SUPPLIER
                // =====================================================

                UserRole.SUPPLIER -> Scaffold(

                    bottomBar = {

                        SupplierBottomBar(

                            onHome = {
                                supplierScreen = "DASHBOARD"
                            },

                            onProducts = {
                                supplierScreen = "PRODUCTS"
                            },

                            onOrders = {
                                supplierScreen = "ORDERS"
                            },

                            onDelivery = {
                                supplierScreen = "DELIVERY"
                            },

                            onPayments = {
                                supplierScreen = "PAYMENTS"
                            }
                        )
                    }

                ) { innerPadding ->

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {

                        when (supplierScreen) {


                            // =========================================
                            // SUPPLIER DASHBOARD
                            // =========================================

                            "DASHBOARD" -> SupplierDashboardScreen(

                                supplierName =
                                    authRepository.currentUserName()
                                        ?: "Supplier",

                                onOpenProfile = {
                                    supplierScreen = "PROFILE"
                                },

                                onOpenProducts = {
                                    supplierScreen = "PRODUCTS"
                                },

                                onOpenOrders = {
                                    supplierScreen = "ORDERS"
                                },

                                onOpenDelivery = {
                                    supplierScreen = "DELIVERY"
                                },

                                onOpenPayments = {
                                    supplierScreen = "PAYMENTS"
                                },

                                onOpenAnalytics = {
                                    supplierScreen = "ANALYTICS"
                                }
                            )


                            // =========================================
                            // PRODUCTS
                            // =========================================

                            "PRODUCTS" -> SupplierProductsScreen(

                                onBack = {
                                    supplierScreen = "DASHBOARD"
                                }
                            )


                            // =========================================
                            // PROFILE
                            // =========================================

                            "PROFILE" -> SupplierProfileScreen(

                                supplierName =
                                    authRepository.currentUserName()
                                        ?: "Supplier",

                                onBack = {
                                    supplierScreen = "DASHBOARD"
                                }
                            )


                            // =========================================
                            // ORDERS
                            // =========================================

                            "ORDERS" -> SupplierOrdersScreen(

                                onBack = {
                                    supplierScreen = "DASHBOARD"
                                }
                            )


                            // =========================================
                            // DELIVERY
                            // =========================================

                            "DELIVERY" -> SupplierDeliveryScreen(

                                onBack = {
                                    supplierScreen = "DASHBOARD"
                                }
                            )


                            // =========================================
                            // PAYMENTS
                            // =========================================

                            "PAYMENTS" -> SupplierPaymentsScreen(

                                onBack = {
                                    supplierScreen = "DASHBOARD"
                                }
                            )


                            // =========================================
                            // ANALYTICS
                            // =========================================

                            "ANALYTICS" -> SupplierAnalyticsScreen(

                                onBack = {
                                    supplierScreen = "DASHBOARD"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}