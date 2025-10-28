package com.example.androidprojectmain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FeedFragment : Fragment() {

    // We only need a variable for our adapter
    private lateinit var rippleFeedAdapter: RippleFeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Setting tup the RecyclerView
        setupRecyclerView(view)

        // 2. Creating our dummy data
        val dummyRipples = createDummyRipples()

        // 3. Submit the dummy data to the adapter to be displayed
        rippleFeedAdapter.submitList(dummyRipples)
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerFeed)
        rippleFeedAdapter = RippleFeedAdapter() // Create an instance of our adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = rippleFeedAdapter
    }

    /**
     * Creating a hardcoded list of RipplePost objects for UI testing.
     */
    private fun createDummyRipples(): List<RipplePost> {
        return listOf(
            RipplePost(
                post_id = 1,
                user_id = 101,
                content = "Just setting up my Ripple app! Excited to see how this looks. #AndroidDev",
                media_url = null, // No image for this post
                created_at = "2025-10-15T14:30:00Z",
                likes_count = 15,
                comments_count = 4
            ),
            RipplePost(
                post_id = 2,
                user_id = 102,
                content = "Here's a nice photo from my trip last week.",
                media_url = "https://images.unsplash.com/photo-1506748686214-e9df14d4d9d0", // A sample image URL
                created_at = "2025-10-15T12:05:00Z",
                likes_count = 128,
                comments_count = 22
            ),
            RipplePost(
                post_id = 3,
                user_id = 101,
                content = "Working with dummy data in a RecyclerView. It's a great way to test the UI without needing an internet connection.",
                media_url = null,
                created_at = "2025-10-15T09:15:00Z",
                likes_count = 42,
                comments_count = 8
            ),
            RipplePost(
                post_id = 4,
                user_id = 103,
                content = "Another post to make the list scrollable!",
                media_url = "https://images.unsplash.com/photo-1532274402911-5a369e4c4bb5",
                created_at = "2025-10-14T22:45:00Z",
                likes_count = 7,
                comments_count = 1
            )
        )
    }
}