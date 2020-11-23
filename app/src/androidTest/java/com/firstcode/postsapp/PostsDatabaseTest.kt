package com.firstcode.postsapp

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.firstcode.postsapp.repository.paging.model.Post
import com.firstcode.postsapp.repository.paging.database.PostDao
import com.firstcode.postsapp.repository.paging.database.PostsDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PostsDatabaseTest {

    private lateinit var postDao: PostDao
    private lateinit var db: PostsDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, PostsDatabase::class.java)
            .build()
        postDao = db.postDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetPost() = runBlocking {
        val postList = listOf(Post(100, "title1", "url1"),
            Post(101, "title2", "url2"),
            Post(102, "title3", "url3"))
        postDao.insertAll(*postList.toTypedArray())
        val dbPostList = postDao.getPostsTest()
        assertEquals(dbPostList, postList )
    }

    @Test
    @Throws(Exception::class)
    fun deletePost() = runBlocking {
        val postList = mutableListOf(Post(100, "title1", "url1"),
            Post(101, "title2", "url2"),
            Post(102, "title3", "url3"))
        postDao.insertAll(*postList.toTypedArray())
        postDao.delete(postList[0])
        postDao.delete(postList[1])
        postList.removeAt(0)
        postList.removeAt(1)
        val dbPostList = postDao.getPostsTest()
        assertEquals(dbPostList.size, postList.size)
    }

    @Test
    @Throws(Exception::class)
    fun updatePost() = runBlocking {
        var post = Post(100, "title", "url")
        postDao.insertAll(post)
        post = Post(100, "new title", "new url")
        postDao.update(post)
        val dbPost = postDao.getPostsTest()
        assertEquals(dbPost[0].title, post.title)
        assertEquals(dbPost[0].url, post.url )
    }

    @Test
    @Throws(Exception::class)
    fun clearDb() = runBlocking {
        val postList = mutableListOf(Post(100, "title1", "url1"),
            Post(101, "title2", "url2"),
            Post(102, "title3", "url3"))
        postDao.insertAll(*postList.toTypedArray())
        postDao.clear()
        postList.clear()
        val dbPostList = postDao.getPostsTest()
        assertEquals(dbPostList.size, postList.size)
    }



}