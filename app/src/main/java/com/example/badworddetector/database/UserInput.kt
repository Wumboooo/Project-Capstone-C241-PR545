package com.example.badworddetector.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_inputs")
data class UserInput(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val result: String,
    val email: String  // New field for storing email
)