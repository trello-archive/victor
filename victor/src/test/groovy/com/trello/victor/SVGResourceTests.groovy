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
import org.junit.Test

import static org.junit.Assert.assertEquals

class SVGResourceTests {

    private final static RESOURCE_PATH = './src/test/resources/'

    @Test
    void canGetRelativeSize() {
        File svgFile = new File(RESOURCE_PATH, 'relative.svg')
        SVGResource svgResource = new SVGResource(svgFile, 72)
        assertEquals(24, svgResource.width)
        assertEquals(24, svgResource.height)
    }

    @Test
    void canReadPixelSize() {
        File svgFile = new File(RESOURCE_PATH, 'pixel.svg')
        SVGResource svgResource = new SVGResource(svgFile, 72)
        assertEquals(24, svgResource.width)
        assertEquals(24, svgResource.height)
    }

    @Test
    void ignoresNonexistantFiles() {
        // Simply tests that it doesn't crash
        File svgFile = new File('does-not-exist.svg')
        new SVGResource(svgFile, 72)
    }

    @Test
    void invalidSvg() {
        File svgFile = new File(RESOURCE_PATH, 'invalid.svg')

        // This is simply testing that it doesn't blow up if you pass it an invalid file
        SVGResource svgResource = new SVGResource(svgFile, 72)
    }
}
