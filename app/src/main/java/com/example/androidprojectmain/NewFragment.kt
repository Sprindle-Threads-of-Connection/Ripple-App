package com.example.androidprojectmain

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.androidprojectmain.API.RippleRepository
import com.google.android.play.integrity.internal.s
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class NewFragment: Fragment() {

    private val TAG = "RippleFragment"

    private lateinit var etRippleContent: EditText
    private lateinit var btnAddImage: LinearLayout
    private lateinit var layoutImagePreview: LinearLayout
    private lateinit var ivSelectedImage: ImageView
    private lateinit var btnRemoveImage: ImageView
    private lateinit var tvCharacterCount: TextView
    private lateinit var btnCreateRipple: Button
    private lateinit var progressBar: ProgressBar

    // Repository
    private val repository = RippleRepository()

    // Selected image
    private var selectedImageUri: Uri? = null
    private var selectedImageFile: File? = null

    // Image Picker Launcher
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val imageUri = data?.data

            if (imageUri != null) {
                Log.d(TAG, "📸 Image Selected from Gallery")
                Log.d(TAG, "URI: $imageUri")

                selectedImageUri = imageUri
                ivSelectedImage.setImageURI(imageUri)
                layoutImagePreview.visibility = View.VISIBLE

                // Convert URI to File
                selectedImageFile = uriToFile(imageUri)
                Log.d(TAG, "Image converted to file: ${selectedImageFile?.name}")
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new, container, false)

        initializeViews(view)
        setupListeners()

        return view
    }

    private fun initializeViews(view: View) {
        Log.d(TAG, "Initializing views...")

        etRippleContent = view.findViewById(R.id.etRippleContent)
        btnAddImage = view.findViewById(R.id.btnAddImage)
        layoutImagePreview = view.findViewById(R.id.layoutImagePreview)
        ivSelectedImage = view.findViewById(R.id.ivSelectedImage)
        btnRemoveImage = view.findViewById(R.id.btnRemoveImage)
        tvCharacterCount = view.findViewById(R.id.tvCharacterCount)
        btnCreateRipple = view.findViewById(R.id.btnCreateRipple)
        progressBar = view.findViewById(R.id.progressBar)
    }

    private fun setupListeners() {
        Log.d(TAG, "Setting up listeners...")

        // Character count listener
        etRippleContent.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                val length = p0?.length ?: 0
                tvCharacterCount.text = "$length/500"
                Log.d(TAG, "Character count: $length")
            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {}

        })

        // Add image button click
        btnAddImage.setOnClickListener {
            Log.d(TAG, "Add Image button clicked")
            openGallery()
        }

        // Remove image button click
        btnRemoveImage.setOnClickListener {
            Log.d(TAG, "Remove Image button clicked")
            removeSelectedImage()
        }

        // Create ripple button click
        btnCreateRipple.setOnClickListener {
            Log.d(TAG, "Create Ripple button clicked")
            createRipple()
        }
    }

    private fun openGallery() {
        Log.d(TAG, "Opening Gallery...")

        val intent = android.content.Intent(
            android.content.Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun removeSelectedImage() {

        selectedImageUri = null
        selectedImageFile = null
        ivSelectedImage.setImageURI(null)
        layoutImagePreview.visibility = View.GONE

        Toast.makeText(requireContext(), "Image removed", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "Image removed successfully")
    }

    private fun createRipple() {
        val content = etRippleContent.text.toString().trim()

        Log.d(TAG, "🚀 Creating Ripple")
        Log.d(TAG, "Content: $content")
        Log.d(TAG, "Has Image: ${selectedImageFile != null}")

        // Validation
        if (content.isEmpty()) {
            Log.w(TAG, "Content is empty")
            Toast.makeText(requireContext(), "Please write something!", Toast.LENGTH_SHORT).show()
            return
        }

        if (content.length < 3) {
            Log.w(TAG, "Content too short")
            Toast.makeText(requireContext(), "Content must be at least 3 characters", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading
        showLoading(true)

        // Replace with actual user ID (from SharedPreferences or ViewModel)
        val userId = 1

        lifecycleScope.launch {
            try {
                val result = if (selectedImageFile != null) {
                    Log.d(TAG, "Creating ripple WITH image")
                    repository.createRippleWithImage(userId, content, selectedImageFile)
                } else {
                    Log.d(TAG, "Creating ripple WITHOUT image (text only)")
                    repository.createTextOnlyRipple(userId, content)
                }

                result.onSuccess { response ->
                    Log.i(TAG, "SUCCESS: Ripple Created!")
                    Log.d(TAG, "Response: ${response.message}")

                    showLoading(false)

                    // Show success toast
                    Toast.makeText(
                        requireContext(),
                        "Ripple created successfully!",
                        Toast.LENGTH_LONG
                    ).show()

                    // Clear form
                    clearForm()

                    // Navigate back to feed (optional)
                    // findNavController().navigateUp()

                }.onFailure { error ->
                    Log.e(TAG, "FAILURE: ${error.message}", error)

                    showLoading(false)

                    // Show error toast
                    Toast.makeText(
                        requireContext(),
                        "Failed: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                Log.e(TAG, "Exception: ${e.message}", e)
                showLoading(false)

                Toast.makeText(
                    requireContext(),
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun clearForm() {
        Log.d(TAG, "🧹 Clearing form...")

        etRippleContent.text.clear()
        removeSelectedImage()
        tvCharacterCount.text = "0/500"

        Log.d(TAG, "Form cleared")
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnCreateRipple.isEnabled = !isLoading
        btnCreateRipple.alpha = if (isLoading) 0.5f else 1.0f

        Log.d(TAG, "Loading state: $isLoading")
    }

    /**
     * Convert URI to File
     */
    private fun uriToFile(uri: Uri): File? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val tempFile = File(requireContext().cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")

            inputStream?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }

            Log.d(TAG, "File created: ${tempFile.absolutePath}")
            Log.d(TAG, "File size: ${tempFile.length()} bytes")

            tempFile
        } catch (e: Exception) {
            Log.e(TAG, "Error converting URI to File: ${e.message}", e)
            null
        }
    }

}
