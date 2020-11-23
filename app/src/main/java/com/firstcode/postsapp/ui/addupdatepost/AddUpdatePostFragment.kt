package com.firstcode.postsapp.ui.addupdatepost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.firstcode.postsapp.R
import com.firstcode.postsapp.databinding.FragmentAddUpdatePostBinding
import com.firstcode.postsapp.repository.paging.model.Post
import com.firstcode.postsapp.ui.MainActivity

class AddUpdatePostFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentAddUpdatePostBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_update_post, container, false)
        val application = requireActivity().application
        val viewModel : AddUpdatePostViewModel by viewModels{ AddUpdatePostViewModel.Factory(application) }
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        val args = AddUpdatePostFragmentArgs.fromBundle(requireArguments())
        val addMode = args.updatedPost == null

        if(!addMode){
            (requireActivity() as MainActivity).supportActionBar?.title = "Update Post"
            binding.apply {
                titleEdittext.setText(args.updatedPost?.title)
                imgSrcUrlEdittext.setText(args.updatedPost?.url)
                addBtn.text = getString(R.string.update)
            }
        }

        binding.addBtn.setOnClickListener {
            binding.apply {
                if(titleEdittext.text.isNullOrEmpty() || imgSrcUrlEdittext.text.isNullOrEmpty()){
                    Toast.makeText(activity, getString(R.string.post_validation_msg), Toast.LENGTH_SHORT).show()
                }else{
                   if(addMode){
                       val newPost = Post(0, titleEdittext.text.toString(), imgSrcUrlEdittext.text.toString())
                       viewModel.displayMasterFragment(newPost)
                   }else{
                       val post = Post(args.updatedPost!!.id, titleEdittext.text.toString(), imgSrcUrlEdittext.text.toString())
                       viewModel.displayDetailsFragment(post)
                   }
                }
            }
        }


        viewModel.post.observe(viewLifecycleOwner, {
            if(it != null){
                if(addMode){
                    this.findNavController().navigate(AddUpdatePostFragmentDirections.actionAddUpdatePostFragmentToMasterFragment())
                }else{
                    this.findNavController().navigate(AddUpdatePostFragmentDirections.actionAddUpdatePostFragmentToDetailsFragment(it))
                }
                viewModel.navigateAwayCompleted(viewLifecycleOwner)
            }else{
                if(addMode){
                    Toast.makeText(activity, getString(R.string.add_post_fail), Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(activity, getString(R.string.update_post_fail), Toast.LENGTH_LONG).show()
                }
            }
        })


        return binding.root
    }

}