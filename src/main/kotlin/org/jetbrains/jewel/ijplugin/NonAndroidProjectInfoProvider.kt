package org.jetbrains.jewel.ijplugin

import com.intellij.psi.PsiElement

class NonAndroidProjectInfoProvider : ProjectInfoProvider {
  override fun getProjectInfo(element: PsiElement): ProjectInfoProvider.ProjectInfo =
    ProjectInfoProvider.ProjectInfo(
      isAndroidProject = false,
      isComposeEnabled = true // TODO define better heuristics
    )
}
