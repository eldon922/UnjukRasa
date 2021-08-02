package com.pedulinegeri.unjukrasa.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pedulinegeri.unjukrasa.databinding.FragmentEditProfilePageBinding
import com.squareup.picasso.Picasso
import java.io.File


class EditProfilePageFragment : Fragment() {

    private var _binding: FragmentEditProfilePageBinding? = null
    private val binding get() = _binding!!

    private val MEDIA_CODE = 1

    private val user = Firebase.auth.currentUser!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfilePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnSubmit.setOnClickListener {
            when {
                binding.etName.text.isBlank() -> {
                    binding.etName.error = "Nama wajib diisi!"
                }
                else -> {
                    binding.btnSubmit.isEnabled = false

                    val db = Firebase.firestore
                    db.collection("users").document(user.uid)
                        .update("name", binding.etName.text.toString()).addOnSuccessListener {
                            binding.tvSuccess.isVisible = true
                        }.addOnFailureListener {
                            Toast.makeText(
                                requireContext(),
                                "Ada kesalahan, silahkan coba lagi. $it",
                                Toast.LENGTH_LONG
                            ).show()
                        }.addOnSuccessListener {
                            binding.btnSubmit.isEnabled = true
                        }
                }
            }
        }

        val imageRef =
            Firebase.storage.reference.child("profile_picture/${user.uid}.jpg")

        imageRef.downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(binding.ivPerson)
        }

        val db = Firebase.firestore
        val docRef = db.collection("users").document(Firebase.auth.currentUser!!.uid)
        docRef.addSnapshotListener { snapshot, e ->
            binding.etName.setText(snapshot?.data?.get("name").toString())
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == MEDIA_CODE) {
            val returnValue = data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
            val path = returnValue!![0]

            val imageRef =
                Firebase.storage.reference.child("profile_picture/${user.uid}.jpg")

            val file = Uri.fromFile(File(path))
            val uploadTask = imageRef.putFile(file)

            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it).into(binding.ivPerson)
                }
            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Unggah foto profil gagal. Silahkan coba lagi. $it",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}