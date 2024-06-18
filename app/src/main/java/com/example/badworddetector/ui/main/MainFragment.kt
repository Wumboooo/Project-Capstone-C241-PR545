package com.example.badworddetector.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.badworddetector.R
import com.example.badworddetector.data.MainRepository
import com.example.badworddetector.data.api.ApiConfig
import com.example.badworddetector.databinding.FragmentMainBinding
import com.example.badworddetector.ui.SharedViewModel
import com.google.android.material.progressindicator.LinearProgressIndicator

class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var mainRepository: MainRepository
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var progressIndicator: ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        mainRepository = ApiConfig.provideMainRepository(requireContext())

        progressIndicator = view.findViewById(R.id.progressIndicator)

        val inputEditText = view.findViewById<EditText>(R.id.input)
        val resultTextView = view.findViewById<TextView>(R.id.tvResult)
        val checkButton = view.findViewById<Button>(R.id.btn_check)
        val resetButton = view.findViewById<Button>(R.id.btn_reset)

        checkButton.setOnClickListener {
            val text = inputEditText.text.toString().trim()
            if (text.isNotEmpty()) {
                resultTextView.text = ""
                showLoading(true)
                sharedViewModel.userEmail.observe(viewLifecycleOwner) { email ->
                    mainRepository.predictText(text, email) { result ->
                        if (isAdded) {
                            showLoading(false)
                            result.onSuccess { predictResponse ->
                                if (predictResponse.status) {
                                    resultTextView.text = predictResponse.result
                                } else {
                                    resultTextView.text = predictResponse.error ?: "Prediction failed"
                                }
                            }.onFailure {
                                resultTextView.text = "An error occurred: ${it.message}"
                            }
                        }
                    }
                }
            } else {
                resultTextView.text = "Please enter some text"
            }
        }

        resetButton.setOnClickListener {
            inputEditText.text.clear()
            resultTextView.text = "Result"
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        requireActivity().window.setFlags(
            if (isLoading) WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE else 0,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }
}