/*
 * Copyright 2019 The Android Open Source Project
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

import androidx.compose.compiler.plugins.kotlin.ComposeClassIds
import androidx.compose.compiler.plugins.kotlin.k1.hasComposableAnnotation
import com.android.tools.idea.kotlin.hasAnnotation
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.psi.PsiElement
import org.jetbrains.jewel.ijplugin.ProjectInfoProvider
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotated
import org.jetbrains.kotlin.analysis.api.permissions.KaAllowAnalysisOnEdt
import org.jetbrains.kotlin.analysis.api.permissions.allowAnalysisOnEdt
import org.jetbrains.kotlin.analysis.api.resolution.KaCallableMemberCall
import org.jetbrains.kotlin.analysis.api.resolution.calls
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaCallableSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaLocalVariableSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaPropertySymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaValueParameterSymbol
import org.jetbrains.kotlin.analysis.api.symbols.receiverType
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.idea.base.plugin.KotlinPluginModeProvider
import org.jetbrains.kotlin.idea.caches.resolve.resolveToCall
import org.jetbrains.kotlin.idea.refactoring.fqName.fqName
import org.jetbrains.kotlin.idea.search.usagesSearch.descriptor
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.util.OperatorNameConventions

private const val COMPOSABLE_CALL_TEXT_ATTRIBUTES_NAME = "ComposableCallTextAttributes"

internal val COMPOSABLE_CALL_TEXT_ATTRIBUTES_KEY: TextAttributesKey =
    TextAttributesKey.createTextAttributesKey(
        COMPOSABLE_CALL_TEXT_ATTRIBUTES_NAME,
        DefaultLanguageHighlighterColors.FUNCTION_CALL,
    )

internal val COMPOSABLE_CALL_TEXT_TYPE: HighlightInfoType =
    HighlightInfoType.HighlightInfoTypeImpl(
        HighlightInfoType.SYMBOL_TYPE_SEVERITY,
        COMPOSABLE_CALL_TEXT_ATTRIBUTES_KEY,
        false,
    )

fun isComposeEnabled(element: PsiElement): Boolean =
    ProjectInfoProvider.getInstance().getProjectInfo(element).let { info ->
        // If this is an Android project, we let the Android Plugin take care of things
        !info.isAndroidProject && info.isComposeEnabled
    }

fun isModifierChainLongerThanTwo(element: KtElement): Boolean {
    if (element.getChildrenOfType<KtDotQualifiedExpression>().isNotEmpty()) {
        val fqName = element.callReturnTypeFqName()?.asString()
        if (fqName == COMPOSE_MODIFIER_FQN) {
            return true
        }
    }
    return false
}

@Suppress("KotlincFE10", "UnstableApiUsage")
@OptIn(KaAllowAnalysisOnEdt::class)
internal fun KtElement.callReturnTypeFqName() =
    if (KotlinPluginModeProvider.isK2Mode()) {
        allowAnalysisOnEdt {
            analyze(this) {
                val call =
                    this@callReturnTypeFqName.resolveToCall()?.calls?.firstOrNull()
                      as? KaCallableMemberCall<*, *>
                call?.let { asFqName(it.symbol.returnType) }
            }
        }
    } else {
        resolveToCall(BodyResolveMode.PARTIAL)?.resultingDescriptor?.returnType?.fqName
    }

// TODO(274630452): When the upstream APIs are available, implement it based on `fullyExpandedType`
// and `KtTypeRenderer`.
internal fun KaSession.asFqName(type: KaType) = type.expandedSymbol?.classId?.asSingleFqName()

@Suppress("KotlincFE10")
internal fun KtFunction.hasComposableAnnotation() =
    if (KotlinPluginModeProvider.isK2Mode()) {
        hasAnnotation(ComposeClassIds.Composable)
    } else {
        descriptor?.hasComposableAnnotation() == true
    }

internal fun KaSession.isComposableInvocation(callableSymbol: KaCallableSymbol): Boolean {
    fun hasComposableAnnotation(annotated: KaAnnotated?) =
        annotated != null && ComposeClassIds.Composable in annotated.annotations

    val type = callableSymbol.returnType
    if (hasComposableAnnotation(type)) return true
    val functionSymbol = callableSymbol as? KaNamedFunctionSymbol
    if (
        functionSymbol != null &&
        functionSymbol.isOperator &&
        functionSymbol.name == OperatorNameConventions.INVOKE
    ) {
        functionSymbol.receiverType?.let { receiverType ->
            if (hasComposableAnnotation(receiverType)) return true
        }
    }
    return when (callableSymbol) {
        is KaValueParameterSymbol -> false
        is KaLocalVariableSymbol -> false
        is KaPropertySymbol -> hasComposableAnnotation(callableSymbol.getter)
        else -> hasComposableAnnotation(callableSymbol)
    }
}

internal fun isInLibrarySource(element: PsiElement) =
    element.containingFile.virtualFile != null &&
      ProjectFileIndex.getInstance(element.project)
          .isInLibrarySource(element.containingFile.virtualFile)
