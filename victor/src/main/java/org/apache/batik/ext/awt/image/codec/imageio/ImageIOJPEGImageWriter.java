/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.ext.awt.image.codec.imageio;

import java.awt.image.RenderedImage;

import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;

import org.apache.batik.ext.awt.image.spi.ImageWriterParams;

/**
 * ImageWriter that encodes JPEG images using Image I/O.
 *
 * @version $Id$
 */
public class ImageIOJPEGImageWriter extends ImageIOImageWriter {

    private static final String JPEG_NATIVE_FORMAT = "javax_imageio_jpeg_image_1.0";

    /**
     * Main constructor.
     */
    public ImageIOJPEGImageWriter() {
        super("image/jpeg");
    }

    /** {@inheritDoc} */
    @Override
    protected IIOMetadata updateMetadata(IIOMetadata meta, ImageWriterParams params) {
        //ImageIODebugUtil.dumpMetadata(meta);
        if (JPEG_NATIVE_FORMAT.equals(meta.getNativeMetadataFormatName())) {
            meta = addAdobeTransform(meta);

            IIOMetadataNode root = (IIOMetadataNode)meta.getAsTree(JPEG_NATIVE_FORMAT);

            IIOMetadataNode jv = getChildNode(root, "JPEGvariety");
            if (jv == null) {
                jv = new IIOMetadataNode("JPEGvariety");
                root.appendChild(jv);
            }
            IIOMetadataNode child;
            if (params.getResolution() != null) {
                child = getChildNode(jv, "app0JFIF");
                if (child == null) {
                    child = new IIOMetadataNode("app0JFIF");
                    jv.appendChild(child);
                }
                //JPEG gets special treatment because there seems to be a bug in
                //the JPEG codec in ImageIO converting the pixel size incorrectly
                //(or not at all) when using standard metadata format.
                child.setAttribute("majorVersion", null);
                child.setAttribute("minorVersion", null);
                child.setAttribute("resUnits", "1"); //dots per inch
                child.setAttribute("Xdensity", params.getResolution().toString());
                child.setAttribute("Ydensity", params.getResolution().toString());
                child.setAttribute("thumbWidth", null);
                child.setAttribute("thumbHeight", null);

            }

            try {
                meta.setFromTree(JPEG_NATIVE_FORMAT, root);
            } catch (IIOInvalidTreeException e) {
                throw new RuntimeException("Cannot update image metadata: "
                            + e.getMessage(), e);
            }

            //ImageIODebugUtil.dumpMetadata(meta);
        }

        return meta;
    }

    private static IIOMetadata addAdobeTransform(IIOMetadata meta) {
        // add the adobe transformation (transform 1 -> to YCbCr)
        IIOMetadataNode root = (IIOMetadataNode)meta.getAsTree(JPEG_NATIVE_FORMAT);

        IIOMetadataNode markerSequence = getChildNode(root, "markerSequence");
        if (markerSequence == null) {
            throw new RuntimeException("Invalid metadata!");
        }

        IIOMetadataNode adobeTransform = getChildNode(markerSequence, "app14Adobe");
        if (adobeTransform == null) {
            adobeTransform = new IIOMetadataNode("app14Adobe");
            adobeTransform.setAttribute("transform" , "1"); // convert RGB to YCbCr
            adobeTransform.setAttribute("version", "101");
            adobeTransform.setAttribute("flags0", "0");
            adobeTransform.setAttribute("flags1", "0");

            markerSequence.appendChild(adobeTransform);
        } else {
            adobeTransform.setAttribute("transform" , "1");
        }

        try {
            meta.setFromTree(JPEG_NATIVE_FORMAT, root);
        } catch (IIOInvalidTreeException e) {
            throw new RuntimeException("Cannot update image metadata: "
                        + e.getMessage(), e);
        }
        return meta;
    }

    /** {@inheritDoc} */
    @Override
    protected ImageWriteParam getDefaultWriteParam(
            ImageWriter iiowriter, RenderedImage image,
            ImageWriterParams params) {
        JPEGImageWriteParam param = new JPEGImageWriteParam(iiowriter.getLocale());
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(params.getJPEGQuality());
        if (params.getCompressionMethod() != null
                && !"JPEG".equals(params.getCompressionMethod())) {
            throw new IllegalArgumentException(
                    "No compression method other than JPEG is supported for JPEG output!");
        }
        if (params.getJPEGForceBaseline()) {
            param.setProgressiveMode(JPEGImageWriteParam.MODE_DISABLED);
        }
        return param;
    }


}
