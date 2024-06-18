package com.example.badworddetector.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreference = UserPreference(requireContext())
        mainRepository = ApiConfig.provideMainRepository(requireContext())

        val logoutButton = view.findViewById<Button>(R.id.logout)
        logoutButton.setOnClickListener {
            logout()
        }

        val userName = view.findViewById<TextView>(R.id.nama)
        val userEmail = view.findViewById<TextView>(R.id.email)
        historyRecyclerView = view.findViewById(R.id.rvHistory)

        val userId = userPreference.getUserId()
        userId?.let {
            fetchUserData(it, userName, userEmail)
        }

    }

    private fun fetchUserData(userId: String, userName: TextView, userEmail: TextView) {
        val token = userPreference.getUserToken()

        if (token != null) {
            mainRepository.getUserData(userId, token) { result ->
                result.onSuccess { userResponse ->
                    val userData = userResponse.payload.data
                    userData?.let {
                        userName.text = it.name ?: "N/A"
                        userEmail.text = it.email ?: "N/A"
                        loadUserHistoryByEmail(it.email.toString())
                    }
                }.onFailure { exception ->
                    Toast.makeText(requireContext(), "Failed to fetch name and email: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "Token is missing", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserHistoryByEmail(email: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userInputs: List<UserInput> = mainRepository.getUserInputsByEmail(email)
                withContext(Dispatchers.Main) {
                    setupRecyclerView(userInputs)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to load history: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
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