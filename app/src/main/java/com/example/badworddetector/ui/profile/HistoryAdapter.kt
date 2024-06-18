package com.example.badworddetector.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.badworddetector.R
import com.example.badworddetector.database.UserInput

class HistoryAdapter(private val userInputList: List<UserInput>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text)
        val resultView: TextView = view.findViewById(R.id.result)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userInput = userInputList[position]
        holder.textView.text = userInput.text
        holder.resultView.text = userInput.result
    }

    override fun getItemCount() = userInputList.size
}