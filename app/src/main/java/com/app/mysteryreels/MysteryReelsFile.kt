package com.app.mysteryreels

import android.content.Context
import android.util.Log
import java.io.File

class MysteryReelsFile(private val name: String, private val context: Context) {

    fun exists(): Boolean = File(context.filesDir, name).exists()

    fun readGonzoData() = context.openFileInput(name).bufferedReader().useLines { it.first() }

    fun writeMysteryData(data: String) {
        Log.e("MysteryReelsFile", "data = $data")
        if (!exists() && !data.contains(BASE_API_URL)) {
            context.openFileOutput(name, Context.MODE_PRIVATE).use {
                it.write(data.toByteArray())
            }
        }
    }

    companion object {
        const val PREFIX = "https://"
        const val BASE_URL = "lionclash.xyz/"
        const val BASE_API_URL = BASE_URL + "bertbert.php"
    }
}