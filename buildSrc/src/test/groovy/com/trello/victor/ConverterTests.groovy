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

import org.apache.commons.io.FileUtils
import org.junit.BeforeClass
import org.junit.Test
import static org.junit.Assert.*

class ConverterTests {

    private final static RESOURCE_PATH = './src/test/resources/'

    // TODO: Figure out a better way to get $buildDir inside of a test
    private final static OUT_PATH = './build/test/out/'

    @BeforeClass
    static void setup() {
        File destinationDir = new File(OUT_PATH)
        destinationDir.mkdirs()
    }

    @Test
    void canRasterizePng() {
        Converter converter = new Converter()

        File svgFile = new File(RESOURCE_PATH, 'rasterize.svg')
        File destination = new File(OUT_PATH, 'rasterize.png')
        converter.transcode(svgFile, Density.MDPI, 72, destination)

        assertTrue destination.exists()

        File expected = new File(RESOURCE_PATH, 'rasterize-expected.png')
        assertTrue FileUtils.contentEquals(destination, expected)
    }

    @Test
    void canResizePngWithRelativeSize() {
        Converter converter = new Converter()

        File svgFile = new File(RESOURCE_PATH, 'relative.svg')

        File destinationLdpi = new File(OUT_PATH, 'relative-ldpi.png')
        converter.transcode(svgFile, Density.LDPI, 72, destinationLdpi)

        File destinationMdpi = new File(OUT_PATH, 'relative-mdpi.png')
        converter.transcode(svgFile, Density.MDPI, 72, destinationMdpi)

        File destinationHdpi = new File(OUT_PATH, 'relative-hdpi.png')
        converter.transcode(svgFile, Density.HDPI, 72, destinationHdpi)

        File destinationXhdpi = new File(OUT_PATH, 'relative-xhdpi.png')
        converter.transcode(svgFile, Density.XHDPI, 72, destinationXhdpi)

        assertTrue destinationLdpi.exists()
        assertTrue destinationMdpi.exists()
        assertTrue destinationHdpi.exists()
        assertTrue destinationXhdpi.exists()

        File expectedLdpi = new File(RESOURCE_PATH, 'relative-ldpi-expected.png')
        File expectedMdpi = new File(RESOURCE_PATH, 'relative-mdpi-expected.png')
        File expectedHdpi = new File(RESOURCE_PATH, 'relative-hdpi-expected.png')
        File expectedXhdpi = new File(RESOURCE_PATH, 'relative-xhdpi-expected.png')

        assertTrue FileUtils.contentEquals(destinationLdpi, expectedLdpi)
        assertTrue FileUtils.contentEquals(destinationMdpi, expectedMdpi)
        assertTrue FileUtils.contentEquals(destinationHdpi, expectedHdpi)
        assertTrue FileUtils.contentEquals(destinationXhdpi, expectedXhdpi)
    }

    @Test
    void canResizePngWithPixelSize() {
        Converter converter = new Converter()

        File svgFile = new File(RESOURCE_PATH, 'pixel.svg')

        File destinationLdpi = new File(OUT_PATH, 'pixel-ldpi.png')
        converter.transcode(svgFile, Density.LDPI, 72, destinationLdpi)

        File destinationMdpi = new File(OUT_PATH, 'pixel-mdpi.png')
        converter.transcode(svgFile, Density.MDPI, 72, destinationMdpi)

        File destinationHdpi = new File(OUT_PATH, 'pixel-hdpi.png')
        converter.transcode(svgFile, Density.HDPI, 72, destinationHdpi)

        File destinationXhdpi = new File(OUT_PATH, 'pixel-xhdpi.png')
        converter.transcode(svgFile, Density.XHDPI, 72, destinationXhdpi)

        assertTrue destinationLdpi.exists()
        assertTrue destinationMdpi.exists()
        assertTrue destinationHdpi.exists()
        assertTrue destinationXhdpi.exists()

        File expectedLdpi = new File(RESOURCE_PATH, 'pixel-ldpi-expected.png')
        File expectedMdpi = new File(RESOURCE_PATH, 'pixel-mdpi-expected.png')
        File expectedHdpi = new File(RESOURCE_PATH, 'pixel-hdpi-expected.png')
        File expectedXhdpi = new File(RESOURCE_PATH, 'pixel-xhdpi-expected.png')

        assertTrue FileUtils.contentEquals(destinationLdpi, expectedLdpi)
        assertTrue FileUtils.contentEquals(destinationMdpi, expectedMdpi)
        assertTrue FileUtils.contentEquals(destinationHdpi, expectedHdpi)
        assertTrue FileUtils.contentEquals(destinationXhdpi, expectedXhdpi)
    }
}
