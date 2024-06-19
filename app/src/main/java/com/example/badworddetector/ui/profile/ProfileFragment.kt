package com.example.badworddetector.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
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
import com.example.badworddetector.data.api.ApiConfig.apiService
import com.example.badworddetector.data.preference.UserPreference
import com.example.badworddetector.database.UserInput
import com.example.badworddetector.databinding.FragmentAboutBinding
import com.example.badworddetector.databinding.FragmentProfileBinding
import com.example.badworddetector.ui.SharedViewModel
import com.example.badworddetector.ui.login.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private lateinit var userPreference: UserPreference
    private lateinit var mainRepository: MainRepository
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var historyRecyclerView: RecyclerView
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var progressIndicator: ProgressBar

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
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        userPreference = UserPreference(requireContext())
        mainRepository = ApiConfig.provideMainRepository(requireContext())
        progressIndicator = view.findViewById(R.id.progressIndicator)

        val logoutButton = binding.logout
        logoutButton.setOnClickListener {
            logout()
        }

        val userName = binding.nama
        val userEmail = binding.email
        historyRecyclerView = binding.rvHistory

        sharedViewModel.userEmail.observe(viewLifecycleOwner) { email ->
            if (email != null) {
                userEmail.text = email
                loadUserHistoryByEmail(email)
            }
        }

        val userId = userPreference.getUserId()
        userId?.let {
            fetchUserData(it, userName)
        }
    }

    private fun fetchUserData(userId: String, userName: TextView) {
        val token = userPreference.getUserToken()

        if (token != null) {
            mainRepository.getUserData(userId, token) { result ->
                if (isAdded) {
                    result.onSuccess { userResponse ->
                        val userData = userResponse.payload.data
                        userData?.let {
                            userName.text = it.name ?: "N/A"
                        }
                    }.onFailure { exception ->
                        Toast.makeText(requireContext(), "Failed to fetch name and email: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "Token is missing", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserHistoryByEmail(email: String) {
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userInputs: List<UserInput> = mainRepository.getUserInputsByEmail(email)
                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        showLoading(false)
                        setupRecyclerView(userInputs)
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

    private fun showLoading(isLoading: Boolean) {
        progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        requireActivity().window.setFlags(
            if (isLoading) WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE else 0,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun setupRecyclerView(userInputs: List<UserInput>) {
        historyAdapter = HistoryAdapter(userInputs)
        historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyRecyclerView.adapter = historyAdapter
    }

    private fun logout() {
        userPreference.clearUserPreference()
        navigateToLoginScreen()
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}