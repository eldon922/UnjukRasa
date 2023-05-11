package com.pedulinegeri.unjukrasa.demonstration

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.pedulinegeri.unjukrasa.util.Resource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DemonstrationRepository @Inject constructor(private val firebaseFirestore: FirebaseFirestore) {

    suspend fun getDemonstration(id: String): Demonstration {
        val docRef = firebaseFirestore.collection("demonstrations").document(id)
        val result = docRef.get().await()
        if (result != null) {
            val demonstration = result.toObject<Demonstration>()
            if (demonstration != null) {
                demonstration.id = id
                return demonstration
            }
        }
        throw docRef.get().exception!!
    }
}