package com.beside153.peopleinside.view.mypage

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.beside153.peopleinside.App
import com.beside153.peopleinside.R
import com.beside153.peopleinside.databinding.ActivitySettingBinding
import com.beside153.peopleinside.util.EventObserver
import com.beside153.peopleinside.util.addBackPressedCallback
import com.beside153.peopleinside.util.setCloseActivityAnimation
import com.beside153.peopleinside.util.setOpenActivityAnimation
import com.beside153.peopleinside.view.dialog.TwoButtonsDialog
import com.beside153.peopleinside.view.login.LoginActivity
import com.beside153.peopleinside.viewmodel.mypage.SettingViewModel

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    private val settingViewModel: SettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)

        binding.apply {
            viewModel = settingViewModel
            lifecycleOwner = this@SettingActivity
        }

        addBackPressedCallback()

        settingViewModel.backButtonClickEvent.observe(
            this,
            EventObserver {
                finish()
                setCloseActivityAnimation()
            }
        )

        settingViewModel.termsClickEvent.observe(
            this,
            EventObserver {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://peopleinside.notion.site/ac6615474dcb40749f59ab453527a602?")
                startActivity(intent)
            }
        )

        settingViewModel.privacyPolicyClickEvent.observe(
            this,
            EventObserver {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://peopleinside.notion.site/1e270175949d4942b2e025d35107362e?pvs=4")
                startActivity(intent)
            }
        )

        settingViewModel.updateClickEvent.observe(
            this,
            EventObserver {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.beside153.peopleinside")
                startActivity(intent)
            }
        )

        settingViewModel.logoutClickEvent.observe(
            this,
            EventObserver {
                val logoutDialog = TwoButtonsDialog.TwoButtonsDialogBuilder()
                    .setTitle(R.string.logout)
                    .setDescription(R.string.you_may_use_after_login)
                    .setButtonClickListener(object : TwoButtonsDialog.TwoButtonsDialogListener {
                        override fun onClickPositiveButton() {
                            App.prefs.setNickname("")
                            startActivity(LoginActivity.newIntent(this@SettingActivity))
                            finishAffinity()
                        }

                        override fun onClickNegativeButton() {
                            // 필요없음
                        }
                    }).create()
                logoutDialog.show(supportFragmentManager, logoutDialog.tag)
            }
        )

        settingViewModel.deleteAccountEvent.observe(
            this,
            EventObserver {
                startActivity(DeleteAccountActivity.newIntent(this))
                setOpenActivityAnimation()
            }
        )
    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, SettingActivity::class.java)
        }
    }
}