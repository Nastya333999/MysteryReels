package com.app.mysteryreels.base

import android.content.Context
import com.onesignal.OneSignal

class OneWrapper(context: Context, uid: String) {
    init {
        OneSignal.initWithContext(context)
        OneSignal.setAppId(ID)
        OneSignal.setExternalUserId(uid)
    }

    fun send(campaign: String, deep: String) {
        when {
            campaign == "null" && deep == "null" -> {
                OneSignal.sendTag("key2", "organic")
            }
            deep != "null" -> {
                OneSignal.sendTag("key2", deep.replace("myapp://", "").substringBefore("/"))
            }
            campaign != "null" -> {
                OneSignal.sendTag("key2", campaign.substringBefore("_"))
            }
        }
    }

    companion object {
        private const val ID = "f7d48586-aabe-4836-8a2d-8959f2736bd9"
    }
}