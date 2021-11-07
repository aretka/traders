package com.example.traders

import javax.inject.Inject

//Example class
class SomeInjectedClass @Inject constructor() : SomeInjectedInterface {

    override fun doSomething() {
        println("something done")
    }
}
