package com.mitra.utils

class NameAdapter {
    companion object {
        fun decodeName(name: String) = name.replace("_number_", "№")
        fun encodeName(name: String) = name.replace("№", "_number_")
    }
}