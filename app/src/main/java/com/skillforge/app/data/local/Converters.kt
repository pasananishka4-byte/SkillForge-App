package com.skillforge.app.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromLongList(list: List<Long>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toLongList(value: String): List<Long> {
        return if (value.isEmpty()) emptyList()
        else value.split(",").map { it.trim().toLong() }
    }

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return list.joinToString("|||")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList()
        else value.split("|||")
    }
}
