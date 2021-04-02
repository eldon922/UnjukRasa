package com.pedulinegeri.unjukrasa.auth

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.pedulinegeri.unjukrasa.databinding.ActivitySignUpBinding


class SignUpPageActivity : AppCompatActivity() {

    private val PICK_IMAGE = 1
    private lateinit var binding: ActivitySignUpBinding

    private lateinit var profilePictureURI: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE) {
            profilePictureURI = data!!.data as Uri
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Daftar Pengguna Baru"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}