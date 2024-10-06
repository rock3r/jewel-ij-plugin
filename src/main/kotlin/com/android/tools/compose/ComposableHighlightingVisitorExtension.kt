/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.tools.compose

import androidx.compose.compiler.plugins.kotlin.k1.isComposableInvocation
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.idea.highlighter.KotlinHighlightingVisitorExtension
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall

/**
 * Used to apply styles for calls to @Composable functions.
 *
 * JetBrains documentation recommends doing highlighting such as this using
 * [com.intellij.lang.annotation.Annotator] (guidance available at
 * https://plugins.jetbrains.com/docs/intellij/syntax-highlighting-and-error-highlighting.html#annotator).
 * But it turns out that the Kotlin plugin is running its syntax highlighting using a different
 * mechanism which can run in parallel with annotators. That doesn't matter for annotators that
 * don't conflict with the built-in highlighting, but in the case of Compose we are overriding some
 * of the standard function colors, and so we need to ensure that Compose highlighting takes
 * precedence.
 *
 * Luckily, the Kotlin plugin provides its own extension mechanism, which is implemented here with
 * [KotlinHighlightingVisitorExtension]. When this code returns Composable function highlighting for
 * a given function call, it will always be used instead of the default Kotlin highlighting.
 */
@Suppress("KotlincFE10")
class ComposableHighlightingVisitorExtension : KotlinHighlightingVisitorExtension() {
    override fun highlightDeclaration(
        elementToHighlight: PsiElement,
        descriptor: DeclarationDescriptor,
    ): HighlightInfoType? = null

    @Suppress("UnstableApiUsage")
    override fun highlightCall(
        elementToHighlight: PsiElement,
        resolvedCall: ResolvedCall<*>,
    ): HighlightInfoType? {
        if (!resolvedCall.isComposableInvocation()) return null

        // For composable invocations, highlight if either:
        // 1. compose is enabled for the current module, or
        // 2. the file is part of a library's source code.
        return if (isComposeEnabled(elementToHighlight) || isInLibrarySource(elementToHighlight))
            COMPOSABLE_CALL_TEXT_TYPE
        else null
    }
}
