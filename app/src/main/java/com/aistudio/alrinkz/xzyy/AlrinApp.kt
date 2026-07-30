package com.aistudio.alrinkz.xzyy

import android.app.Application
import com.aistudio.alrinkz.xzyy.data.local.AppDatabase
import com.aistudio.alrinkz.xzyy.data.repository.AlrinRepository

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
