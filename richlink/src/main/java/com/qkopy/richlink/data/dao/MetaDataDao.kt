package com.qkopy.richlink.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.qkopy.richlink.data.model.MetaData

@Dao
interface MetaDataDao {

    @Insert(onConflict = REPLACE)
    fun insert(metadata: MetaData)

    @Query("SELECT DISTINCT * FROM meta_data WHERE url = :url LIMIT 1")
    fun getMetaDataUrl(url: String): MetaData

    @Query("DELETE FROM meta_data where id NOT IN (SELECT id from meta_data ORDER BY id DESC LIMIT :limit)")
    fun delete(limit:Int)

}