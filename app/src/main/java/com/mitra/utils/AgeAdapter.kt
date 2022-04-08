package com.mitra.utils

class AgeAdapter {
    companion object {
        fun getAge(ageIndex: Int): Int =
            when (ageIndex) {
                0 -> 18
                25 -> 22
                50 -> 26
                75 -> 30
                else -> 100
            }

        fun getAgeIndex(age: Int): Int =
            when (age) {
                18 -> 0
                22 -> 25
                26 -> 50
                30 -> 75
                else -> 100
            }
    }
}