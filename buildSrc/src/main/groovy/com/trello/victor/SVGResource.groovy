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
import org.apache.batik.bridge.BridgeContext
import org.apache.batik.bridge.UnitProcessor
import org.apache.batik.bridge.UserAgent
import org.apache.batik.bridge.UserAgentAdapter
import org.apache.batik.dom.svg.SAXSVGDocumentFactory
import org.apache.batik.util.XMLResourceDescriptor
import org.w3c.dom.svg.SVGDocument
import org.w3c.dom.svg.SVGSVGElement

/**
 * Automatically calculates the width and height of an SVG file.
 */
class SVGResource {

    File file

    // Data read from the file, if it exists
    int width
    int height

    SVGResource(File file, int dpi) {
        this.file = file

        readSvgInfo(dpi)
    }

    private void readSvgInfo(int dpi) {
        if (!file.exists()) {
            return
        }

        String parser = XMLResourceDescriptor.getXMLParserClassName()
        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser)
        SVGDocument document = (SVGDocument) factory.createDocument(file.toURI().toString())
        SVGSVGElement svgElement = document.getRootElement()

        UserAgent userAgent = new DensityUserAgent(dpi)
        BridgeContext bridgeContext = new BridgeContext(userAgent)
        org.apache.batik.parser.UnitProcessor.Context context = UnitProcessor.createContext(bridgeContext, svgElement)

        width = UnitProcessor.svgHorizontalLengthToUserSpace(svgElement.width.baseVal.valueAsString, '', context)
        height = UnitProcessor.svgVerticalLengthToUserSpace(svgElement.height.baseVal.valueAsString, '', context)
    }

    private static final class DensityUserAgent extends UserAgentAdapter {

        private float pixelUnitToMillimeter

        private DensityUserAgent(int dpi) {
            pixelUnitToMillimeter = (2.54f / dpi) * 10
        }

        @Override
        float getPixelUnitToMillimeter() {
            return pixelUnitToMillimeter
        }
    }
}