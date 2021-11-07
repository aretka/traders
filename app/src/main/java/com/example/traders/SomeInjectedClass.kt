package com.example.traders

import javax.inject.Inject

class SomeInjectedClass @Inject constructor() : SomeInjectedInterface {

    override fun doSomething() {
        println("something done")
    }
}
