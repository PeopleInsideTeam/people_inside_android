package com.beside153.peopleinside.view.mypage.contents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.beside153.peopleinside.databinding.ItemMypageBookmarkContentBinding
import com.beside153.peopleinside.model.mycontent.BookmarkedContentModel

class BookmarkedContentListAdapter(private val onBookmarkClick: (item: BookmarkedContentModel) -> Unit) :
    ListAdapter<BookmarkedContentModel, BookmarkedContentListAdapter.ContentItemViewHolder>(
        BookmarkContentItemDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentItemViewHolder {
        val binding = ItemMypageBookmarkContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.bookmarkImageView.setOnClickListener {
            if (binding.item != null) {
                onBookmarkClick(binding.item!!)
            }
        }
        return ContentItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContentItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ContentItemViewHolder(private val binding: ItemMypageBookmarkContentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BookmarkedContentModel) {
            binding.item = item
            binding.executePendingBindings()
        }
    }

    private class BookmarkContentItemDiffCallback : DiffUtil.ItemCallback<BookmarkedContentModel>() {
        override fun areItemsTheSame(oldItem: BookmarkedContentModel, newItem: BookmarkedContentModel): Boolean {
            return oldItem.contentId == newItem.contentId
        }

        override fun areContentsTheSame(oldItem: BookmarkedContentModel, newItem: BookmarkedContentModel): Boolean {
            return oldItem == newItem
        }
    }
}
