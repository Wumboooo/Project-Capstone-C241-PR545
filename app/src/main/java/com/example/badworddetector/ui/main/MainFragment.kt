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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.badworddetector.R
import com.example.badworddetector.data.MainRepository
import com.example.badworddetector.data.api.ApiConfig
import com.example.badworddetector.data.preference.UserPreference
import com.example.badworddetector.database.UserInput
import com.example.badworddetector.databinding.FragmentMainBinding
import com.example.badworddetector.ui.SharedViewModel
import com.example.badworddetector.ui.profile.HistoryAdapter
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var mainRepository: MainRepository
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var progressIndicator: ProgressBar
    private lateinit var userPreference: UserPreference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        mainRepository = ApiConfig.provideMainRepository(requireContext())
        userPreference = UserPreference(requireContext())

        progressIndicator = view.findViewById(R.id.progressIndicator)

        val inputEditText = view.findViewById<EditText>(R.id.input)
        val resultTextView = view.findViewById<TextView>(R.id.tvResult)
        val checkButton = view.findViewById<Button>(R.id.btn_check)
        val resetButton = view.findViewById<Button>(R.id.btn_reset)

        fetchUserDetails()

        checkButton.setOnClickListener {
            val text = inputEditText.text.toString().trim()
            if (text.isNotEmpty()) {
                resultTextView.text = ""
                showLoading(true)
                sharedViewModel.userEmail.observe(viewLifecycleOwner) { email ->
                    if (email != null) {
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
                    } else {
                        resultTextView.text = "Failed to get user email"
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

    private fun fetchUserDetails() {
        val userName = view?.findViewById<TextView>(R.id.nama)
        val userEmail = view?.findViewById<TextView>(R.id.email)
        val historyRecyclerView = view?.findViewById<RecyclerView>(R.id.rvHistory)

        val userId = userPreference.getUserId()
        val token = userPreference.getUserToken()

        if (userId != null && token != null) {
            mainRepository.getUserData(userId, token) { result ->
                if (isAdded) {
                    result.onSuccess { userResponse ->
                        val userData = userResponse.payload.data
                        userData?.let {
                            sharedViewModel.setUserEmail(it.email ?: "N/A")
                            userName?.text = it.name ?: "N/A"
                            userEmail?.text = it.email ?: "N/A"
                            loadUserHistoryByEmail(it.email.toString(), historyRecyclerView)
                        }
                    }.onFailure { exception ->
                        Toast.makeText(requireContext(), "Failed to fetch user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "User ID or Token is missing", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserHistoryByEmail(email: String, recyclerView: RecyclerView?) {
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userInputs: List<UserInput> = mainRepository.getUserInputsByEmail(email)
                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        showLoading(false)
                        setupRecyclerView(userInputs, recyclerView)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        showLoading(false)
                        Toast.makeText(requireContext(), "Failed to load history: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupRecyclerView(userInputs: List<UserInput>, recyclerView: RecyclerView?) {
        val historyAdapter = HistoryAdapter(userInputs)
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        recyclerView?.adapter = historyAdapter
    }

    private fun showLoading(isLoading: Boolean) {
        progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        requireActivity().window.setFlags(
            if (isLoading) WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE else 0,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }
}