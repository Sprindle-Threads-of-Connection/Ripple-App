package com.example.androidprojectmain

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)
        val rootView = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Apply padding to avoid system bars overlap
            view.setPadding(
                insets.left,
                insets.top,
                insets.right,
                insets.bottom
            )

            windowInsets
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Load default fragment
        loadFragment(FeedFragment())

        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_feed -> loadFragment(FeedFragment())
                R.id.nav_search -> loadFragment(SearchFragment())
                R.id.nav_chatbot -> loadFragment(ChatbotFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
