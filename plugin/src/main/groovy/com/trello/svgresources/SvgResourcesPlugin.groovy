package com.trello.svgresources
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.tasks.JavaExec

class SvgResourcesPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.extensions.create('svg', SvgResourcesPluginExtension)

        project.configurations.create('batik')

        project.dependencies {
            batik 'org.apache.xmlgraphics:batik-codec:1.7'
            batik 'org.apache.xmlgraphics:batik-rasterizer:1.7'
        }

        // Add 'svg' as a source set extension
        project.android.sourceSets.all { sourceSet ->
            sourceSet.extensions.create('svg', DefaultSourceDirectorySet, 'svg', project.fileResolver)
        }

        project.afterEvaluate {
            Map densities = [
                    ldpi   : Math.round(project.svg.svgDpi * 0.5f),
                    mdpi   : project.svg.svgDpi,
                    hdpi   : Math.round(project.svg.svgDpi * 1.5f),
                    xhdpi  : project.svg.svgDpi * 2,
                    xxhdpi : project.svg.svgDpi * 3,
                    xxxhdpi: project.svg.svgDpi * 4
            ]

            project.svg.excludeDensities.each { density ->
                densities.remove(density)
            }

            // Register our task with the variant's resources
            project.android.applicationVariants.all { variant ->
                Set<File> srcDirs = new HashSet<>()
                variant.sourceSets.each { sourceSet ->
                    srcDirs.addAll(sourceSet.svg.srcDirs)
                }

                if (srcDirs.size() == 0) {
                    return
                }

                // Gather all available files
                // TODO: Use lazy evaluation?
                // TODO: Only accept SVG files that are named properly; reject (with warning) invalid resource names
                List<File> svgFiles = srcDirs.collect { srcDir ->
                    srcDir.listFiles(new FilenameFilter() {
                        @Override
                        boolean accept(File dir, String name) {
                            name.endsWith('.svg')
                        }
                    })
                }

                // If there are no files in a directory, it returns null
                svgFiles.removeAll {
                    it == null
                }

                svgFiles = svgFiles.flatten()

                if (svgFiles.size() == 0) {
                    return
                }

                // Overall task that we'll hook into
                String rasterizationTaskName = "rasterizeSvgsFor${variant.name.capitalize()}"
                project.task(rasterizationTaskName) {
                    ext.outputDir = project.file("$project.buildDir/generated/res/$flavorName/$buildType.name/svg/")
                }

                // TODO: Create our own convert class instead of using Batik's tool, which kicks off JAR executable
                densities.keySet().each { density ->
                    // TODO: If rasterize needs to run again, clear out build directory first
                    File svgBuildDir = new File(project.tasks[rasterizationTaskName].ext.outputDir, "drawable-$density")
                    String rasterizeTaskName = "rasterizeSvgsFor${variant.name.capitalize()}${density.capitalize()}"
                    project.task(rasterizeTaskName, type: JavaExec) {
                        inputs.files svgFiles
                        outputs.dir svgBuildDir

                        main = 'org.apache.batik.apps.rasterizer.Main'
                        classpath = project.configurations.batik
                        args '-scriptSecurityOff'
                        args '-d', svgBuildDir.absolutePath
                        args '-dpi', densities[density]
                        args svgFiles
                    }

                    project.tasks[rasterizationTaskName].dependsOn rasterizeTaskName
                }

                // Makes the magic happen (inserts resources so devs can use it)
                variant.registerResGeneratingTask(project.tasks[rasterizationTaskName],
                        project.tasks[rasterizationTaskName].ext.outputDir)
            }
        }
    }
}