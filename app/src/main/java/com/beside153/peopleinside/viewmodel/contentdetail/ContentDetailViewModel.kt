package com.beside153.peopleinside.viewmodel.contentdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.beside153.peopleinside.App
import com.beside153.peopleinside.base.BaseViewModel
import com.beside153.peopleinside.model.contentdetail.ContentCommentModel
import com.beside153.peopleinside.model.contentdetail.ContentDetailModel
import com.beside153.peopleinside.model.contentdetail.ContentRatingModel
import com.beside153.peopleinside.model.contentdetail.ContentRatingRequest
import com.beside153.peopleinside.model.contentdetail.ContentReviewModel
import com.beside153.peopleinside.service.BookmarkService
import com.beside153.peopleinside.service.ContentDetailService
import com.beside153.peopleinside.service.LikeToggleService
import com.beside153.peopleinside.service.ReportService
import com.beside153.peopleinside.service.RetrofitClient
import com.beside153.peopleinside.util.Event
import com.beside153.peopleinside.util.roundToHalf
import com.beside153.peopleinside.view.contentdetail.ContentDetailScreenAdapter.ContentDetailScreenModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Suppress("TooManyFunctions")
class ContentDetailViewModel(
    private val contentDetailService: ContentDetailService,
    private val bookmarkService: BookmarkService,
    private val likeToggleService: LikeToggleService,
    private val reportService: ReportService
) : BaseViewModel() {

    private val _contentDetailItem = MutableLiveData<ContentDetailModel>()
    val contentDetailItem: LiveData<ContentDetailModel> get() = _contentDetailItem

    private val contentRatingItem = MutableLiveData<ContentRatingModel>()
    private val bookmarked = MutableLiveData(false)
    private val writerReviewItem = MutableLiveData<ContentReviewModel>()
    private val commentList = MutableLiveData<List<ContentCommentModel>>()

    private val _screenList = MutableLiveData<List<ContentDetailScreenModel>>()
    val screenList: LiveData<List<ContentDetailScreenModel>> get() = _screenList

    private val _scrollEvent = MutableLiveData<Event<Unit>>()
    val scrollEvent: LiveData<Event<Unit>> get() = _scrollEvent

    private val _createReviewClickEvent = MutableLiveData<Event<Pair<Int, String>>>()
    val createReviewClickEvent: LiveData<Event<Pair<Int, String>>> get() = _createReviewClickEvent

    private val _verticalDotsClickEvent = MutableLiveData<Event<Unit>>()
    val verticalDotsClickEvent: LiveData<Event<Unit>> get() = _verticalDotsClickEvent

    private val _reportSuccessEvent = MutableLiveData<Event<Unit>>()
    val reportSuccessEvent: LiveData<Event<Unit>> get() = _reportSuccessEvent

    private val writerHasReview = MutableLiveData(false)
    private var commentIdForReport = 0

    private var contentId = 0
    private var currentRating = 0f
    private var currentRatingId = 0
    private var page = 1

    fun setContentId(id: Int) {
        contentId = id
    }

    fun loadMoreCommentList() {
        viewModelScope.launch(exceptionHandler) {
            val newCommentList = contentDetailService.getContentReviewList(contentId, ++page)
            commentList.value = commentList.value?.plus(newCommentList)

            @Suppress("SpreadOperator")
            _screenList.value = _screenList.value?.plus(
                listOf(
                    *newCommentList.map { ContentDetailScreenModel.ContentCommentItem(it) }.toTypedArray()
                )
            )
        }
    }

    fun onVerticalDotsClick(item: ContentCommentModel) {
        _verticalDotsClickEvent.value = Event(Unit)
        commentIdForReport = item.reviewId
    }

    fun onCommentLikeClick(item: ContentCommentModel) {
        viewModelScope.launch(exceptionHandler) {
            val statusItem = likeToggleService.postLikeToggle(contentId, item.reviewId)

            val updatedList: List<ContentCommentModel>?
            if (statusItem.toggleStatus == CREATED) {
                updatedList = commentList.value?.map {
                    if (item == it) {
                        it.copy(likeCount = it.likeCount + 1)
                    } else {
                        it
                    }
                }
            } else {
                updatedList = commentList.value?.map {
                    if (item == it) {
                        it.copy(likeCount = it.likeCount - 1)
                    } else {
                        it
                    }
                }
            }

            commentList.value = updatedList ?: emptyList()
            _screenList.value = screenList()
        }
    }

    fun reportComment(reportId: Int) {
        viewModelScope.launch(exceptionHandler) {
            reportService.postReport(contentId, commentIdForReport, reportId)
            _reportSuccessEvent.value = Event(Unit)
        }
    }

    fun initAllData(didClickComment: Boolean) {
        viewModelScope.launch(exceptionHandler) {
            initRating(contentId).join()
            initWriterReview(contentId).join()
            val contentDetailItemDeferred = async { contentDetailService.getContentDetail(contentId) }
            val bookmarkStatusDeferred = async { bookmarkService.getBookmarkStatus(contentId) }
            val commentListDeferred = async { contentDetailService.getContentReviewList(contentId, page) }
            val postViewLogDeferred = async { contentDetailService.postViewLog(contentId, ENTER) }

            _contentDetailItem.value = contentDetailItemDeferred.await()
            bookmarked.value = bookmarkStatusDeferred.await()
            commentList.value = commentListDeferred.await()
            postViewLogDeferred.await()

            _screenList.value = screenList()
            if (didClickComment) _scrollEvent.value = Event(Unit)
        }
    }

    fun postViewLogStay() {
        viewModelScope.launch(exceptionHandler) {
            contentDetailService.postViewLog(contentId, STAY)
        }
    }

    private suspend fun initWriterReview(contentId: Int): Job {
        val exceptionHandler = CoroutineExceptionHandler { _, t ->
            when (t) {
                is HttpException -> {
                    writerHasReview.value = false
                }

                // else -> 처리 필요
            }
        }

        return viewModelScope.launch(exceptionHandler) {
            val writerReviewDeferred = async { contentDetailService.getWriterReview(contentId, App.prefs.getUserId()) }
            writerReviewItem.value = writerReviewDeferred.await()
            writerHasReview.value = true
        }
    }

    private suspend fun initRating(contentId: Int): Job {
        val exceptionHandler = CoroutineExceptionHandler { _, t ->
            when (t) {
                is HttpException -> {
                    contentRatingItem.value = ContentRatingModel(contentId, 0, 0f)
                    currentRating = 0f
                    currentRatingId = 0
                }

                // else -> 처리 필요
            }
        }

        return viewModelScope.launch(exceptionHandler) {
            val contentRatingItemDeferred =
                async { contentDetailService.getContentRating(contentId, App.prefs.getUserId()) }
            contentRatingItem.value = contentRatingItemDeferred.await()
            currentRating = contentRatingItem.value?.rating ?: 0f
            currentRatingId = contentRatingItem.value?.ratingId ?: 0
        }
    }

    fun onRatingChanged(rating: Float) {
        viewModelScope.launch(exceptionHandler) {
            if (currentRating.roundToHalf() == rating) return@launch

            if (currentRating <= 0) {
                contentRatingItem.value =
                    contentDetailService.postContentRating(contentId, ContentRatingRequest(rating))
                currentRating = rating
                currentRatingId = contentRatingItem.value?.ratingId ?: 0
                return@launch
            }
            val currentRatingHasValue = 0 < currentRating && currentRating <= MAX_RATING
            if (currentRatingHasValue && (0 < rating && rating <= MAX_RATING)) {
                contentDetailService.putContentRating(contentId, ContentRatingRequest(rating))
                currentRating = rating
                return@launch
            }
            if (currentRatingHasValue && rating == 0f) {
                contentDetailService.deleteContentRating(contentId, currentRatingId)
                currentRating = rating
            }
        }
    }

    fun onBookmarkClick() {
        bookmarked.value = bookmarked.value != true
        _screenList.value = screenList()
        viewModelScope.launch(exceptionHandler) {
            bookmarkService.postBookmarkStatus(contentId)
        }
    }

    @Suppress("SpreadOperator")
    private fun screenList(): List<ContentDetailScreenModel> {
        val commentAreaList = if ((commentList.value ?: emptyList()).isEmpty()) {
            listOf(ContentDetailScreenModel.NoCommentView)
        } else {
            listOf(
                *commentList.value?.map { ContentDetailScreenModel.ContentCommentItem(it) }?.toTypedArray()
                    ?: emptyArray()
            )
        }

        return listOf(
            ContentDetailScreenModel.PosterView(_contentDetailItem.value!!),
            ContentDetailScreenModel.ReviewView(contentRatingItem.value!!, bookmarked.value!!, writerHasReview.value!!),
            ContentDetailScreenModel.InfoView(_contentDetailItem.value!!),
            ContentDetailScreenModel.CommentsView
        ) + commentAreaList
    }

    fun onCreateReviewClick() {
        if (writerHasReview.value == false) {
            _createReviewClickEvent.value = Event(Pair(contentId, ""))
        } else {
            _createReviewClickEvent.value = Event(Pair(contentId, writerReviewItem.value?.content ?: ""))
        }
    }

    companion object {
        private const val MAX_RATING = 5
        private const val ENTER = "enter"
        private const val STAY = "stay"
        private const val CREATED = "created"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val contentDetailService = RetrofitClient.contentDetailService
                val bookmarkService = RetrofitClient.bookmarkService
                val likeToggleService = RetrofitClient.likeToggleService
                val reportServie = RetrofitClient.reportService
                return ContentDetailViewModel(
                    contentDetailService,
                    bookmarkService,
                    likeToggleService,
                    reportServie
                ) as T
            }
        }
    }
}
