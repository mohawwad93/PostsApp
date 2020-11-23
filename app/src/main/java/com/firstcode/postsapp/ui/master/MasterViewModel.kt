package com.firstcode.postsapp.ui.master

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.firstcode.postsapp.repository.paging.model.Post
import com.firstcode.postsapp.repository.PostsRepository
import com.firstcode.postsapp.repository.paging.database.PostsDatabase.Companion.getDatabase

import kotlinx.coroutines.launch


class MasterViewModel(val application: Application) : ViewModel(){

    var scrollPosition = 0
    private val repository = PostsRepository(getDatabase(application))

    val posts = repository.posts.cachedIn(viewModelScope)

    private val _navigateToSelectedPost =  MutableLiveData<Post>()
    val navigateToSelectedPost: LiveData<Post>
        get() = _navigateToSelectedPost

    private val _deletedPost =  MutableLiveData<Boolean>()
    val deletedPost: LiveData<Boolean>
        get() = _deletedPost

    fun deletePost(post: Post){
        viewModelScope.launch {
            _deletedPost.value = repository.deletePost(post)
        }
    }

    fun displayPostDetails(post: Post){
        _navigateToSelectedPost.value = post
    }

    fun displayPostDetailsComplete(){
        _navigateToSelectedPost.value = null
    }

    class Factory(private val application: Application): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MasterViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MasterViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")

        }
    }
}