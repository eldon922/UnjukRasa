package com.pedulinegeri.unjukrasa.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.pedulinegeri.unjukrasa.MainActivity
import com.pedulinegeri.unjukrasa.databinding.FragmentNotificationPageBinding
import com.pedulinegeri.unjukrasa.databinding.FragmentSignUpPageBinding


class SignUpPageFragment : Fragment() {

    private var fragmentBinding: FragmentSignUpPageBinding? = null

    private val PICK_IMAGE = 1
    private lateinit var profilePictureURI: Uri

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = FragmentSignUpPageBinding.inflate(inflater, container, false)
        return fragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = fragmentBinding!!

        binding.toolbar.setNavigationOnClickListener { view ->
            requireActivity().onBackPressed()
        }

        binding.btnSignUp.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser!!

            user.updateEmail(binding.etEmail.text.toString())

//            val storage = Firebase.storage
//            val storageRef = storage.reference
//            val mountainsRef = storageRef.child("mountains.jpg")
//            val mountainImagesRef = storageRef.child("images/mountains.jpg")

            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"

            val pickIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            startActivityForResult(chooserIntent, PICK_IMAGE)

//            var file = profilePictureURI
//            val riversRef = storageRef.child("images/${file.lastPathSegment}")
//            val uploadTask = riversRef.putFile(file)
//
//// Register observers to listen for when the download is done or if it fails
//            uploadTask.addOnFailureListener {
//                // Handle unsuccessful uploads
//            }.addOnSuccessListener { taskSnapshot ->
//                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
//                // ...
//
//            }

//            val profileUpdates = userProfileChangeRequest {
//                displayName = "Jane Q. User"
//                photoUri = profilePictureURI
//            }
//            user.updateProfile(profileUpdates)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        Log.d("Firebase", "User profile updated.")
//                    }
//                }
        }

        onBackPressedCallback = requireActivity().onBackPressedDispatcher.addCallback {
            return@addCallback
        }
    }

    override fun onPause() {
        super.onPause()

        onBackPressedCallback.remove()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE) {
            profilePictureURI = data!!.data as Uri
        }
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}