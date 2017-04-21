/*
 * Copyright 2015 Trello, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.trello.victor

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.util.GradleVersion

class VictorPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.extensions.create('victor', VictorPluginExtension)

        // We can either implement our own SourceDirectorySet (which is a PITA)
        // or we can suffer the lesser pains of accessing internal APIs
        final boolean useNewSourceDirectorySet =
            GradleVersion.current().compareTo(GradleVersion.version("2.12")) >= 0

        // Add 'svg' as a source set extension
        project.android.sourceSets.all { sourceSet ->
            if (useNewSourceDirectorySet) {
                Class defaultFileTreeFactoryClass =
                    Class.forName("org.gradle.api.internal.file.collections.DefaultDirectoryFileTreeFactory")

                sourceSet.extensions.create('svg', DefaultSourceDirectorySet, 'svg',
                    project.fileResolver,
                    defaultFileTreeFactoryClass.getConstructor().newInstance())
            }
            else {
                sourceSet.extensions.create('svg', DefaultSourceDirectorySet, 'svg',
                    project.fileResolver)
            }
        }

        project.afterEvaluate {
            List<Density> densities = Density.values()
            project.victor.excludeDensities.each { String density ->
                densities.remove(Density.valueOf(density.toUpperCase()))
            }

            // Keep order consistent
            densities = densities.sort()

            def variants = null
            if (project.android.hasProperty('applicationVariants')) {
                variants = project.android.applicationVariants
            }
            else if (project.android.hasProperty('libraryVariants')) {
                variants = project.android.libraryVariants
            }
            else {
                throw new IllegalStateException('Android project must have applicationVariants or libraryVariants!')
            }

            // Register our task with the variant's resources
            variants.all { variant ->
                // TODO: Use lazier evaluation for files by sticking this in a prep task?
                FileCollection svgFiles = project.files()
                variant.sourceSets.each { sourceSet ->
                    FileCollection filteredCollection = sourceSet.svg.filter { File file ->
                        file.name.endsWith '.svg'
                    }
                    svgFiles.from filteredCollection
                }

                if (svgFiles.empty) {
                    return
                }


                File conversionOutputDir = project.file("$project.buildDir/generated/res/$flavorName/$buildType.name/svg/")
                Task conversionTask = project.task("rasterizeSvgsFor${variant.name.capitalize()}", type: RasterizeTask) {
                    sources = svgFiles
                    outputDir = conversionOutputDir
                    includeDensities = densities
                    baseDpi = project.victor.svgDpi
                    generateVectorDrawables = project.victor.generateVectorDrawables
                }

                // Makes the magic happen (inserts resources so devs can use it)
                variant.registerResGeneratingTask(conversionTask, conversionTask.outputDir)
            }
        }
    }
}