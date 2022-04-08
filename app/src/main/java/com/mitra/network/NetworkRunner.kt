package com.mitra.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


open class Emitter<OBJECT>(
    private val success: (OBJECT) -> Unit = {},
    private val error: (Throwable) -> Unit = {},
    private val complete: () -> Unit = {}
) {
    fun onSuccess(data: OBJECT) {
        success(data)
    }

    fun onError(t: Throwable) {
        error(t)
    }

    fun onComplete() {
        complete()
    }
}

open class EmitBuilder<OBJECT> {
    private var successFunc: (OBJECT) -> Unit = {}
    private var errorFunc: (Throwable) -> Unit = {}
    private var completeFunc: () -> Unit = {}

    open fun onSuccess(successFunc: (OBJECT) -> Unit): EmitBuilder<OBJECT> {
        this.successFunc = successFunc

        return this
    }

    open fun onError(errorFunc: (Throwable) -> Unit): EmitBuilder<OBJECT> {
        this.errorFunc = errorFunc

        return this
    }

    open fun onComplete(completeFunc: () -> Unit): EmitBuilder<OBJECT> {
        this.completeFunc = completeFunc

        return this
    }

    fun build() = Emitter(successFunc, errorFunc, completeFunc)
}

class MainThreadEmitBuilder<OBJECT> : EmitBuilder<OBJECT>(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + SupervisorJob()

    override fun onSuccess(successFunc: (OBJECT) -> Unit): MainThreadEmitBuilder<OBJECT> {
        super.onSuccess {
            launch {
                successFunc(it)
            }
        }

        return this
    }

    override fun onError(errorFunc: (Throwable) -> Unit): MainThreadEmitBuilder<OBJECT> {
        super.onError {
            launch {
                errorFunc(it)
            }
        }

        return this
    }

    override fun onComplete(completeFunc: () -> Unit): MainThreadEmitBuilder<OBJECT> {
        super.onComplete {
            launch {
                completeFunc()
            }
        }

        return this
    }
}

object EmptyEmitter : Emitter<Unit>()

class NetworkRunner<OBJECT>(
    emitter: Emitter<OBJECT>,
    runnable: (suspend () -> OBJECT)
) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    init {
        launch {
            try {
                val result = runnable()
                emitter.onSuccess(result)
            } catch (t: Throwable) {
                emitter.onError(t)
            } finally {
                emitter.onComplete()
            }
        }
    }
}


