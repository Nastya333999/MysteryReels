package com.app.mysteryreels

import android.app.Application

class App : Application() {
    lateinit var mysteryFile: MysteryReelsFile

    override fun onCreate() {
        super.onCreate()
        mysteryFile = MysteryReelsFile("mystery.data", this)
    }
}