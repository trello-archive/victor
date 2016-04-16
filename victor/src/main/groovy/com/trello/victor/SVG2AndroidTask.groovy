package com.trello.victor

import com.romainpiel.svgtoandroid.Svg2Vector
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import org.gradle.api.tasks.incremental.InputFileDetails

class SVG2AndroidTask extends DefaultTask {

    /**
     * The input SVGs.
     */
    @InputFiles
    FileCollection sources

    /**
     * The output directory.
     */
    @OutputDirectory
    File outputDir

    @TaskAction
    def transform(IncrementalTaskInputs inputs) {

        // If the whole thing isn't incremental, delete the build folder (if it exists)
        if (!inputs.isIncremental() && outputDir.exists()) {
            logger.debug("SVG2Android is not incremental; deleting build folder and starting fresh!")
            outputDir.delete()
        }

        List<File> svgFiles = []
        inputs.outOfDate { InputFileDetails change ->
            logger.debug("$change.file.name out of date; converting")
            svgFiles.add change.file
        }

        File resDir = getResourceDir()
        resDir.mkdirs()

        svgFiles.each { File svgFile ->
            File destination = new File(resDir, getDestinationFile(svgFile.name))
            OutputStream outStream = new FileOutputStream(destination)
            String error = Svg2Vector.parseSvgToXml(svgFile, outStream)
            if (!error.isEmpty()) {
                logger.error(error)
                throw new GradleException(error)
            }

            outStream.flush()
            outStream.close()

            logger.info("Converted $svgFile to $destination")
        }

        inputs.removed { change ->
            logger.debug("$change.file.name was removed; removing it from generated folder")

            File file = new File(resDir, getDestinationFile(change.file.name))
            file.delete()
        }
    }

    File getResourceDir() {
        return new File(outputDir, "/drawable")
    }

    String getDestinationFile(String name) {
        int suffixStart = name.lastIndexOf '.'
        return suffixStart == -1 ? name : "${name.substring(0, suffixStart)}.xml"
    }
}