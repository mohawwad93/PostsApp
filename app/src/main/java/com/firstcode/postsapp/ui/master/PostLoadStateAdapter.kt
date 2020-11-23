/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.firstcode.postsapp.ui.master

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.firstcode.postsapp.databinding.LoadStateFooterViewItemBinding

class PostLoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<PostLoadStateAdapter.PostLoadStateViewHolder>() {

    override fun onBindViewHolder(holder: PostLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): PostLoadStateViewHolder {
        return PostLoadStateViewHolder.create(parent, retry)
    }

    class PostLoadStateViewHolder(private val binding: LoadStateFooterViewItemBinding,
                                  retry: () -> Unit) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            binding.apply {
                if (loadState is LoadState.Error) {
                    errorMsg.text = loadState.error.localizedMessage
                }
                progressBar.isVisible = loadState is LoadState.Loading
                retryButton.isVisible = loadState !is LoadState.Loading
                errorMsg.isVisible = loadState !is LoadState.Loading
                executePendingBindings()
            }
        }

        companion object {
            fun create(parent: ViewGroup, retry: () -> Unit): PostLoadStateViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = LoadStateFooterViewItemBinding.inflate(inflater, parent, false)
                return PostLoadStateViewHolder(binding, retry)
            }
        }
    }
}



