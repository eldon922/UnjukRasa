package com.pedulinegeri.unjukrasa.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.FragmentSignUpPageBinding
import com.squareup.picasso.Picasso


class SignUpPageFragment : Fragment() {

    private var _binding: FragmentSignUpPageBinding? = null
    private val binding get() = _binding!!

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
                    binding.etName.error = getString(R.string.name_input_empty)
                    binding.etName.requestFocus()
                    binding.etEmail.error = null
                }
                else -> {
                    binding.btnSignUp.isEnabled = false

                    if (binding.etEmail.text.isNotBlank()) {
                        if (isValidEmail(binding.etEmail.text)) {
                            user.updateEmail(binding.etEmail.text.toString()).addOnFailureListener {
                                toast.setText(getString(R.string.email_input_exist))
                                toast.show()
                                binding.etEmail.setText(user.email)
                            }
                        } else {
                            toast.setText(getString(R.string.email_not_valid))
                            toast.show()
                            binding.btnSignUp.isEnabled = true
                            return@setOnClickListener
                        }
                    }

                    val db = Firebase.firestore

                    val userData = hashMapOf(
                        "name" to binding.etName.text.toString()
                    )

                    db.collection("users").document(user.uid)
                        .set(userData)
                        .addOnSuccessListener {
                            authViewModel.signedIn(Firebase.auth.currentUser!!.uid)
                            findNavController().popBackStack()
                        }
                        .addOnFailureListener {
                            toast.setText(getString(R.string.unknown_error_message, it))
                            toast.show()

                            binding.btnSignUp.isEnabled = true
                        }
                }
            }
        }

        binding.btnPhotoProfile.setOnClickListener {
            ImagePicker.with(this).compress(1024)
                .cropSquare()
                .maxResultSize(170, 170)
                .start()
        }
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    override fun onResume() {
        super.onResume()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().onBackPressed()
            return@addCallback
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
                toast.setText(getString(R.string.upload_photo_profile_failed, it))
                toast.show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}