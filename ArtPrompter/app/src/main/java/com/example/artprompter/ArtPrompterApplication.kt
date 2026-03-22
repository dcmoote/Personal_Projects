package com.example.artprompter

import android.app.Application
import com.example.artprompter.util.AppContainer

class ArtPrompterApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
