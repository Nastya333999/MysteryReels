package com.app.mysteryreels.presentation

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.view.View
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.app.mysteryreels.App
import com.app.mysteryreels.MysteryReelsFile
import com.app.mysteryreels.R
import com.app.mysteryreels.base.BaseBindingFragment
import com.app.mysteryreels.databinding.MysteryWebViewBinding
import com.google.android.material.snackbar.Snackbar


class WebViewMysteryFragment :
    BaseBindingFragment<MysteryWebViewBinding>(MysteryWebViewBinding::inflate) {
    private lateinit var mysteryWebView: WebView
    private lateinit var valueCallback: ValueCallback<Array<Uri?>>
    private val args: WebViewMysteryFragmentArgs by navArgs()

    val data = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) {
        valueCallback.onReceiveValue(it.toTypedArray())
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
        requireActivity().window.statusBarColor =
            resources.getColor(R.color.black, requireActivity().theme)

        mysteryWebView = binding.mystaryWebView
        CookieManager.getInstance().setAcceptThirdPartyCookies(mysteryWebView, true)
        CookieManager.getInstance().setAcceptCookie(true)

        mysteryWebView.loadUrl(args.url)
        mysteryWebView.settings.loadWithOverviewMode = false
        mysteryWebView.settings.userAgentString =
            mysteryWebView.settings.userAgentString.replace("wv", "")

        mysteryWebView.settings.javaScriptEnabled = true
        mysteryWebView.settings.domStorageEnabled = true

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (mysteryWebView.canGoBack()) {
                        mysteryWebView.goBack()
                    } else {
                        isEnabled = false
                    }
                }
            })

        mysteryWebView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                Snackbar.make(view, error.description, Snackbar.LENGTH_LONG).show()
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                CookieManager.getInstance().flush()

                val navController = findNavController()

                if (MysteryReelsFile.BASE.substringBefore("/") == url) {
                    val action = WebViewMysteryFragmentDirections.actionWebViewToMainFragment()
                    navController.navigate(action)
                } else {
                    (requireActivity().application as App).mysteryFile.writeMysteryData(url)
                }
            }
        }

        mysteryWebView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri?>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                valueCallback = filePathCallback
                data.launch(IMAGE_MIME_TYPE)
                return true
            }

            @SuppressLint("SetJavaScriptEnabled")
            override fun onCreateWindow(
                view: WebView?, isDialog: Boolean,
                isUserGesture: Boolean, resultMsg: Message
            ): Boolean {
                val newWebView = WebView(requireContext())
                newWebView.settings.javaScriptEnabled = true
                newWebView.webChromeClient = this
                newWebView.settings.javaScriptCanOpenWindowsAutomatically = true
                newWebView.settings.domStorageEnabled = true
                newWebView.settings.setSupportMultipleWindows(true)
                val transport = resultMsg.obj as WebView.WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()
                return true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    companion object {
        private const val IMAGE_MIME_TYPE = "image/*"
        private const val USER_AGENT =
            "Mozilla/5.0 (Linux; Android 7.0; SM-G930V Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.125 Mobile Safari/537.36"
    }
}