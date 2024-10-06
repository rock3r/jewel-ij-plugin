package icons

import com.intellij.ui.IconManager

object StudioIcons {
  @Suppress("UnstableApiUsage")
  fun load(path: String, expUiPath: String, cacheKey: Int, flags: Int) =
    IconManager.Companion.getInstance()
      .loadRasterizedIcon(path, expUiPath, StudioIcons::class.java.classLoader, cacheKey, flags)

  object Compose {
    object Editor {
      @JvmField
      val COMPOSABLE_FUNCTION =
        load(
          "studio/icons/compose/editor/composable-function.svg",
          "intui/icons/compose/editor/composable-function.svg",
          1242742575,
          2,
        )
    }
  }

  object GutterIcons {
    @JvmField
    val COMPOSABLE_FUNCTION =
      load(
        "studio/icons/gutter-icons/composable-function.svg",
        "intui/icons/gutter-icons/composable-function.svg",
        -642809118,
        2,
      )
  }
}
