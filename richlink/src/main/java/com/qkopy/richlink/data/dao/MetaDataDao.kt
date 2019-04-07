package com.qkopy.richlink.data.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.qkopy.richlink.data.model.MetaData

@Dao
interface MetaDataDao {

    @Insert(onConflict = REPLACE)
    fun insert(metadata: MetaData)

    @Insert(onConflict = REPLACE)
    fun insertAll(queues: List<MetaData>)

    @Query("SELECT DISTINCT * FROM meta_data")
    fun getQueues(): List<MetaData>

    @Query("SELECT DISTINCT * FROM meta_data WHERE url = :url LIMIT 1")
    fun getMetaDataUrl(url: String): MetaData

    @Query("DELETE FROM meta_data where id NOT IN (SELECT id from meta_data ORDER BY id DESC LIMIT 100)")
    fun deleteLimit()

}