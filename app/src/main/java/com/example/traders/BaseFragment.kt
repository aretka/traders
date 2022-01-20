package com.example.traders

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
open class BaseFragment : Fragment() {

    @Inject
    lateinit var navControllerProvider: Provider<NavController>

    val navController: NavController by lazy {
        navControllerProvider.get()
    }

    override fun onStart() {
        super.onStart()
        Log.e(this.javaClass.toString(), "onStart")
    }

    override fun onStop() {
        super.onStop()
        Log.e(this.javaClass.toString(), "onStop")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(this.javaClass.toString(), "onCreate")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(this.javaClass.toString(), "onViewCreated")
    }

    override fun onPause() {
        super.onPause()
        Log.e(this.javaClass.toString(), "onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.e(this.javaClass.toString(), "onResume")
    }

    open fun showError(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }
}
