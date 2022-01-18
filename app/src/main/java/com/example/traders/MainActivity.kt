package com.example.traders

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.traders.webSocket.BinanceWSClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

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
        webSocketClient.stopConnection()
    }

    override fun onResume() {
        super.onResume()
//        webSocketClient.restartConnection()
    }
}
