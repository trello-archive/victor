victor
======

*What's our vector, Victor? -Captain Oveur*

Use SVGs as resources in Android!

With this plugin, you can define source folders for SVGs and they will automatically be rasterized/included in your build without messing with your source code.

Installation
------------

The plugin is pre-alpha and thus is not published. If you absolutely must have it now, you can create a `$projectDir/buildSrc/` directory in your project then place the contents of `/plugin/` inside of it.

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
}
```

OSX Issues
----------

When using Android Studio on OSX, you might see this error:

`Toolkit not found: apple.awt.CToolkit`

If this happens you should install and use [JDK 1.7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html) for your instance of Android Studio. For instructions how, consult the "Mac OS X" section of [this article](https://intellij-support.jetbrains.com/entries/23455956-Selecting-the-JDK-version-the-IDE-will-run-under).

Known Issues
------------

- Android Studio doesn't automatically rebuild if the SVG folder is modified; if you change your SVGs you should make sure to rebuild.
- Android Studio doesn't recognize SVG resources in XML (so no autocomplete + warnings). The Android plugin will support it someday.
- Currently, Victor only resizes SVGs whose size is defined in relative terms (e.g. `pt` or `in`). In the future it will properly resize any SVG.

Planned Features
----------------

- 9-patch support
- Better handling of invalid filenames for SVGs
- Better handling for invalid SVGs (that don't rasterize)
- Faster build time (currently rasterizes all SVGs per variant, could be more efficient)
