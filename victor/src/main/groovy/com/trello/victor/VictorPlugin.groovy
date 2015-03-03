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

class VictorPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.extensions.create('victor', VictorPluginExtension)

        // Add 'svg' as a source set extension
        project.android.sourceSets.all { sourceSet ->
            sourceSet.extensions.create('svg', DefaultSourceDirectorySet, 'svg', project.fileResolver)
        }

        project.afterEvaluate {
            List<Density> densities = Density.values()
            project.victor.excludeDensities.each { String density ->
                densities.remove(Density.valueOf(density.toUpperCase()))
            }

            // Register our task with the variant's resources
            project.android.applicationVariants.all { variant ->
                // TODO: Use lazier evaluation for files by sticking this in a prep task?
                FileCollection svgFiles = project.files()
                variant.sourceSets.each { sourceSet ->
                    // TODO: Only accept SVG files that are named properly; reject (with warning) invalid resource names
                    FileCollection filteredCollection = sourceSet.svg.filter { File file ->
                        file.name.endsWith '.svg'
                    }
                    svgFiles.from filteredCollection
                }

                if (svgFiles.empty) {
                    return
                }

                Task rasterizationTask = project.task("rasterizeSvgsFor${variant.name.capitalize()}", type: RasterizeTask) {
                    sources = svgFiles
                    outputDir = project.file("$project.buildDir/generated/res/$flavorName/$buildType.name/svg/")
                    includeDensities = densities
                    baseDpi = project.victor.svgDpi
                }

                // Makes the magic happen (inserts resources so devs can use it)
                variant.registerResGeneratingTask(rasterizationTask, rasterizationTask.outputDir)
            }
        }
    }
}