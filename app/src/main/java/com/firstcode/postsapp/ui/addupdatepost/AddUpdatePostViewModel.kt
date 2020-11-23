package com.firstcode.postsapp.ui.addupdatepost

import android.app.Application
import androidx.lifecycle.*
import com.firstcode.postsapp.repository.PostsRepository
import com.firstcode.postsapp.repository.paging.database.PostsDatabase
import com.firstcode.postsapp.repository.paging.model.Post
import kotlinx.coroutines.launch



class AddUpdatePostViewModel(application: Application) : ViewModel(){

    private val repository = PostsRepository(PostsDatabase.getDatabase(application))
    private val _post = MutableLiveData<Post?>()
    val post: LiveData<Post?>
        get() = _post

    fun displayMasterFragment(post: Post){
       viewModelScope.launch {
           _post.value = repository.addPost(post)
       }
    }

    fun displayDetailsFragment(post: Post){
        viewModelScope.launch {
            _post.value = repository.updatePost(post)
        }
    }

    fun navigateAwayCompleted(owner: LifecycleOwner){
        post.removeObservers(owner)
        _post.value = null
    }


    class Factory(val application: Application): ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T{
            if (modelClass.isAssignableFrom(AddUpdatePostViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddUpdatePostViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")

        }
    }
}

