package com.pedulinegeri.unjukrasa.auth

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.jakewharton.processphoenix.ProcessPhoenix
import com.pedulinegeri.unjukrasa.databinding.FragmentSignUpPageBinding


class SignUpPageFragment : Fragment() {

    private var _binding: FragmentSignUpPageBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    private val MEDIA_CODE = 1

    private lateinit var profilePictureURI: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUp.setOnClickListener {
            // TODO DEV
            authViewModel.signedIn()
            ProcessPhoenix.triggerRebirth(requireContext())
//            val user = FirebaseAuth.getInstance().currentUser!!
//
//            user.updateEmail(binding.etEmail.text.toString())
//
//            val storage = Firebase.storage
//            val storageRef = storage.reference
//            val mountainsRef = storageRef.child("mountains.jpg")
//            val mountainImagesRef = storageRef.child("images/mountains.jpg")
//
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
//
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

        binding.btnImage.setOnClickListener {
            val options: Options = Options.init()
                .setRequestCode(MEDIA_CODE) //Request code for activity results
                .setCount(1) //Number of images to restict selection count
                .setFrontfacing(true) //Front Facing camera on start
                .setMode(Options.Mode.Picture) //Option to select only pictures or videos or both
                .setScreenOrientation(Options.SCREEN_ORIENTATION_SENSOR) //Orientaion

            Pix.start(this, options)
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
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == MEDIA_CODE) {
            val returnValue = data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)

            profilePictureURI = returnValue!![0]


            val bmImg = BitmapFactory.decodeFile(profilePictureURI)
            binding.ivPerson.setImageBitmap(bmImg)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}