package com.firstcode.postsapp.ui.details

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter

import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions

import com.firstcode.postsapp.R
import com.firstcode.postsapp.repository.paging.model.Post


@BindingAdapter("postTitle")
fun TextView.setPostTitle(post: Post?){
    post?.let { text = post.title }
}

@BindingAdapter("postImageUrl")
fun ImageView.setPostImage(post: Post?){

    post?.let {
        val imgUrl = post.url
        imgUrl.let {
            val imgUri = it.toUri().buildUpon().scheme("https").build()
            // there is an issue with placeholder.com, you must add user-agent header
            val glideUrl = GlideUrl(imgUri.toString(),
                LazyHeaders.Builder().addHeader("User-Agent", "android").build())
            Glide.with(context)
                .load(glideUrl)
                .apply(
                    RequestOptions().placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image)
                )
                .into(this)
        }
    }


}
