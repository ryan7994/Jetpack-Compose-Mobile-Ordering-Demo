package com.ryanjames.composemobileordering.features.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.ryanjames.composemobileordering.core.BaseActivity
import com.ryanjames.composemobileordering.features.bottomnav.BottomNavActivity
import com.ryanjames.composemobileordering.features.bottomnav.Screens
import com.ryanjames.composemobileordering.features.signup.SignUpScreen
import com.ryanjames.composemobileordering.features.signup.SignUpViewModel
import com.ryanjames.composemobileordering.network.model.Event
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@ExperimentalMaterialApi
@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

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
            loginViewModel.loginEvent.collect { event ->
                handleLoginEvent(event)
            }
        }

    }

    private fun handleLoginEvent(event: Event<LoginEvent>) {
        event.handle { loginEvent ->
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

    @Composable
    override fun SetContent() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = Screens.Login.route) {

            composable(route = Screens.Login.route) {
                LoginScreen(
                    loginViewModel = loginViewModel,
                    onClickSignUp = { navController.navigate(Screens.SignUp.route) })
            }

            composable(route = Screens.SignUp.route) {
                val signUpViewModel: SignUpViewModel = hiltViewModel()

                LaunchedEffect(null) {
                    signUpViewModel.autoLoginEvent.collect { event ->
                        handleLoginEvent(event)
                    }
                }

                SignUpScreen(signUpViewModel = signUpViewModel) {
                    navController.popBackStack()
                }
            }
        }


    }
}