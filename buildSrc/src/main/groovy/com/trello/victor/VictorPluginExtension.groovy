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

class VictorPluginExtension {
    /** Base DPI of SVG assets */
    int svgDpi = 72

    /** Densities to exclude when generating SVG assets (ldpi, mdpi, hdpi, xhdpi, xxhdpi, or xxxhpdi) */
    List<String> excludeDensities = []
}
