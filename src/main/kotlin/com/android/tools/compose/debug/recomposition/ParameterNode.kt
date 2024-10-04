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
package com.android.tools.compose.debug.recomposition

import com.android.tools.compose.ComposeBundle
import com.intellij.debugger.engine.JavaStackFrame
import com.intellij.debugger.engine.JavaValue
import com.intellij.debugger.engine.evaluation.EvaluationContextImpl
import com.intellij.debugger.jdi.LocalVariableProxyImpl
import com.intellij.debugger.ui.impl.watch.LocalVariableDescriptorImpl
import com.intellij.icons.AllIcons
import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.xdebugger.frame.XNamedValue
import com.intellij.xdebugger.frame.XValueChildrenList
import com.intellij.xdebugger.frame.XValueNode
import com.intellij.xdebugger.frame.XValuePlace

/** A [XNamedValue] representing a Composable function parameter and its recomposition state. */
internal class ParameterNode(
  private val context: EvaluationContextImpl,
  name: String,
  param: LocalVariableProxyImpl?,
  private val state: ParamState,
) : XNamedValue(name) {
  // Create the child node early because we are known to be on the correct thread.
  private val valueNode =
    if (param != null) createValueNode(param) else createOptimizedOutNode(name)

  override fun computePresentation(node: XValueNode, place: XValuePlace) {
    node.setPresentation(AllIcons.Nodes.Parameter, null, state.getDisplayName(), true)
  }

  override fun computeChildren(node: XCompositeNode) {
    node.addChildren(XValueChildrenList.singleton(valueNode), true)
  }

  private fun createValueNode(param: LocalVariableProxyImpl): JavaValue {
    val nodeManager =
      context.debugProcess.xdebugProcess?.nodeManager
        ?: throw IllegalStateException("Missing node manager")
    val descriptor =
      object : LocalVariableDescriptorImpl(context.project, param) {
        override fun getName() = ComposeBundle.message("recomposition.state.value")
      }
    return JavaValue.create(null, descriptor, context, nodeManager, false)
  }
}

private fun createOptimizedOutNode(name: String): XNamedValue =
  JavaStackFrame.createMessageNode(
    ComposeBundle.message("recomposition.optimised.variable.message", "\'$name\'"),
    AllIcons.General.Information,
  )
