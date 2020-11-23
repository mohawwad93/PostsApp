package com.firstcode.postsapp.ui.master

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.firstcode.postsapp.R
import com.firstcode.postsapp.databinding.ItemPostBinding
import com.firstcode.postsapp.repository.paging.model.Post

class PostAdapter(val clickListener: PostListener) : PagingDataAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post, clickListener)
    }

    fun getPost(position: Int) = getItem(position)

    class PostViewHolder private constructor(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(post: Post?, clickListener: PostListener) {
            val resources = itemView.resources
            if(post == null){
                binding.post = Post(-1, resources.getString(R.string.loading), resources.getString(R.string.loading))
                binding.clickListener = null
                binding.executePendingBindings()
            }else{
                binding.post = post
                binding.clickListener = clickListener
                binding.executePendingBindings()
            }

        }
        companion object {
            fun create(parent: ViewGroup): PostViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemPostBinding.inflate(inflater, parent, false)
                return PostViewHolder(binding)
            }
        }
    }

    companion object PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }

    }

    class PostListener(val clickListener: (post: Post) -> Unit){
        fun onClick(post: Post) = clickListener(post)
    }
}
