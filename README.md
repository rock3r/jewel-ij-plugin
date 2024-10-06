# Jewel IntelliJ Plugin

Experimental copy of Compose tooling from Android Studio's `compose-ide-plugin` that works in IntelliJ IDEA, even
without the Android plugin enabled.

This is **experimental, untested, and probably broken**. You've been warned!

This plugin will only be active on non-Android modules, or when the IntelliJ Android plugin is disabled.

# Ported features

These features have been ported from Studio:

## Debugger

* Composition state is displayed in the debugger when debugging a composable function
* Enable setting breakpoints inside composable singleton lambdas
* Allow skipping over Compose runtime inner classes (with setting to toggle, default enabled)
* Add Composable function breakpoint type
* Improve rendering of Compose state objects in debugger's variables view
* Improve rendering of MapEntry values

## Editor

* State read inlay hints
    * This is off by default, you can enable it by alt-enter over a State read and selecting "Enable inlay hints for
      State reads"
* Composable function invocation colour highlighting
    * You can customise these colours in _Settings | Editor | Color Scheme | Compose_
* Composable function invocation: icon in gutter
    * This is off by default, you can enable it by going to _Settings | Editor | General | Gutter Icons_ and turning on
      the "@Composable function call" item in the Jewel section
* Compose Color preview in gutter; can be clicked to open a colour picker
    * Slight difference from Studio: we don't append the Material colours (they're pretty meaningless in 2024 anyway) 
* Tweaks to how Composable functions are presented across the IDE features (e.g., in Add Imports)
    * Add @Composable, ellipsize optional parameters 
* Compose-aware folding tweaks
* Compose Kotlin code style tab
* Compose intentions and quick-fixes
  * Surround with widget
  * Unwrap composable
  * Wrap modifiers
  * Create preview
  * Create composable function
  * Add missing composable annotation
* Live templates: Compose, Compose Preview 
* Code completion
  * Improve completion order for Compose code 
    * Prioritise modifiers, colours, etc
    * Prioritise composable functions
    * Improve trailing lambda completion UX
    * Improve Material Icons preview in completion
    * Improve suggestions for Alignment and Arrangement 
  * Setting: insert required trailing lambdas for composables
  * Note: ConstraintLayout completion tweaks have been omitted as it is only available on Android
* Suppress inspection that flags composable functions with names starting with capital letters
* Formatting: split Modifier chains to multiple lines (with setting to toggle, default on)
* Renaming: renames a file if a @Composable function with the same name was renamed
* Ensure @Composable annotation is retained when extracting interfaces and functions
* Add usage grouping rule provider for composables and previews
* Add Compose documentation provider for completion

# License

All code in this plugin is Copyright by the Android Open Source Project, or by JetBrains, 
licensed under the Apache 2.0 license.

```
Copyright 2024 The Android Open Source Project and JetBrains

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0
     
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
