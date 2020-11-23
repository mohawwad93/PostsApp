package com.firstcode.postsapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.firstcode.postsapp.repository.paging.model.Post
import com.firstcode.postsapp.repository.paging.network.PostApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.net.HttpURLConnection


@RunWith(AndroidJUnit4::class)
class PostsNetworkTest {

    private lateinit var server: MockWebServer
    private lateinit var postApiService: PostApiService

    fun getJsonList(json: String): List<Post>{
        val moshi = Moshi.Builder().build()
        val type: Type = Types.newParameterizedType(
            MutableList::class.java,
            Post::class.java
        )
        val adapter = moshi.adapter<List<Post>>(type)
        return adapter.fromJson(json) as List<Post>
    }

    fun getJsonObject(json: String): Post{
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(Post::class.java)
        return adapter.fromJson(json) as Post
    }

    fun readFileMockResponse(path: String): String {
        val reader = InputStreamReader(this.javaClass.classLoader!!.getResourceAsStream(path))
        val content = reader.readText()
        reader.close()
        return content
    }

    @Before
    fun initServer() {
        server = MockWebServer()
        server.start()

        postApiService = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(server.url("/"))
            .build()
            .create(PostApiService::class.java)
    }

    @After
    @Throws(IOException::class)
    fun shutdownServer() {
        server.shutdown()
    }

    @Test
    @Throws(Exception::class)
    fun getPosts() = runBlocking {
        val jsonString =  readFileMockResponse("getPosts_page1_limit30.json")
        server.enqueue(
            MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(jsonString)
        )
        val response = postApiService.getPosts(1, 30)
        val actual  = getJsonList(jsonString)
        assertEquals(response, actual)
    }


    @Test
    @Throws(Exception::class)
    fun addPost() = runBlocking {
        val jsonString =  readFileMockResponse("add_post.json")
        server.enqueue(
            MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(jsonString)
        )
        val post = Post(0, "my title", "my url")
        val response = postApiService.addPost(post)
        val actual  = getJsonObject(jsonString)
        assertEquals(response, actual)
    }

    @Test
    @Throws(Exception::class)
    fun updatePost() = runBlocking {
        val post = Post(100, "title", "url")
        val jsonString =  Moshi.Builder().build().adapter(Post::class.java).toJson(post)
        server.enqueue(
            MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(jsonString)
        )
        val response = postApiService.updatePost(post.id, post)
        val actual  = getJsonObject(jsonString)
        assertEquals(response, actual)
    }

}