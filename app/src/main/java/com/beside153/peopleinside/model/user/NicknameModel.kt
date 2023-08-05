package com.beside153.peopleinside.model.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class NicknameModel(
    @SerialName("nickname") val nickname: String
) : Parcelable
