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

import com.intellij.codeInsight.generation.OverrideImplementsAnnotationsFilter
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.KtFile

/** Ensure that any @Composable annotations are retained when implementing an interface. */
class ComposeOverrideImplementsAnnotationsFilter : OverrideImplementsAnnotationsFilter {
  override fun getAnnotations(file: PsiFile): Array<String> =
    if (file is KtFile && isComposeEnabled(file)) arrayOf(COMPOSABLE_ANNOTATION_FQ_NAME)
    else arrayOf()
}
