package com.mitra.di

class NewDi {
    val dependencies: MutableMap<Class<*>, Any> = mutableMapOf()

    inline fun <reified T> inject(): T = dependencies[T::class.java] as T

    inline fun <reified T> register(obj: T) {
        dependencies[T::class.java] = obj as Any
    }

    companion object {
        private var di: NewDi? = null

        fun getInstance(): NewDi {
            if (di == null)
                di = NewDi()

            return di!!
        }

        fun clear() {
            di?.dependencies?.clear()
            di = null
        }
    }
}

inline fun <reified T> injectT(): T = NewDi.getInstance().inject()