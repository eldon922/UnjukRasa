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
import androidx.fragment.app.activityViewModels
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pedulinegeri.unjukrasa.auth.AuthViewModel
import com.pedulinegeri.unjukrasa.databinding.FragmentEditProfilePageBinding
import com.squareup.picasso.Picasso


class EditProfilePageFragment : Fragment() {

    private var _binding: FragmentEditProfilePageBinding? = null
    private val binding get() = _binding!!

    private lateinit var toast: Toast

    private val authViewModel: AuthViewModel by activityViewModels()

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

        toast = Toast.makeText(requireActivity().applicationContext, "", Toast.LENGTH_LONG)

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
                            toast.setText("Ada kesalahan, silahkan coba lagi. ($it)")
                            toast.show()
                        }.addOnSuccessListener {
                            binding.btnSubmit.isEnabled = true
                        }
                }
            }
        }

        val imageRef =
            Firebase.storage.reference.child("profile_picture/${user.uid}.png")

        imageRef.downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(binding.ivPerson)
        }

        binding.etName.setText(authViewModel.name)

        binding.btnImage.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()
                .maxResultSize(170, 170)
                .start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == ImagePicker.REQUEST_CODE) {
            val uri: Uri = data?.data!!

            val imageRef =
                Firebase.storage.reference.child("profile_picture/${user.uid}.png")
            val uploadTask = imageRef.putFile(uri)

            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it).into(binding.ivPerson)
                }
            }.addOnFailureListener {
                toast.setText("Unggah foto profil gagal. Silahkan coba lagi. ($it)")
                toast.show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}