package com.qkopy.richlink.data.typeconverters

import android.arch.persistence.room.TypeConverter
import java.sql.Date


object DateTypeConverts {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return (date?.time)
    }
}