package com.firstcode.postsapp.repository.paging.network

import com.firstcode.postsapp.repository.paging.model.Post
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

interface PostApiService{
    @GET("photos")
   suspend fun getPosts(@Query("_page") page: Int,
                        @Query("_limit") limit: Int): List<Post>

    @POST("photos")
    suspend fun addPost(@Body post: Post): Post?

    @DELETE("photos/{id}")
    suspend fun deletePost(@Path("id") postId: Int)

    @PUT("photos/{id}")
    suspend fun updatePost(@Path("id") postId: Int, @Body post: Post): Post?

}

object NetworkService{

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    val postApiService: PostApiService by lazy { retrofit.create(PostApiService::class.java)}
}
