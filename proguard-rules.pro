-keep, includedescriptorclasses class com.brittank88.dtdm.** { *; }
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, AnnotationDefault
-dontwarn org.jetbrains.annotations.**

-dontobfuscate

-optimizations library/*, class/unboxing/*, class/merging/*, field/removal/*, method/propagation/*, method/inlining/*, code/merging, code/simplification/*, code/removal/variable, code/removal/exception