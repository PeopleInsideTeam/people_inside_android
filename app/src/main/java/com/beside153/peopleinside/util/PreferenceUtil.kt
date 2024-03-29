package com.beside153.peopleinside.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PreferenceUtil @Inject constructor(
    @ApplicationContext context: Context
) {
    private val jwtTokenKey = "JWT_TOKEN"
    private val userIdKey = "USER_ID"
    private val userMbtiKey = "USER_MBTI"
    private val userNicknameKey = "USER_NICKNAME"
    private val userGenderKey = "USER_GENDER"
    private val userBirthKey = "USER_BIRTH"
    private val reportListKey = "REPORT_LIST"
    private val isMemberKey = "IS_MEMBER"
    private val emailKey = "EMAIL"
    private val recentSearchKey = "RECENT_SEARCH_LIST"

    private val prefs: SharedPreferences =
        context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)

    fun getReportList(): String {
        return prefs.getString(reportListKey, "").toString()
    }

    fun setReportList(str: String) {
        prefs.edit().putString(reportListKey, str).apply()
    }

    fun getUserId(): Int {
        return prefs.getInt(userIdKey, 0)
    }

    fun setUserId(i: Int) {
        prefs.edit().putInt(userIdKey, i).apply()
    }

    fun getJwtToken(): String {
        return prefs.getString(jwtTokenKey, "").toString()
    }

    fun setJwtToken(str: String) {
        prefs.edit().putString(jwtTokenKey, str).apply()
    }

    fun getMbti(): String {
        return prefs.getString(userMbtiKey, "").toString().uppercase()
    }

    fun setMbti(str: String) {
        prefs.edit().putString(userMbtiKey, str).apply()
    }

    fun getNickname(): String {
        return prefs.getString(userNicknameKey, "").toString()
    }

    fun setNickname(str: String) {
        prefs.edit().putString(userNicknameKey, str).apply()
    }

    fun getGender(): String {
        return prefs.getString(userGenderKey, "").toString()
    }

    fun setGender(str: String) {
        prefs.edit().putString(userGenderKey, str).apply()
    }

    fun getBirth(): String {
        return prefs.getString(userBirthKey, "").toString()
    }

    fun setBirth(str: String) {
        prefs.edit().putString(userBirthKey, str).apply()
    }

    fun getEmail(): String {
        return prefs.getString(emailKey, "").toString()
    }

    fun setEmail(str: String) {
        prefs.edit().putString(emailKey, str).apply()
    }

    fun getIsMember(): Boolean {
        return prefs.getBoolean(isMemberKey, false)
    }

    fun setIsMember(bln: Boolean) {
        prefs.edit().putBoolean(isMemberKey, bln).apply()
    }

    fun setRecentSearchList(list: List<String>) {
        prefs.edit().putStringSet(recentSearchKey, list.toSet()).apply()
    }

    fun getRecentSearchList(): List<String> {
        return prefs.getStringSet(recentSearchKey, setOf())?.toList() ?: listOf()
    }
}
