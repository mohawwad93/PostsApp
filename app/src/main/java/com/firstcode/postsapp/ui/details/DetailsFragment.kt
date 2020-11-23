package com.firstcode.postsapp.ui.details

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.firstcode.postsapp.R
import com.firstcode.postsapp.databinding.FragmentDetailsBinding


class DetailsFragment : Fragment() {

    private lateinit var args: DetailsFragmentArgs

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false)
        args = DetailsFragmentArgs.fromBundle(requireArguments())
        val viewModel : DetailsViewModel by viewModels{ DetailsViewModel.Factory(args.selectedPost) }
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

         if(item.itemId == R.id.action_update){
             this.findNavController().navigate(DetailsFragmentDirections.actionDetailsFragmentToAddPostFragment().setUpdatedPost(args.selectedPost))
             return true
         }
        return super.onOptionsItemSelected(item)
    }
}