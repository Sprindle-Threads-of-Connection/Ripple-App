package com.example.androidprojectmain

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {

    private lateinit var logoutBtn: Button
    private lateinit var userName: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = requireActivity().getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
        val name = sharedPref.getString("name", null)

        userName = view.findViewById(R.id.username)
        userName.text = name

        logoutBtn = view.findViewById(R.id.logout)
        logoutBtn.setOnClickListener {
            sharedPref.edit()
                .putString("user_id", null)
                .putString("name", null)
                .apply()

            Toast.makeText(requireContext(), "Logged Out", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), AuthActivity::class.java)
            startActivity(intent)
        }
    }

}
