package org.jetbrains.jewel.ijplugin

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.PsiElement

interface ProjectInfoProvider {

    fun getProjectInfo(element: PsiElement): ProjectInfo

    data class ProjectInfo(val isAndroidProject: Boolean, val isComposeEnabled: Boolean)

    companion object {

        private val EP_NAME =
            ExtensionPointName.create<ProjectInfoProvider>(
                "org.jetbrains.jewel.projectInfoProvider"
            )

        fun getInstance() = EP_NAME.extensions.first()

        fun getNonAndroidInstance() =
            EP_NAME.extensions.last { provider -> provider is NonAndroidProjectInfoProvider }
    }
}
