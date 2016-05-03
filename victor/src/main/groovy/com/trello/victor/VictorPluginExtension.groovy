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

/**
 * Configuration values for the victor {} script block.
 */
class VictorPluginExtension {

    /**
     * The DPI to use for SVG assets whose size is defined in relative terms.
     *
     * For example, if the size is 1in x 1in, you would need to define how many
     * pixels are in an inch.
     *
     * This value is ignored for SVGs whose size are defined in absolute terms.
     *
     * The default value is 72 DPI.
     */
    int svgDpi = 72

    /**
     * Densities to exclude when generating SVG assets.
     *
     * If not set, all densities are generated.
     *
     * Possible values: ldpi, mdpi, hdpi, xhdpi, xxhdpi, xxxhpdi
     */
    List<String> excludeDensities = []

    /**
     * [Experimental]
     *
     * If set to true, Victor will generate Android vector drawables
     * If not, it will generate PNGs in all specified densities
     *
     * Note that the svgDpi and excludeDensities are ignored if
     * generateVectorDrawables is set to true
     *
     * The default value is false
     */
    boolean generateVectorDrawables = false

}
