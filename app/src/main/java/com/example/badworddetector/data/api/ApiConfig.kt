package com.example.badworddetector.data.api

import android.content.Context
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.badworddetector.data.MainRepository
import com.example.badworddetector.database.AppDatabase
import com.example.badworddetector.database.UserInputDao
import com.google.gson.GsonBuilder
import de.hdodenhof.circleimageview.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    private const val BASE_URL = "https://wordwardenapp.et.r.appspot.com/"
    private const val BASE_ML_URL = "https://wordwarden-flask-api-wn2mamywtq-et.a.run.app"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val mlRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_ML_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val mlApiService: MlApiService by lazy {
        mlRetrofit.create(MlApiService::class.java)
    }

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create the table if it doesn't exist
            database.execSQL("""
            CREATE TABLE IF NOT EXISTS user_inputs (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                text TEXT NOT NULL,
                result TEXT NOT NULL
            )
        """.trimIndent())
            // If there are additional columns to add, include them here
            // For example: database.execSQL("ALTER TABLE user_inputs ADD COLUMN new_column_name TEXT")
        }
    }

    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "user-input-database"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    fun provideUserInputDao(database: AppDatabase): UserInputDao {
        return database.userInputDao()
    }

    fun provideMainRepository(context: Context): MainRepository {
        val database = provideDatabase(context)
        return MainRepository(apiService, mlApiService, provideUserInputDao(database))
    }
}
