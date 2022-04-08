package com.mitra.data

import com.mitra.R

class AvatarArray {
    val array = mutableListOf<AvatarList>()
    var count = 0


    init {
        addNewList(R.string.boys_text, MEN_ADD) {
            add(AvatarItem(it + size, R.drawable.ic_man_1))
            add(AvatarItem(it + size, R.drawable.ic_man_2))
            add(AvatarItem(it + size, R.drawable.ic_man_3))
            add(AvatarItem(it + size, R.drawable.ic_man_4))
            add(AvatarItem(it + size, R.drawable.ic_man_5))
            add(AvatarItem(it + size, R.drawable.ic_man_6))
            add(AvatarItem(it + size, R.drawable.ic_man_7))
            add(AvatarItem(it + size, R.drawable.ic_man_8))
            add(AvatarItem(it + size, R.drawable.ic_man_9))
            add(AvatarItem(it + size, R.drawable.ic_man_10))
            add(AvatarItem(it + size, R.drawable.ic_man_11))
            add(AvatarItem(it + size, R.drawable.ic_man_12))
            add(AvatarItem(it + size, R.drawable.ic_man_13))
            add(AvatarItem(it + size, R.drawable.ic_man_14))
            add(AvatarItem(it + size, R.drawable.ic_man_15))
            add(AvatarItem(it + size, R.drawable.ic_man_16))
            add(AvatarItem(it + size, R.drawable.ic_man_17))
            add(AvatarItem(it + size, R.drawable.ic_man_18))
            add(AvatarItem(it + size, R.drawable.ic_man_19))
            add(AvatarItem(it + size, R.drawable.ic_man_20))
            add(AvatarItem(it + size, R.drawable.ic_man_21))
        }
        addNewList(R.string.girls_text, WOMEN_ADD) {
            add(AvatarItem(it + size, R.drawable.ic_woman_1))
            add(AvatarItem(it + size, R.drawable.ic_woman_2))
            add(AvatarItem(it + size, R.drawable.ic_woman_3))
            add(AvatarItem(it + size, R.drawable.ic_woman_4))
            add(AvatarItem(it + size, R.drawable.ic_woman_5))
            add(AvatarItem(it + size, R.drawable.ic_woman_6))
            add(AvatarItem(it + size, R.drawable.ic_woman_7))
            add(AvatarItem(it + size, R.drawable.ic_woman_8))
            add(AvatarItem(it + size, R.drawable.ic_woman_9))
            add(AvatarItem(it + size, R.drawable.ic_woman_10))
            add(AvatarItem(it + size, R.drawable.ic_woman_11))
            add(AvatarItem(it + size, R.drawable.ic_woman_12))
            add(AvatarItem(it + size, R.drawable.ic_woman_13))
            add(AvatarItem(it + size, R.drawable.ic_woman_14))
            add(AvatarItem(it + size, R.drawable.ic_woman_15))
            add(AvatarItem(it + size, R.drawable.ic_woman_16))
            add(AvatarItem(it + size, R.drawable.ic_woman_17))
            add(AvatarItem(it + size, R.drawable.ic_woman_18))
            add(AvatarItem(it + size, R.drawable.ic_woman_19))
            add(AvatarItem(it + size, R.drawable.ic_woman_20))
            add(AvatarItem(it + size, R.drawable.ic_woman_21))
        }
    }

    private fun addNewList(
        stringId: Int, indexAvatars: Int, init: MutableList<AvatarItem>.(Int) -> Unit
    ) {
        array.add(AvatarList(stringId, mutableListOf<AvatarItem>().apply {
            init(indexAvatars)
        }))
    }

    fun findAvatar(avatarId: Int, prepareInnerFunc: ((Int, Int) -> Unit)? = null): AvatarItem? {
        var avatar: AvatarItem? = null

        run loop@{
            array.forEach { list ->
                avatar = list.listAvatars.find { avatar -> avatar.index == avatarId }
                avatar?.let {
                    prepareInnerFunc?.invoke(array.indexOf(list), list.listAvatars.indexOf(it))

                    return@loop
                }
            }
        }

        return avatar
    }

    fun findIndex(avatarId: Int): Pair<Int, Int> {
        var indexPage = 0
        var indexAvatar = 0
        findAvatar(avatarId) { page, avatar ->
            indexPage = page
            indexAvatar = avatar
        }

        return indexPage to indexAvatar
    }

    companion object {
        const val MEN_ADD = 500
        const val WOMEN_ADD = 1000
    }

}

class AvatarItem(val index: Int, val resourceId: Int)

class AvatarList(val stringId: Int, val listAvatars: MutableList<AvatarItem>)