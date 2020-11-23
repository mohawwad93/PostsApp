package com.firstcode.postsapp.repository.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.firstcode.postsapp.repository.paging.model.Post
import com.firstcode.postsapp.repository.paging.model.RemoteKeys

import com.firstcode.postsapp.repository.paging.database.PostsDatabase
import com.firstcode.postsapp.repository.paging.network.*
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.io.InvalidObjectException

private const val POSTS_STARTING_PAGE_INDEX = 162

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val postApiService: PostApiService,
    private val database: PostsDatabase) : RemoteMediator<Int, Post>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Post>): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: POSTS_STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                if (remoteKeys == null) {
                    throw InvalidObjectException("Remote key and the prevKey should not be null $loadType")
                }
                remoteKeys.prevKey ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                if (remoteKeys == null || remoteKeys.nextKey == null) {
                    throw InvalidObjectException("Remote key should not be null for $loadType")
                }
                remoteKeys.nextKey
            }

        }

        try {
            val postsResponse = postApiService.getPosts(page, state.config.pageSize)
            val endOfPaginationReached = postsResponse.isEmpty()
            Timber.d("Page loaded from remote successfully")
            database.withTransaction {
                val prevKey = if (page == POSTS_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val remoteKeys = postsResponse.map { RemoteKeys(it.id, prevKey, nextKey) }
                database.remoteKeysDao.insertAll(*remoteKeys.toTypedArray())
                database.postDao.insertAll(*postsResponse.toTypedArray())
                Timber.d("Page loaded successfully")

            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            Timber.e("Fail to load page ${page}, ${exception.message}")
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            Timber.e("Fail to load page ${page}, ${exception.message}")
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Post>): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull{ it.data.isNotEmpty() }?.data?.lastOrNull()?.let { post ->
                // Get the remote keys of the last item retrieved
                database.remoteKeysDao.remoteKeysPostId(post.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Post>): RemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { post ->
                // Get the remote keys of the first items retrieved
                database.remoteKeysDao.remoteKeysPostId(post.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Post>
    ): RemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { postId ->
                database.remoteKeysDao.remoteKeysPostId(postId)
            }
        }
    }

}