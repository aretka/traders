package com.example.traders.presentation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.traders.R
import com.example.traders.network.webSocket.BinanceWSClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var webSocketClient: BinanceWSClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpBottomNavigation()
        webSocketClient.startConnection()
    }

    private fun setUpBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {
                R.id.cryptoItemFragment -> {
                    bottomNavigationView.menu.forEach { it.isEnabled = false }
                    bottomNavigationView.isFocusable = false
                    bottomNavigationView.animate()
                        .translationX(-bottomNavigationView.width.toFloat())
                        .setDuration(300)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                super.onAnimationEnd(animation)
                                bottomNavigationView.setVisibility(View.GONE)
                            }
                        })
                }
                else -> {
                    bottomNavigationView.menu.forEach { it.isEnabled = true }
                    bottomNavigationView.isActivated = true
                    bottomNavigationView.animate()
                        .translationX(0F)
                        .setDuration(300)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationStart(animation: Animator?) {
                                super.onAnimationEnd(animation)
                                bottomNavigationView.setVisibility(View.VISIBLE)
                            }
                        })
                }
            }

        }
    }

    override fun onStop() {
        super.onStop()
        // stops client only if it is open
        webSocketClient.stopConnection()
    }

    override fun onResume() {
        super.onResume()
        // restarts client only if it is closed
        webSocketClient.restartConnection()
    }
}
