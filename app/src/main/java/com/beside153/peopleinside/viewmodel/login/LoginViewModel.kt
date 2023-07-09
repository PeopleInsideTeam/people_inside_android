package com.beside153.peopleinside.viewmodel.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.beside153.peopleinside.App
import com.beside153.peopleinside.base.BaseViewModel
import com.beside153.peopleinside.service.RetrofitClient
import com.beside153.peopleinside.service.SignUpService
import com.beside153.peopleinside.util.Event
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onSuccess
import kotlinx.coroutines.launch

class LoginViewModel(private val signUpService: SignUpService) : BaseViewModel() {

    private val _kakaoLoginClickEvent = MutableLiveData<Event<Unit>>()
    val kakaoLoginClickEvent: LiveData<Event<Unit>> get() = _kakaoLoginClickEvent

    private val _goToSignUpEvent = MutableLiveData<Event<String>>()
    val goToSignUpEvent: LiveData<Event<String>> get() = _goToSignUpEvent

    private val _loginSuccessEvent = MutableLiveData<Event<Unit>>()
    val loginSuccessEvent: LiveData<Event<Unit>> get() = _loginSuccessEvent

    private var authToken = ""

    fun setAuthToken(token: String) {
        authToken = token
    }

    fun onKakaoLoginClick() {
        _kakaoLoginClickEvent.value = Event(Unit)
    }

    fun peopleInsideLogin() {
        viewModelScope.launch(exceptionHandler) {
            val response = signUpService.postLoginKakao("Bearer $authToken")
            response.onSuccess {
                val jwtToken = this.response.body()?.jwtToken!!
                val user = this.response.body()?.user!!

                App.prefs.setString(App.prefs.jwtTokenKey, jwtToken)
                App.prefs.setUserId(user.userId)
                App.prefs.setNickname(user.nickname)
                App.prefs.setMbti(user.mbti)
                App.prefs.setBirth(user.birth)
                App.prefs.setGender(user.sex)

                _loginSuccessEvent.value = Event(Unit)
            }.onError {
                if (this.response.raw().message == "Unauthorized") {
                    _goToSignUpEvent.value = Event(authToken)
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val signUpService = RetrofitClient.signUpService
                return LoginViewModel(signUpService) as T
            }
        }
    }
}
