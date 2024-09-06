package com.husseinrasti.app.mytonwallet

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.husseinrasti.app.component.theme.MyTonWalletContestTheme
import com.husseinrasti.app.mytonwallet.navigation.MyTonWalletNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyTonWalletContestTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    when (state) {
                        MainState.Idle -> {}
                        MainState.NavigateToAuth ->
                            MyTonWalletNavHost(isAuth = true)

                        MainState.NavigateToCreateWallet ->
                            MyTonWalletNavHost(isAuth = false)
                    }
                }
            }
        }
    }
}