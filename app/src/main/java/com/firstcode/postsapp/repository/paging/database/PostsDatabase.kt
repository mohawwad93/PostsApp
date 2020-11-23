package com.firstcode.postsapp.repository.paging.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.firstcode.postsapp.repository.paging.model.Post
import com.firstcode.postsapp.repository.paging.model.RemoteKeys
import timber.log.Timber

@Database(entities = [Post::class, RemoteKeys::class], version = 1, exportSchema = false)
abstract class PostsDatabase : RoomDatabase() {

    abstract val postDao: PostDao
    abstract val remoteKeysDao:RemoteKeysDao

    companion object{
        @Volatile
        private var INSTANCE: PostsDatabase? = null

        fun getDatabase(context: Context): PostsDatabase{

            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,
                    PostsDatabase::class.java,
                    "posts_db"
                ).fallbackToDestructiveMigration()
                    .build()
                Timber.d("Posts database created successfully")
                INSTANCE = instance
                instance
            }
        }
    }

}