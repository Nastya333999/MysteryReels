package com.app.mysteryreels.base

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

open class BaseViewModel(app: Application) : AndroidViewModel(app) {

    private val _errorText = MutableSharedFlow<String>()
    val errorText = _errorText.asSharedFlow()

    fun runOperation(
        scope: CoroutineScope = viewModelScope,
        operationBuilderFunc: CoroutineWorkBuilder.() -> Unit
    ) = scope.launch {
        val builder = CoroutineWorkBuilder().apply { operationBuilderFunc() }
        try {
            builder.progressFunction?.invoke(this, true)
            builder.workFunction?.invoke(this)
        } catch (error: Throwable) {
            Log.e("Run Operation", error.message ?: "")
            _errorText.emit(error.toString())
            builder.errorFunction?.invoke(this, error)
        } finally {
            builder.progressFunction?.invoke(this, false)
        }
    }

}


class CoroutineWorkBuilder {
    internal var progressFunction: (suspend CoroutineScope.(Boolean) -> Unit)? = null
    internal var workFunction: (suspend CoroutineScope.() -> Unit)? = null
    internal var errorFunction: (suspend CoroutineScope.(Throwable) -> Unit)? = null

    fun progress(progressFunc: suspend CoroutineScope.(Boolean) -> Unit) {
        this.progressFunction = progressFunc
    }

    fun work(workFunc: suspend CoroutineScope.() -> Unit) {
        this.workFunction = workFunc
    }

    fun error(errorFunc: suspend CoroutineScope.(Throwable) -> Unit) {
        this.errorFunction = errorFunc
    }
}
