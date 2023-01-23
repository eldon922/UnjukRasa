package com.pedulinegeri.unjukrasa.home.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject

class TrendingDemonstrationPagingSource @Inject constructor(
    db: FirebaseFirestore
) : DemonstrationPagingSource() {

     override val collectionRef =
        db.collection("demonstrations")
            .orderBy("numberOfAction", Query.Direction.DESCENDING)
            .limit(10)
}