package com.beside153.peopleinside.view.mypage.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.beside153.peopleinside.App
import com.beside153.peopleinside.R
import com.beside153.peopleinside.base.BaseActivity
import com.beside153.peopleinside.common.extension.eventObserve
import com.beside153.peopleinside.databinding.ActivitySettingBinding
import com.beside153.peopleinside.util.addBackPressedAnimation
import com.beside153.peopleinside.util.setCloseActivityAnimation
import com.beside153.peopleinside.util.setOpenActivityAnimation
import com.beside153.peopleinside.view.dialog.TwoButtonsDialog
import com.beside153.peopleinside.view.login.LoginActivity
import com.beside153.peopleinside.viewmodel.mypage.setting.SettingEvent
import com.beside153.peopleinside.viewmodel.mypage.setting.SettingViewModel
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SettingActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingBinding
    private val settingViewModel: SettingViewModel by viewModels()
    private lateinit var kakaoApi: UserApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)

        binding.apply {
            viewModel = settingViewModel
            lifecycleOwner = this@SettingActivity
        }

        addBackPressedAnimation()

        KakaoSdk.init(this, getString(R.string.kakao_native_app_key))
        kakaoApi = UserApiClient.instance

        settingViewModel.setAppVersionName()
        initObserver()
    }

    private fun initObserver() {
        settingViewModel.backButtonClickEvent.eventObserve(this) {
            finish()
            setCloseActivityAnimation()
        }

        settingViewModel.error.eventObserve(this) {
            showErrorDialog(it) { settingViewModel.setAppVersionName() }
        }

        settingViewModel.settingEvent.eventObserve(this) {
            when (it) {
                SettingEvent.TermsClick -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://peopleinside.notion.site/ac6615474dcb40749f59ab453527a602?")
                    startActivity(intent)
                }

                SettingEvent.PrivacyPolicyClick -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://peopleinside.notion.site/1e270175949d4942b2e025d35107362e?pvs=4")
                    startActivity(intent)
                }

                SettingEvent.UpdateClick -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.beside153.peopleinside")
                    startActivity(intent)
                }

                SettingEvent.LogoutClick -> showLogoutDialog()

                SettingEvent.DeleteAccountClick -> {
                    startActivity(DeleteAccountActivity.newIntent(this))
                    setOpenActivityAnimation()
                }
            }
        }
    }

    private fun showLogoutDialog() {
        val logoutDialog = TwoButtonsDialog.TwoButtonsDialogBuilder()
            .setTitle(R.string.logout)
            .setDescriptionRes(R.string.you_may_use_after_login)
            .setButtonClickListener(object : TwoButtonsDialog.TwoButtonsDialogListener {
                override fun onClickPositiveButton() {
                    logoutKakaoAccount()
                    App.prefs.setUserId(0)
                    App.prefs.setNickname("")
                    startActivity(
                        LoginActivity.newIntent(this@SettingActivity).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    )
                }

                override fun onClickNegativeButton() = Unit
            }).create()
        logoutDialog.show(supportFragmentManager, logoutDialog.tag)
    }

    private fun logoutKakaoAccount() {
        kakaoApi.logout { error ->
            if (error != null) {
                Timber.e(error, "로그아웃 실패. SDK에서 토큰 삭제됨")
            } else {
                Timber.i("로그아웃 성공. SDK에서 토큰 삭제됨")
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, SettingActivity::class.java)
        }
    }
}
