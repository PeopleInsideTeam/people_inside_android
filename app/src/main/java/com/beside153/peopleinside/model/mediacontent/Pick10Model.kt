package com.beside153.peopleinside.model.mediacontent

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Pick10Model(
    @SerialName("content_id") val contentId: Int,
    @SerialName("title") val title: String,
    @SerialName("overview") val overview: String,
    @SerialName("poster_path") val posterPath: String,
    @SerialName("total_rating") val totalRating: Double = 0.0,
    @SerialName("mbti_rating") val mbtiRating: Double = 0.0,
    @SerialName("bookmarked") val bookmarked: Boolean,
    @SerialName("top_like_review") val topLikeReview: Pick10TopLikeReview? = null
) : Parcelable

@Parcelize
@Serializable
data class Pick10TopLikeReview(
    @SerialName("content_id") val contentId: Int,
    @SerialName("review_id") val reviewId: Int,
    @SerialName("content") val content: String,
    @SerialName("like_count") val likeCount: Int,
    @SerialName("writer") val writer: Writer
) : Parcelable

@Parcelize
@Serializable
data class Writer(
    @SerialName("user_id") val userId: Int,
    @SerialName("nickname") val nickname: String,
    @SerialName("mbti") val mbti: String,
    @SerialName("birth") val birth: String,
    @SerialName("sex") val sex: String
) : Parcelable
