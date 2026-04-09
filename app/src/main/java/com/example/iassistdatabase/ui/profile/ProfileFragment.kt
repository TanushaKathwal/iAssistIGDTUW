package com.example.iassistdatabase.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.iassistdatabase.R
import com.example.iassistdatabase.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var storageRef: FirebaseStorage

    private var currentStorageImagePath: String? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            uploadImageToFirebase(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("users")
        storageRef = FirebaseStorage.getInstance()

        val userEmail = auth.currentUser?.email
        if (userEmail != null) {
            loadUserData(userEmail)
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }

        binding.editProfileImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.signOutButton.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }

        binding.removeProfileImageButton.setOnClickListener {
            removeProfileImage()
        }
    }

    private fun loadUserData(email: String) {
        databaseRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userSnap = snapshot.children.first()
                        val name = userSnap.child("name").getValue(String::class.java) ?: "Name"
                        val emailValue = userSnap.child("email").getValue(String::class.java) ?: "Email"
                        val profileImageUrl = userSnap.child("profileImageUrl").getValue(String::class.java)

                        binding.profileName.text = name
                        binding.profileEmail.text = emailValue

                        if (!profileImageUrl.isNullOrEmpty()) {
                            currentStorageImagePath = "profile_images/${Uri.parse(profileImageUrl).lastPathSegment}"
                            Glide.with(this@ProfileFragment)
                                .load(profileImageUrl)
                                .circleCrop()
                                .placeholder(R.drawable.ic_defaultprofile)
                                .into(binding.profileImageView)
                        } else {
                            binding.profileImageView.setImageResource(R.drawable.ic_defaultprofile)
                        }
                    } else {
                        Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val currentUser = auth.currentUser ?: return
        val email = currentUser.email ?: return

        val fileName = UUID.randomUUID().toString() + ".jpg"
        val storagePath = storageRef.reference.child("profile_images/$fileName")

        storagePath.putFile(imageUri)
            .addOnSuccessListener {
                storagePath.downloadUrl.addOnSuccessListener { uri ->
                    updateUserImageUrl(email, uri.toString())

                    Glide.with(this@ProfileFragment)
                        .load(uri)
                        .circleCrop()
                        .placeholder(R.drawable.ic_defaultprofile)
                        .into(binding.profileImageView)

                    currentStorageImagePath = "profile_images/$fileName"
                    Toast.makeText(requireContext(), "Profile image updated", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserImageUrl(email: String, imageUrl: String) {
        databaseRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userSnap = snapshot.children.first()
                        userSnap.ref.child("profileImageUrl").setValue(imageUrl)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun removeProfileImage() {
        val currentUser = auth.currentUser ?: return
        val email = currentUser.email ?: return

        databaseRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userSnap = snapshot.children.first()

                        // Remove profileImageUrl from database
                        userSnap.ref.child("profileImageUrl").removeValue()

                        // Remove image from Firebase Storage
                        currentStorageImagePath?.let { path ->
                            storageRef.reference.child(path).delete()
                        }

                        // Reset image to default
                        binding.profileImageView.setImageResource(R.drawable.ic_defaultprofile)
                        Toast.makeText(requireContext(), "Profile photo removed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to remove image", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
