package com.alrinkz

import android.app.Application
import com.alrinkz.data.local.AppDatabase
import com.alrinkz.data.repository.AlrinRepository

class AlrinApp : Application() {
    
    lateinit var repository: AlrinRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getDatabase(this)
        repository = AlrinRepository(database)
        instance = this
    }

    companion object {
        lateinit var instance: AlrinApp
            private set
    }
}