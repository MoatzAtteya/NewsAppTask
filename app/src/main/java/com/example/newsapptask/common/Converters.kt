package com.example.newsapptask.common

import androidx.room.TypeConverter
import com.example.newsapptask.domain.model.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(name : String) : Source {
        return Source(name , name)
    }
}