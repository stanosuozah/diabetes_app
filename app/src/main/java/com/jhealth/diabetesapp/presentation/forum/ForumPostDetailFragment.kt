package com.jhealth.diabetesapp.presentation.forum

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jhealth.diabetesapp.R
import com.jhealth.diabetesapp.databinding.FragmentForumPostDetailBinding
import com.jhealth.diabetesapp.domain.model.Reply
import com.jhealth.diabetesapp.presentation.adapter.PostDetailReplyAdapter
import com.jhealth.diabetesapp.presentation.arch.DiabetesViewModel
import com.jhealth.diabetesapp.util.RequestState
import com.tutorial.messageme.data.utils.Resource
import kotlinx.coroutines.launch

class ForumPostDetailFragment : Fragment() {
    private var _binding: FragmentForumPostDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var commentAdapter : PostDetailReplyAdapter
    private val fUser = Firebase.auth.currentUser
    private val viewModel by activityViewModels<DiabetesViewModel>()
    private val args by navArgs<ForumPostDetailFragmentArgs>()

   /* override fun onPause() {
        super.onPause()
        viewModel.toggleRepliesState(Resource.Loading())
    }*/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentForumPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.toggleRepliesState(Resource.Loading())
        commentAdapter = fUser?.email?.let { PostDetailReplyAdapter(it) } ?:return
        val post = args.args
        binding.apply {
            commentsRecyclerView.adapter = commentAdapter
            postTitle.text = post.postTitle
            postContentText.text = post.postDescription
            authorText.text = post.email
            backBtn.setOnClickListener {
                findNavController().navigateUp()
            }

            sendBtn.setOnClickListener {
                addReply(post.postId)
            }
        }
        viewModel.addAllRepliesSnapshotListener(post.postId)
        observeAllReplies()
        commentAdapter.viewClick { mView, reply ->
            setUpPopUpMenu(post.postId,mView, reply)
        }
    }

    private fun addReply(postId:String) {
        val replyTxt = binding.replyEdt.text.toString().trim()

        if (replyTxt.isEmpty()) {
            return
        }

        fUser?.email?.let {
            val reply = Reply(
                System.currentTimeMillis().toInt(),
                replyDescription = replyTxt,
                replyTitle = "$postId-reply",
                email = it
            )
            viewModel.replyPost(postId, reply)
        } ?: Toast.makeText(requireContext(), "Email is not provided", Toast.LENGTH_SHORT).show()

        lifecycleScope.launch {
            viewModel.replyPostState.collect {
                when (it) {
                    is RequestState.Successful -> {
                        binding.replyEdt.text?.clear()
                    }
                    is RequestState.Failure -> {
                        Toast.makeText(
                            requireContext(),
                            "Error while replying: ${it.msg}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> Unit
                }
            }
        }

    }

    private fun observeAllReplies(){
        lifecycleScope.launch {
            viewModel.allRepliesState.collect {
                when (it) {
                    is Resource.Successful -> {
                        binding.commentsRecyclerView.isVisible = true
                        binding.emptyStateTv.isVisible = false
                        binding.progressBar.isVisible = false
                        binding.commentCountText.text = "${it.data?.size} comments"
                        commentAdapter.submitList(it.data?.reversed())
                    }
                    is Resource.Failure -> {
                        binding.commentsRecyclerView.isVisible = false
                        binding.emptyStateTv.isVisible = true
                        binding.emptyStateTv.text = it.msg
                        binding.progressBar.isVisible = false
                    }
                    is Resource.Loading -> {
                        binding.commentsRecyclerView.isVisible = false
                        binding.emptyStateTv.isVisible = false
                        binding.progressBar.isVisible = true

                    }
                }
            }
        }

    }
    private fun observeDeleteReply(){
        lifecycleScope.launch {
            viewModel.deleteReplyState.collect {
                when (it) {
                    is RequestState.Successful -> {
                        Toast.makeText(requireContext(),"Reply deleted",Toast.LENGTH_SHORT).show()
                    }
                    is RequestState.Failure -> {
                        Toast.makeText(requireContext(),"Some error occurred: ${it.msg}",Toast.LENGTH_SHORT).show()
                    }
                    is RequestState.Loading -> {
                        Toast.makeText(requireContext(),"Deleting...",Toast.LENGTH_SHORT).show()
                    }
                    else->Unit
                }
            }
        }

    }

    private fun setUpPopUpMenu(postId:String,view: View,reply: Reply){
            val popupMenu = PopupMenu(requireContext(), view)
            popupMenu.menuInflater.inflate(R.menu.pop_delete_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {item->
                when (item.itemId) {
                    R.id.delete_option -> {
                        fUser?.email?.let {
                            if (it.equals(reply.email,true)){
                                viewModel.deleteReplyToPost(postId, reply)
                                observeDeleteReply()
                                Log.d("dia_delete_reply", "setUpPopUpMenu: deleting reply...DELETED")
                                return@let
                            }
                            Log.d("dia_delete_reply", "setUpPopUpMenu: deleting reply...ERROR: Email not the same")
                        }?: Log.d("dia_delete_reply", "setUpPopUpMenu: NULLABLE state")
                        true
                    }
                    else -> super.onOptionsItemSelected(item)
                }
            }
            popupMenu.show()
    }


}