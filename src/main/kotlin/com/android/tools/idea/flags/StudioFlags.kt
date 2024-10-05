package com.android.tools.idea.flags

import com.intellij.openapi.util.registry.Registry

/** Emulates Android Studio's homonymous feature, but uses the IJP's registry under the hood. */
object StudioFlags {
    val COMPOSE_STATE_READ_INLAY_HINTS_ENABLED = FlagFacade {
        Registry.`is`("jewel.compose.state.read.inlay.hints.enabled")
    }
}

/**
 * A simple facade used to provide a binary-compatible API for [StudioFlags] properties, so that no
 * source code changes are required for copy-pasted code.
 */
fun interface FlagFacade<T> {
    fun get(): T
}
