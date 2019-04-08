package com.qkopy.richlink.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import java.io.Serializable

@Entity(tableName = "meta_data")
data class MetaData(
    @Json(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @Json(name = "_id")
    var _id: String,

    @Json(name = "url")
    var url: String = "",

    @Json(name = "image")
    var image: String = "",

    @Json(name = "title")
    var title: String = "",

    @Json(name = "description")
    var description: String = "",

    @Json(name = "site")
    var site: String,

    @Json(name = "media_type")
    var media_type: String,

    @Json(name = "favicon")
    var favicon: String

) : Serializable