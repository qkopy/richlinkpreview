package com.qkopy.richlink.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.qkopy.richlink.data.model.MetaData
import com.qkopy.richlink.data.dao.MetaDataDao
import com.qkopy.richlink.data.typeconverters.DateTypeConverts

@Database(
    entities = [
        MetaData::class
    ],
    version = 1, exportSchema = true
)
@TypeConverters(DateTypeConverts::class)
abstract class MetaDatabase : RoomDatabase() {
    abstract fun metaDataDao(): MetaDataDao

    companion object {
        var qkopyDatabase: MetaDatabase? = null
        fun getInstance(context: Context): MetaDatabase {
            if (qkopyDatabase == null) {
                qkopyDatabase = Room.databaseBuilder(context.applicationContext, MetaDatabase::class.java, "meta.db")
                    .build()
            }
            return qkopyDatabase!!
        }
    }

}