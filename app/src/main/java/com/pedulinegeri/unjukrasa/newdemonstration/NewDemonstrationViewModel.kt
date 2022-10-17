package com.pedulinegeri.unjukrasa.newdemonstration

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pedulinegeri.unjukrasa.util.Resource
import java.util.*

class NewDemonstrationViewModel : ViewModel() {

    private val createNewDemonstrationStatus =
        MutableLiveData<Resource<String>>(Resource.loading(null))

    fun createNewDemonstration(
        uid: String,
        title: String,
        to: String,
        description: String,
        youtubeVideoID: String,
        roadProtest: Boolean,
        roadProtestDate: Date,
        roadProtestLocation: String
    ): MutableLiveData<Resource<String>> {
        val demonstrationData = DemonstrationData(
            uid,
            title,
            to,
            description
        )

        if (youtubeVideoID.isNotBlank()) {
            demonstrationData.youtubeVideo = youtubeVideoID
        }

        if (roadProtest) {
            demonstrationData.roadProtest = roadProtest
            demonstrationData.datetime = roadProtestDate
            demonstrationData.location = roadProtestLocation
        }

        Firebase.firestore.collection("demonstrations").add(demonstrationData)
            .addOnSuccessListener {
                createNewDemonstrationStatus.postValue(Resource.success(it.id))
            }
            .addOnFailureListener {
                createNewDemonstrationStatus.postValue(
                    Resource.error(
                        it.message ?: "create_new_demonstration",
                        null
                    )
                )
            }

        return createNewDemonstrationStatus
    }

    private val uploadDemonstrationImagesStatus =
        MutableLiveData<Resource<Int>>(Resource.loading(null))

    fun uploadDemonstrationImages(
        imagesUri: List<Uri>,
        demonstrationId: String,
        uid: String
    ): MutableLiveData<Resource<Int>> {
        imagesUri.forEachIndexed { index, uri ->
            val imageRef =
                Firebase.storage.reference.child("demonstration_image/$demonstrationId/$uid/$index.png")
            val uploadTask = imageRef.putFile(uri)

            uploadTask.addOnSuccessListener {
                if (imagesUri.size - 1 == index) {
                    uploadDemonstrationImagesStatus.postValue(Resource.success(index + 1))
                }
            }.addOnFailureListener {
                uploadDemonstrationImagesStatus.postValue(
                    Resource.error(
                        it.message ?: "upload_demonstration_images",
                        index + 1
                    )
                )
            }
        }

        return uploadDemonstrationImagesStatus
    }

    private val uploadPolicePermitStatus =
        MutableLiveData(Resource.loading(null))

    fun uploadPolicePermit(demonstrationId: String, uid: String, uri: Uri): MutableLiveData<Resource<Nothing>> {
        val imageRef =
            Firebase.storage.reference.child("/police_permit_image/$demonstrationId/$uid.png")
        val uploadTask =
            imageRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            uploadPolicePermitStatus.postValue(Resource.success(null))
        }.addOnFailureListener {
            uploadPolicePermitStatus.postValue(Resource.error(it.message ?: "police_permit", null))
        }

        return uploadPolicePermitStatus
    }
}