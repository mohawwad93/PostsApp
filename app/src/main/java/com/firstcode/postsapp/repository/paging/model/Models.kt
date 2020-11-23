package com.firstcode.postsapp.repository.paging.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
@Entity(tableName = "posts")
data class Post(
    @PrimaryKey val id: Int,
    val title: String,
    val url: String): Parcelable


@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val postId: Int,
    val prevKey: Int?,
    val nextKey: Int?)