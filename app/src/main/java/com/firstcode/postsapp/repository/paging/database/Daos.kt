package com.firstcode.postsapp.repository.paging.database

import androidx.paging.PagingSource
import androidx.room.*
import com.firstcode.postsapp.repository.paging.model.Post
import com.firstcode.postsapp.repository.paging.model.RemoteKeys
import org.jetbrains.annotations.TestOnly

@Dao
interface PostDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg posts: Post)

    @Update
    suspend fun update(post: Post)

    @Delete
    suspend fun delete(post: Post)

    @Query("DELETE FROM posts")
    suspend fun clear()

    @Query("SELECT * FROM posts ORDER BY id ASC")
    fun getPosts(): PagingSource<Int, Post>

    @TestOnly @Query("SELECT * FROM posts ORDER BY id ASC")
    fun getPostsTest(): List<Post>
}

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg remoteKeys :RemoteKeys)

    @Query("SELECT * FROM remote_keys WHERE postId = :postId")
    suspend fun remoteKeysPostId(postId: Int): RemoteKeys?

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()

    @Query("DELETE FROM remote_keys WHERE postId = :postId")
    suspend fun delete(postId: Int)

}