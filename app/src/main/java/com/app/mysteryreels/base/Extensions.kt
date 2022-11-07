package com.app.mysteryreels.base

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.telephony.TelephonyManager
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


fun AppCompatActivity.showToast(@StringRes resId: Int) {
    applicationContext.showToast(resId)
}

fun AppCompatActivity.showToast(message: String) {
    applicationContext.showToast(message)
}


fun <T> AppCompatActivity.collectLatestLifecycleAware(
    flow: Flow<T>,
    collectLatest: suspend (T) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(collectLatest)
        }
    }
}

fun <T> AppCompatActivity.collectLifecycleAware(flow: Flow<T>, collect: suspend (T) -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(collect)
        }
    }
}


fun <T> Fragment.collectLatestLifecycleAware(flow: Flow<T>, collectLatest: suspend (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(collectLatest)
        }
    }
}

fun <T> Fragment.collectLifecycleAware(flow: Flow<T>, collect: suspend (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(collect)
        }
    }
}

fun Fragment.showToast(@StringRes resId: Int) {
    requireContext().showToast(resId)
}

fun Fragment.showToast(message: String) {
    requireContext().showToast(message)
}

fun Fragment.showSnackbar(message: String, view: View) {
    requireContext().showSnackbar(message, view)
}

fun Fragment.showSnackbar(@StringRes resId: Int, view: View) {
    requireContext().showSnackbar(getString(resId), view)
}

fun Context.showToast(@StringRes resId: Int) {
    showToast(getString(resId))
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showSnackbar(@StringRes resId: Int, view: View) {
    showSnackbar(getString(resId), view)
}

fun Context.showSnackbar(message: String, view: View) {
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
}

fun Context.dpToPx(dp: Float): Int =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

fun Context.copyToClipboard(text: CharSequence) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Copied text", text)
    clipboard.setPrimaryClip(clip)
}

fun Context.countryCode(): String {
    return (getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager)
        .networkCountryIso
        .uppercase()
}

fun View.margin(
    left: Float? = null,
    top: Float? = null,
    right: Float? = null,
    bottom: Float? = null
) {
    layoutParams<ViewGroup.MarginLayoutParams> {
        left?.run { leftMargin = dpToPx(this) }
        top?.run { topMargin = dpToPx(this) }
        right?.run { rightMargin = dpToPx(this) }
        bottom?.run { bottomMargin = dpToPx(this) }
    }
}

fun View.dpToPx(dp: Float): Int = context.dpToPx(dp)

fun View.show() = run { visibility = View.VISIBLE }

fun View.hide() = run { visibility = View.INVISIBLE }

fun View.gone() = run { visibility = View.GONE }

fun View.show(isShow: Boolean) = run { visibility = if (isShow) View.VISIBLE else View.GONE }

inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
    if (layoutParams is T) block(layoutParams as T)
}

fun TextView.setDrawableColor(@ColorInt color: Int) {
    compoundDrawables.filterNotNull().forEach {
        it.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
    }
}

fun EditText.syncStripsState(vararg strips: View) {
    setOnFocusChangeListener { _, hasFocus ->
        strips.forEach {
            it.isSelected = hasFocus
        }
    }
}

@ColorInt
fun String?.parseColor(): Int {
    if(this == null) return Color.WHITE

    val color = try {
        Color.parseColor(this)
    } catch (e: Exception) {
        Color.WHITE
    }

    return color
}

fun String?.firstOrEmpty(): String {
    if (this == null) return "";

    return this.first().toString()
}