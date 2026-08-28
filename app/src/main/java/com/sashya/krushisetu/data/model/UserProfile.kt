package com.sashya.krushisetu.data.model

enum class UserRole {
    FARMER,
    ADVISOR,
    SUPPLIER
}

data class UserProfile(

    // Common information
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val location: String = "",

    // Selected role
    val role: UserRole = UserRole.FARMER,

    // Farmer information
    val village: String = "",
    val district: String = "",
    val farmLocation: String = "",
    val numberOfFarms: Int = 0,
    val totalAreaAcres: Double = 0.0,

    // Advisor information
    val organizationName: String = "",
    val expertise: String = "",
    val experience: String = "",

    // Supplier information
    val companyName: String = "",
    val branchLocations: String = "",
    val businessType: String = "",
    val contactPerson: String = ""
)