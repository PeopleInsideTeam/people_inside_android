package com.beside153.peopleinside.view.community

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beside153.peopleinside.App
import com.beside153.peopleinside.R
import com.beside153.peopleinside.base.BaseFragment
import com.beside153.peopleinside.databinding.FragmentCommunityBinding
import com.beside153.peopleinside.model.community.post.CommunityPostModel
import com.beside153.peopleinside.util.EventObserver
import com.beside153.peopleinside.util.setOpenActivityAnimation
import com.beside153.peopleinside.util.showToast
import com.beside153.peopleinside.view.login.nonmember.NonMemberLoginActivity
import com.beside153.peopleinside.viewmodel.community.CommunityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommunityFragment : BaseFragment() {
    private lateinit var binding: FragmentCommunityBinding
    private val communityViewModel: CommunityViewModel by viewModels()
    private val postListAdapter = CommunityPostListAdapter(::onPostItemClick)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_community, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewModel = communityViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        binding.communityPostRecyclerView.apply {
            adapter = postListAdapter
            layoutManager = LinearLayoutManager(requireActivity())
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val lastVisibleItemPosition =
                        (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                    val itemTotalCount = recyclerView.adapter!!.itemCount - 1

                    if (!recyclerView.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount) {
                        communityViewModel.loadMorePostList()
                    }
                }
            })
        }

        communityViewModel.postList.observe(viewLifecycleOwner) { list ->
            postListAdapter.submitList(list)
        }

        communityViewModel.error.observe(
            viewLifecycleOwner,
            EventObserver {
                showErrorDialog(it) { communityViewModel.initPostList() }
            }
        )

        communityViewModel.searchBarClickEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                startActivity(CommunitySearchActivity.newIntent(requireActivity()))
            }
        )

        communityViewModel.writePostClickEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                if (App.prefs.getIsMember()) {
                    activityLauncher.launch(CreatePostActivity.newIntent(requireActivity()))
                    requireActivity().setOpenActivityAnimation()
                    return@EventObserver
                }
                startActivity(NonMemberLoginActivity.newIntent(requireActivity()))
                requireActivity().setOpenActivityAnimation()
            }
        )

        communityViewModel.postItemClickEvent.observe(
            viewLifecycleOwner,
            EventObserver { postId ->
                activityLauncher.launch(PostDetailActivity.newIntent(requireActivity(), postId))
                requireActivity().setOpenActivityAnimation()
            }
        )
    }

    override fun onResume() {
        super.onResume()
        communityViewModel.initPostList()
    }

    private fun onPostItemClick(item: CommunityPostModel) {
        communityViewModel.onPostItemClick(item)
    }

    private val activityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == R.string.complete_create_post) {
                communityViewModel.initPostList()
                return@registerForActivityResult
            }
            if (result.resultCode == R.string.delete_post_dialog_title) {
                communityViewModel.initPostList()
                Handler(Looper.getMainLooper()).postDelayed({
                    requireActivity().showToast(R.string.complete_delete_post)
                }, TOAST_DELAY)
            }
        }

    companion object {
        private const val TOAST_DELAY = 500L
        private const val PROGRESSBAR_VISIBLE_DELAY = 1000L
    }
}
