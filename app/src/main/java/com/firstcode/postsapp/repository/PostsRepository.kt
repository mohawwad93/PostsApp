package com.firstcode.postsapp.repository

import androidx.paging.*
import com.firstcode.postsapp.repository.paging.model.Post
import com.firstcode.postsapp.repository.paging.database.PostsDatabase
import com.firstcode.postsapp.repository.paging.model.RemoteKeys
import com.firstcode.postsapp.repository.paging.network.NetworkService
import com.firstcode.postsapp.repository.paging.PostRemoteMediator
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.lang.Exception
import kotlin.math.ceil


class PostsRepository(private val database: PostsDatabase) {

    private val postApiService = NetworkService.postApiService

    companion object {
        private const val PAGE_SIZE = 30
        private const val PREFETCH_DISTANCE = 30
    }

    val posts: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = PREFETCH_DISTANCE, enablePlaceholders = false),
        remoteMediator = PostRemoteMediator(postApiService, database),
        pagingSourceFactory = {database.postDao.getPosts()}
    ).flow

    suspend fun addPost(post: Post): Post?{
        try {
            val response = postApiService.addPost(post)
            response?.let {
                Timber.d("Post added in remote successfully")
                val page = ceil((response.id.toDouble() / PAGE_SIZE)).toInt()
                val remoteKeys = RemoteKeys(response.id, page-1, page+1)
                database.postDao.insertAll(response)
                database.remoteKeysDao.insertAll(remoteKeys)
                Timber.d("Post added successfully")
                return response

            }
        }catch (ex: Exception){
            Timber.e("Fail to add post, ${ex.message}")
        }
        return null
    }

    suspend fun deletePost(post: Post) : Boolean{
        try {
            postApiService.deletePost(post.id)
            Timber.d("Post deleted from remote successfully")
            database.postDao.delete(post)
            database.remoteKeysDao.delete(post.id)
            Timber.d("Post deleted successfully")
            return true
        }catch (ex: Exception){
            Timber.e("Fail to delete post, ${ex.message}")
            return false
        }
    }

    suspend fun updatePost(post: Post): Post?{
        try {
            val response = postApiService.updatePost(post.id, post)
            response?.let {
                Timber.d("Post updated in remote successfully")
                database.postDao.insertAll(response)
                Timber.d("Post updated successfully")
                return response
            }
        }catch (ex: Exception){
            Timber.e("Fail to update post, ${ex.message}")
        }
        return null
    }



}