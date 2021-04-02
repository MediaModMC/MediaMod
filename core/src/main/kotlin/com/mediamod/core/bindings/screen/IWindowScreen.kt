package com.mediamod.core.bindings.screen

import com.mediamod.core.bindings.BindingRegistry

interface IWindowScreen {
    fun onClose() {

    }

    fun onResize(width: Int, height: Int) {

    }

    companion object : IWindowScreen by BindingRegistry.windowScreen
}
