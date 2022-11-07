package com.app.mysteryreels.presentation


import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.app.mysteryreels.App
import com.app.mysteryreels.MysteryReelsFile
import com.app.mysteryreels.R
import com.app.mysteryreels.base.AppsWrapper
import com.app.mysteryreels.base.BaseViewModel
import com.app.mysteryreels.base.FileDataCreator
import com.app.mysteryreels.base.OneWrapper
import com.app.mysteryreels.data.Const
import com.app.mysteryreels.data.SlotItem
import com.app.mysteryreels.data.SlotState
import com.appsflyer.AppsFlyerLib
import com.facebook.applinks.AppLinkData
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random

class MainViewModel(
    private val app: Application,
) : BaseViewModel(app) {

    private val _fileData = MutableStateFlow<String>("")
    val fileData = _fileData.asStateFlow()

    private var creditsAmount = 0

    private val _slotState = MutableSharedFlow<SlotState>()
    val slotState = _slotState.asSharedFlow()

    private val _isLoze = MutableSharedFlow<Boolean>()
    val isLoze = _isLoze.asSharedFlow()

    private val _dispalyCredits = MutableStateFlow<Int>(0)
    val displayCredits = _dispalyCredits.asSharedFlow()

    private val slotItems = listOf(
        SlotItem(R.drawable.icon_1),
        SlotItem(R.drawable.icon_2),
        SlotItem(R.drawable.icon_3),
        SlotItem(R.drawable.icon_5),
        SlotItem(R.drawable.icon_6),
    )

    fun setCredits(newVal: Int) {
        creditsAmount = newVal
        runOperation {
            work {
                _dispalyCredits.emit(creditsAmount)
            }
        }
    }

    fun runSlot() {
        runOperation {
            work {
                val left = slotItems[Random.nextInt(slotItems.size)]
                val center = slotItems[Random.nextInt(slotItems.size)]
                val right = slotItems[Random.nextInt(slotItems.size)]

                if (left == center && center == right)
                    creditsAmount += 100
                else
                    creditsAmount -= 20

                if (creditsAmount <= 0) {
                    _isLoze.emit(true)
                    return@work
                }

                val state = SlotState(
                    slotLeft = left,
                    slotCenter = center,
                    slotRight = right
                )
                _dispalyCredits.emit(creditsAmount)
                _slotState.emit(state)
            }
        }

    }


    fun init(activity: MainActivity) {
        viewModelScope.launch(Dispatchers.IO) {

            if ((app as App).mysteryFile.exists()) {
                Log.e("Initialization", "mysteryFile exists")

                _fileData.emit((app).mysteryFile.readGonzoData())
            } else {
                val apps = getAppsflyer(activity)
                val deep = deepFlow(activity)
                val adId = AdvertisingIdClient.getAdvertisingIdInfo(activity).id.toString()
                val uId = AppsFlyerLib.getInstance().getAppsFlyerUID(activity)!!

                OneWrapper(app, adId).send(apps?.get("campaign").toString(), deep)

                _fileData.emit(
                    FileDataCreator.create(
                        res = app.resources,
                        baseFileData = MysteryReelsFile.BASE,
                        gadid = adId,
                        apps = if (deep == "null") apps else null,
                        deep = deep,
                        uid = if (deep == "null") uId else null
                    )
                )
            }
        }
    }


    private suspend fun getAppsflyer(activity: MainActivity): MutableMap<String, Any>? = suspendCoroutine { coroutine ->
        Log.e("Initialization", "start appsFlow")

        val callback = object : AppsWrapper {
            override fun onConversionDataSuccess(convData: MutableMap<String, Any>?) {

                Log.e("Initialization", "onConversionDataSuccess $convData")
                coroutine.resume(convData)
            }

            override fun onConversionDataFail(p0: String?) {
                Log.e("Initialization", "onConversionDataFail $p0")
                coroutine.resume(null)
            }
        }
        AppsFlyerLib.getInstance().init(Const.APPS_FLYER_KEY, callback, activity)
        Log.e("Initialization", "init appsFlow")

        AppsFlyerLib.getInstance().start(activity)

        Log.e("Initialization", "end appsFlow")

    }

    private suspend fun deepFlow(activity: MainActivity): String = suspendCoroutine { coroutine ->
        Log.e("Initialization", "deepFlow start")

        val callback = AppLinkData.CompletionHandler {
            Log.e("Initialization", "deepFlow callback = $it")
            coroutine.resume(it?.targetUri.toString())
        }
        AppLinkData.fetchDeferredAppLinkData(activity, callback)
        Log.e("Initialization", "deepFlow end")

    }

}