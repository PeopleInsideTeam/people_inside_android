package com.beside153.peopleinside.viewmodel.community

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.beside153.peopleinside.base.BaseViewModel
import com.beside153.peopleinside.model.community.post.CommunityPostModel
import com.beside153.peopleinside.service.community.CommunityPostService
import com.beside153.peopleinside.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val communityPostService: CommunityPostService
) : BaseViewModel() {
    private val _searchBarClickEvent = MutableLiveData<Event<Unit>>()
    val searchBarClickEvent: LiveData<Event<Unit>> = _searchBarClickEvent

    private val _writePostClickEvent = MutableLiveData<Event<Unit>>()
    val writePostClickEvent: LiveData<Event<Unit>> = _writePostClickEvent

    private val _postList = MutableLiveData<List<CommunityPostModel>>()
    val postList: LiveData<List<CommunityPostModel>> get() = _postList

    private var page = 1

    fun initData() {
        viewModelScope.launch(exceptionHandler) {
            _postList.value = communityPostService.getPostList(page)
        }
    }

    fun onCommunitySearchBarClick() {
        _searchBarClickEvent.value = Event(Unit)
    }

    fun onWritePostClick() {
        _writePostClickEvent.value = Event(Unit)
    }
}
