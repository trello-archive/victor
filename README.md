victor
======

*What's our vector, Victor? -Captain Oveur*

Use SVGs as resources in Android!

With this plugin, you can define source folders for SVGs and they will automatically be rasterized/included in your build without messing with your source code.

Installation
------------

Add the following to your `build.gradle`:

```gradle
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.trello:victor:0.3.0'
    }
}

apply plugin: 'com.android.application'

// Make sure to apply this plugin *after* the Android plugin
apply plugin: 'com.trello.victor'
```

Also, your Android Gradle plugin (`com.android.tools.build:gradle`) must be at least version 1.1.+

Usage
-----

Victor adds the `svg` source set to the Android plugin. You can define where your SVG folders are in the same way you define any other source sets:

```gradle
android {
    sourceSets {
        main {
            svg.srcDir 'src/main/svg'
        }
    }
}
```

You can have multiple SVG folders for a variety of build types/product flavors; or you can just use 'main' to cover them all.

Additional configuration can be done in the `victor` closure:

```gradle
victor {
    // Any assets defined in relative terms needs a base DPI specified
    svgDpi = 72

    // Do not generate these densities for SVG assets
    excludeDensities = [ 'ldpi', 'xxxhdpi' ]

    // WARNING: EXPERIMENTAL
    // Generates Android drawables instead of PNGs.
    //
    // This is known not to work on only a subset of SVGs (e.g., does not support any value besides px).
    generateVectorDrawables = true
}
```

OSX Issues
----------

When using Android Studio on OSX, you might see this error:

`Toolkit not found: apple.awt.CToolkit`

This occurs because [Batik](http://xmlgraphics.apache.org/batik/), the SVG toolkit Victor uses, requires a working version of AWT.

If this happens you should install and use [JDK 1.7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html) for your instance of Android Studio. For instructions how, consult the "Mac OS X" section of [this article](https://intellij-support.jetbrains.com/entries/23455956-Selecting-the-JDK-version-the-IDE-will-run-under).

Known Issues
------------

- Android Studio doesn't recognize generated resources in XML, so autocomplete doesn't work and you get warnings (even though the code works fine). Generated resources should be fully supported in future versions of the Android Gradle plugin.

- Android Studio doesn't automatically rebuild if the SVG folder is modified (like it does with other resources). Therefore, if you add SVGs you will have to manually rebuild before they will be generated.

- Due to bugs in Batik, the SVG toolkit used by Victor, some SVGs containing 'mask="url(#mask-2)"' references are not rasterized correctly. Sketch and other tools occasionally produce assets with these references.

Planned Features
----------------

- 9-patch support
