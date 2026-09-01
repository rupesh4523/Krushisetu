package com.sashya.krushisetu.data.crop

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.sashya.krushisetu.data.model.Crop

class CropRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // =========================================================
    // GET CURRENT USER ID
    // =========================================================

    private fun currentUserId(): String? {
        return auth.currentUser?.uid
    }

    // =========================================================
    // LISTEN TO CURRENT USER'S CROPS
    // =========================================================

    fun getCrops(
        onResult: (Result<List<Crop>>) -> Unit
    ): ListenerRegistration? {

        val userId = currentUserId()

        if (userId == null) {
            onResult(
                Result.failure(
                    Exception("No farmer is currently signed in.")
                )
            )

            return null
        }

        return firestore
            .collection("users")
            .document(userId)
            .collection("crops")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onResult(
                        Result.failure(error)
                    )
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    onResult(
                        Result.failure(
                            Exception("Unable to load crops.")
                        )
                    )
                    return@addSnapshotListener
                }

                val crops = snapshot.documents.mapNotNull { document ->

                    document.toObject(Crop::class.java)?.copy(
                        id = document.id
                    )
                }

                onResult(
                    Result.success(crops)
                )
            }
    }

    // =========================================================
    // ADD CROP
    // =========================================================

    fun addCrop(
        crop: Crop,
        onResult: (Result<String>) -> Unit
    ) {

        val userId = currentUserId()

        if (userId == null) {
            onResult(
                Result.failure(
                    Exception("No farmer is currently signed in.")
                )
            )
            return
        }

        val cropReference = firestore
            .collection("users")
            .document(userId)
            .collection("crops")
            .document()

        val cropToSave = crop.copy(
            id = cropReference.id
        )

        cropReference
            .set(cropToSave)
            .addOnSuccessListener {

                onResult(
                    Result.success(cropReference.id)
                )
            }
            .addOnFailureListener { exception ->

                onResult(
                    Result.failure(exception)
                )
            }
    }

    // =========================================================
    // UPDATE CROP
    // =========================================================

    fun updateCrop(
        crop: Crop,
        onResult: (Result<Unit>) -> Unit
    ) {

        val userId = currentUserId()

        if (userId == null) {
            onResult(
                Result.failure(
                    Exception("No farmer is currently signed in.")
                )
            )
            return
        }

        if (crop.id.isBlank()) {
            onResult(
                Result.failure(
                    Exception("Crop ID is missing.")
                )
            )
            return
        }

        firestore
            .collection("users")
            .document(userId)
            .collection("crops")
            .document(crop.id)
            .set(crop)
            .addOnSuccessListener {

                onResult(
                    Result.success(Unit)
                )
            }
            .addOnFailureListener { exception ->

                onResult(
                    Result.failure(exception)
                )
            }
    }

    // =========================================================
    // DELETE CROP
    // =========================================================

    fun deleteCrop(
        cropId: String,
        onResult: (Result<Unit>) -> Unit
    ) {

        val userId = currentUserId()

        if (userId == null) {
            onResult(
                Result.failure(
                    Exception("No farmer is currently signed in.")
                )
            )
            return
        }

        if (cropId.isBlank()) {
            onResult(
                Result.failure(
                    Exception("Crop ID is missing.")
                )
            )
            return
        }

        firestore
            .collection("users")
            .document(userId)
            .collection("crops")
            .document(cropId)
            .delete()
            .addOnSuccessListener {

                onResult(
                    Result.success(Unit)
                )
            }
            .addOnFailureListener { exception ->

                onResult(
                    Result.failure(exception)
                )
            }
    }
}