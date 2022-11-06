package com.ryanjames.composemobileordering.features.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.pager.ExperimentalPagerApi
import com.ryanjames.composemobileordering.core.BaseActivity
import com.ryanjames.composemobileordering.features.bottomnav.BottomNavActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@ExperimentalMaterialApi
@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribe()
    }

    private fun loginUser() {
        startActivity(Intent(this@LoginActivity, BottomNavActivity::class.java))
        finish()
    }

    private fun subscribe() {
        lifecycleScope.launch {
            viewModel.loginEvent.collect {
                it.handleEvent { loginEvent ->
                    when (loginEvent) {
                        is LoginEvent.LoginSuccess -> {
                            loginUser()
                        }
                        LoginEvent.AutoLogin -> {
                            loginUser()
                        }
                        else -> {}
                    }
                }

            }
        }

    }

    @Composable
    override fun SetContent() {
        LoginScreen(viewModel)
    }
}