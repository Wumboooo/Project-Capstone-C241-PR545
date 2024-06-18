package com.example.badworddetector.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserInputDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userInput: UserInput)

    @Query("SELECT * FROM user_inputs ORDER BY id DESC")
    suspend fun getAll(): List<UserInput>

    @Query("SELECT * FROM user_inputs WHERE email = :email ORDER BY id DESC")
    suspend fun getByEmail(email: String): List<UserInput>
}