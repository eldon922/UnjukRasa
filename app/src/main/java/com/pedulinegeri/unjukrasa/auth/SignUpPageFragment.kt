package com.pedulinegeri.unjukrasa.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.jakewharton.processphoenix.ProcessPhoenix
import com.pedulinegeri.unjukrasa.databinding.FragmentSignUpPageBinding
import com.squareup.picasso.Picasso


class SignUpPageFragment : Fragment() {

    private var _binding: FragmentSignUpPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    private lateinit var toast: Toast

    private val authViewModel: AuthViewModel by activityViewModels()

    private val user = Firebase.auth.currentUser!!

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

        toast = Toast.makeText(requireActivity().applicationContext, "", Toast.LENGTH_LONG)

        binding.btnSignUp.setOnClickListener {
            when {
                binding.etName.text.isBlank() -> {
                    binding.etName.error = "Nama wajib diisi!"
                    binding.etEmail.error = null
                }
                else -> {
                    binding.btnSignUp.isEnabled = false

                    if (binding.etEmail.text.isNotBlank()) {
                        user.updateEmail(binding.etEmail.text.toString())
                    }

                    val db = Firebase.firestore

                    val userData = hashMapOf(
                        "name" to binding.etName.text.toString(),
                        "demonstrations" to listOf<String>()
                    )

                    db.collection("users").document(user.uid)
                        .set(userData)
                        .addOnSuccessListener {
                            authViewModel.signedIn(Firebase.auth.currentUser!!.uid)
                            ProcessPhoenix.triggerRebirth(requireContext())
                        }
                        .addOnFailureListener {
                            toast.setText("Ada kesalahan, silahkan coba lagi. ($it)")
                            toast.show()

                            binding.btnSignUp.isEnabled = true
                        }
                }
            }
        }

        binding.btnImage.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()
                .maxResultSize(170, 170)
                .start()
        }
    }

    override fun onResume() {
        super.onResume()

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