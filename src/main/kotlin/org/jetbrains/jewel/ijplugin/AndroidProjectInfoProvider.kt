package org.jetbrains.jewel.ijplugin

import com.android.tools.idea.projectsystem.getModuleSystem
import com.intellij.psi.PsiElement
import org.jetbrains.android.facet.AndroidFacet

class AndroidProjectInfoProvider : ProjectInfoProvider {
    override fun getProjectInfo(element: PsiElement): ProjectInfoProvider.ProjectInfo {
        val moduleSystem = element.getModuleSystem()

        val isAndroidProject = moduleSystem?.module?.let { AndroidFacet.getInstance(it) } != null
        return if (isAndroidProject) {
            ProjectInfoProvider.ProjectInfo(
                isAndroidProject = true,
                isComposeEnabled = moduleSystem?.usesCompose == true,
            )
        } else {
            ProjectInfoProvider.getNonAndroidInstance().getProjectInfo(element)
        }
    }
}
