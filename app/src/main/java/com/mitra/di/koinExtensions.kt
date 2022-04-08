package com.mitra.di

import org.koin.core.context.KoinContextHandler

inline fun <reified T> inject(): T? {
    return KoinContextHandler.getOrNull()?.get()
}