package com.example.androidprojectmain

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidprojectmain.Adapters.MessageAdapter
import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class ChatbotFragment: Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var editText: EditText
    private lateinit var submitBtn: ImageButton
    private lateinit var generativeModel: GenerativeModel
    private lateinit var chat: com.google.ai.client.generativeai.Chat
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: MutableList<Messages>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chatbot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initializing Views
        recyclerView = view.findViewById(R.id.recyclerViewChat)
        editText = view.findViewById(R.id.et)
        submitBtn = view.findViewById(R.id.submitBtn)

        // Initializing Message Views and Adapter
        messageList = mutableListOf()
        messageAdapter = MessageAdapter(messageList)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = messageAdapter
        }

        generativeModel = GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = "AIzaSyCN8PzRCMy0fU8IpNXTbYpxBRnnMSVhEIs"
        )

        chat = generativeModel.startChat()

        submitBtn.setOnClickListener {
            val prompt = editText.text.toString().trim()
            if(prompt.isNotEmpty()){
                sendMessage(prompt)
                editText.text.clear()
            }
        }
    }

    private fun ChatbotFragment.sendMessage(userMessage: String) {
        messageList.add(Messages(userMessage, true))
        messageAdapter.notifyItemInserted(messageList.size - 1)
        recyclerView.smoothScrollToPosition(messageList.size - 1)

        lifecycleScope.launch {
            try{
                val response = chat.sendMessage(userMessage)
                val botResponse = response.text ?: "No Response"

                messageList.add(Messages(botResponse, false))
                messageAdapter.notifyItemInserted(messageList.size - 1)
                recyclerView.smoothScrollToPosition(messageList.size - 1)
            } catch (e: Exception){
                Log.d("ChatbotFragment", "Error: ${e.message}")
                messageList.add(Messages("Error: ${e.message}", false))
                messageAdapter.notifyItemInserted(messageList.size - 1)
                recyclerView.smoothScrollToPosition(messageList.size - 1)
            }
        }

    }
}
