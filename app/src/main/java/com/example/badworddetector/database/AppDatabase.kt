package com.example.badworddetector.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserInput::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userInputDao(): UserInputDao
}