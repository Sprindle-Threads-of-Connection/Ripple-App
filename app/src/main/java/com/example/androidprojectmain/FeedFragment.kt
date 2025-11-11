package com.example.androidprojectmain

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidprojectmain.API.RippleRepository
import com.example.androidprojectmain.Models.Ripple
import kotlinx.coroutines.launch

class FeedFragment : Fragment() {

    private val TAG = "FeedFragment"
    private lateinit var recyclerView: RecyclerView
    private lateinit var rippleAdapter: RippleFeedAdapter
    private var progressBar: ProgressBar? = null  // Made nullable
    private val repository = RippleRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView called")
        val view = inflater.inflate(R.layout.fragment_feed, container, false)

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerFeed)
        progressBar = view.findViewById(R.id.progressBar)  // Won't crash if not found

        setupRecyclerView()
        loadRipples()

        return view
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "Setting up RecyclerView")

        // Initialize adapter with click listeners
        rippleAdapter = RippleFeedAdapter(
            onLikeClick = { ripple ->
                handleLikeClick(ripple)
            },
            onCommentClick = { ripple ->
                handleCommentClick(ripple)
            },
            onRippleClick = { ripple ->
                handleRippleClick(ripple)
            }
        )

        // Set layout manager and adapter
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rippleAdapter
            setHasFixedSize(true)
        }

        Log.d(TAG, "RecyclerView setup complete")
    }

    private fun loadRipples() {
        Log.d(TAG, "========== Loading Ripples ==========")
        showLoading(true)

        lifecycleScope.launch {
            val result = repository.getAllRipples()

            result.onSuccess { ripples ->
                Log.i(TAG, "✅ SUCCESS: Loaded ${ripples.size} ripples")
                showLoading(false)

                if (ripples.isEmpty()) {
                    Log.d(TAG, "No ripples found")
                    Toast.makeText(requireContext(), "No ripples yet!", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d(TAG, "Updating RecyclerView with ${ripples.size} items")
                    // Use submitList instead of updateData
                    rippleAdapter.submitList(ripples)

                    // Log each ripple
                    ripples.forEachIndexed { index, ripple ->
                        Log.d(TAG, "Ripple #${index + 1}: ID=${ripple.post_id}, Likes=${ripple.likes_count}, Comments=${ripple.comments_count}")
                    }
                }
            }.onFailure { error ->
                Log.e(TAG, "❌ FAILURE: ${error.message}", error)
                showLoading(false)
                Toast.makeText(
                    requireContext(),
                    "Failed to load ripples: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun handleLikeClick(ripple: Ripple) {
        Log.d(TAG, "========== Like Button Clicked ==========")
        Log.d(TAG, "Ripple ID: ${ripple.post_id}")

        lifecycleScope.launch {
            // Replace with actual user ID
            val userId = 1

            val result = repository.likeRipple(userId, ripple.post_id)

            result.onSuccess {
                Log.i(TAG, "✅ Ripple liked successfully")
                Toast.makeText(requireContext(), "Liked!", Toast.LENGTH_SHORT).show()

                // Reload ripples to update counts
                loadRipples()
            }.onFailure { error ->
                Log.e(TAG, "❌ Failed to like: ${error.message}")
                Toast.makeText(requireContext(), "Failed to like", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleCommentClick(ripple: Ripple) {
        Log.d(TAG, "========== Comment Button Clicked ==========")
        Log.d(TAG, "Ripple ID: ${ripple.post_id}")

        // Navigate to comments screen or show comment dialog
        Toast.makeText(requireContext(), "Open comments for ripple ${ripple.post_id}", Toast.LENGTH_SHORT).show()
    }

    private fun handleRippleClick(ripple: Ripple) {
        Log.d(TAG, "========== Ripple Item Clicked ==========")
        Log.d(TAG, "Ripple ID: ${ripple.post_id}, Content: ${ripple.content}")

        // Handle ripple item click (e.g., show details)
        Toast.makeText(requireContext(), "Ripple: ${ripple.content}", Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    // Pull to refresh functionality
    fun refreshFeed() {
        Log.d(TAG, "Refreshing feed...")
        loadRipples()
    }
}
