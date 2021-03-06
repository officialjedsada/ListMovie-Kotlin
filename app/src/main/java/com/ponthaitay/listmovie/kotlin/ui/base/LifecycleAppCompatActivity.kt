package com.ponthaitay.listmovie.kotlin.ui.base

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.support.v7.app.AppCompatActivity

open class LifecycleAppCompatActivity : AppCompatActivity(), LifecycleRegistryOwner {
    @Suppress("LeakingThis")
    private val mRegister = LifecycleRegistry(this)

    override fun getLifecycle(): LifecycleRegistry = mRegister
}