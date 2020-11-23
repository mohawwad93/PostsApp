package com.firstcode.postsapp.ui.details

import androidx.lifecycle.*
import com.firstcode.postsapp.repository.paging.model.Post


class DetailsViewModel(post: Post) : ViewModel(){

    private val _selectedPost = MutableLiveData<Post>()
    val selectedPost: LiveData<Post>
        get() = _selectedPost

    init {
        _selectedPost.value = post
    }

    class Factory(private val post: Post): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DetailsViewModel(post) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")

        }
    }
}

