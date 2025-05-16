package com.example.maro.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.maro.R


class CallScriptFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate fragment layout
        return inflater.inflate(R.layout.fragment_call_script, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide chatbot button in MainActivity
        val mainActivity = activity as? MainActivity
        mainActivity?.chatbotButton?.visibility = View.GONE
        mainActivity?.shouldHideChatbotButton = true

        //click createButton
        val createButton = view.findViewById<TextView>(R.id.tv_create)
        createButton.setOnClickListener {
            val intent = Intent(requireContext(), CallScriptActivity::class.java)
            startActivity(intent)
        }
    }
}