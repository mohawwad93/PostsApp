package com.firstcode.postsapp.ui.master

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firstcode.postsapp.R
import com.firstcode.postsapp.databinding.FragmentMasterBinding
import com.firstcode.postsapp.repository.paging.model.Post
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class MasterFragment : Fragment() {

    private lateinit var adapter: PostAdapter
    private lateinit var viewModel : MasterViewModel
    private lateinit var binding: FragmentMasterBinding

    @OptIn(InternalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_master, container, false)

        val application = requireNotNull(activity).application
        viewModel = ViewModelProviders.of(this, MasterViewModel.Factory(application))
                     .get(MasterViewModel::class.java)
        binding.lifecycleOwner = this
        initAdapter()
        setupRecyclerview()
        setupLiveDataListeners()
        setupClickListeners()
        // Collect posts
        lifecycleScope.launch {
            viewModel.posts.collectLatest {adapter.submitData(it) }
        }
        return binding.root
    }

    private fun setupClickListeners() {
        binding.addFab.setOnClickListener{
            this.findNavController().navigate(MasterFragmentDirections.actionListFragmentToAddPostFragment())
        }
        binding.retryButton.setOnClickListener { adapter.retry() }
    }

    private fun initAdapter() {
        adapter = PostAdapter(PostAdapter.PostListener { viewModel.displayPostDetails(it) })
        lifecycleScope.launch {
            adapter.loadStateFlow
                // Only emit when REFRESH LoadState for RemoteMediator changes.
                .distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .collectLatest { binding.recyclerView.scrollToPosition(0) }
        }

        adapter.addLoadStateListener { loadState ->
            // initial load or refresh
            binding.apply {
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                retryButton.isVisible = loadState.source.refresh is LoadState.Error
            }

            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(context, "\uD83D\uDE28 Wooops ${it.error}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun setupLiveDataListeners() {
        // navigate to selected post
        viewModel.navigateToSelectedPost.observe(viewLifecycleOwner, {
            it?.let {
                this.findNavController().navigate(
                    MasterFragmentDirections.actionListFragmentToDetailsFragment(it)
                )
                viewModel.displayPostDetailsComplete()
            }
        })
        // Observe deleted posts
        viewModel.deletedPost.observe(viewLifecycleOwner, {
            if (it) {
                Toast.makeText(activity, R.string.delete_post_success, Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(activity, R.string.delete_post_success, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerview() {
        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        binding.apply {
            recyclerView.addItemDecoration(decoration)
            mItemTouchHelper.attachToRecyclerView(recyclerView)
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                header = PostLoadStateAdapter { adapter.retry() },
                footer = PostLoadStateAdapter { adapter.retry() }
            )
            recyclerView.scrollToPosition(viewModel.scrollPosition)
        }
    }

    val mItemTouchHelper = ItemTouchHelper(object :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.bindingAdapterPosition
            val post: Post? = adapter.getPost(position)
            post?.let {
                MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(getString(R.string.delete_post_title))
                    .setMessage(getString(R.string.delete_post_body, post.title))
                    .setNeutralButton(getString(R.string.cancel)) {
                            dialog: DialogInterface, _: Int ->
                        adapter.notifyItemChanged(position)
                        dialog.dismiss()
                    }
                    .setPositiveButton(getString(R.string.ok)) {
                            _: DialogInterface?, _: Int -> viewModel.deletePost(post)
                    }.show()

            }

        }
    })

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.scrollPosition = (binding.recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
    }
}