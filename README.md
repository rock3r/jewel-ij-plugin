# Jewel IntelliJ Plugin

Experimental copy of Compose tooling from Android Studio's `compose-ide-plugin` that works in IntelliJ IDEA, even
without the Android plugin enabled.

This is **experimental, untested, and probably broken**. You've been warned!

# Ported features

These features have been ported from Studio:

## Debugger

* Composition state is displayed in the debugger when debugging a composable function

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

## License

All code in this plugin is Copyright by the Android Open Source Project, licensed under the Apache 2.0 license.

```
Copyright 2024 The Android Open Source Project

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