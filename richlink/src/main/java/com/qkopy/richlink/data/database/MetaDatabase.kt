package com.qkopy.richlink.data.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.arch.persistence.room.migration.Migration
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
        private val TAG = MetaDatabase::class.java.simpleName
        var qkopyDatabase: MetaDatabase? = null
        fun getInstance(context: Context): MetaDatabase {
            if (qkopyDatabase == null) {
                qkopyDatabase = Room.databaseBuilder(context.applicationContext, MetaDatabase::class.java, "meta.db")
                    .addMigrations(migration).build()
            }
            return qkopyDatabase!!
        }

        private val migration: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the new table
                println("No migrations found")
            }
        }
    }

}