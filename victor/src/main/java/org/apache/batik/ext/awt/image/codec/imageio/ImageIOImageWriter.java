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
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.event.IIOWriteWarningListener;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.batik.ext.awt.image.spi.ImageWriter;
import org.apache.batik.ext.awt.image.spi.ImageWriterParams;

/**
 * ImageWriter implementation that uses Image I/O to write images.
 *
 * @version $Id$
 */
public class ImageIOImageWriter implements ImageWriter, IIOWriteWarningListener {

    private String targetMIME;
    
    /**
     * Main constructor.
     * @param mime the MIME type of the image format
     */
    public ImageIOImageWriter(String mime) {
        this.targetMIME = mime;
    }
    
    /**
     * @see ImageWriter#writeImage(RenderedImage, OutputStream)
     */
    public void writeImage(RenderedImage image, OutputStream out) throws IOException {
        writeImage(image, out, null);
    }

    /**
     * @see ImageWriter#writeImage(RenderedImage, OutputStream, ImageWriterParams)
     */
    public void writeImage(RenderedImage image, OutputStream out, 
            ImageWriterParams params) 
                throws IOException {
        Iterator iter;
        iter = ImageIO.getImageWritersByMIMEType(getMIMEType());
        javax.imageio.ImageWriter iiowriter = null;
        try {
            iiowriter = (javax.imageio.ImageWriter)iter.next();
            if (iiowriter != null) {
                iiowriter.addIIOWriteWarningListener(this);

                ImageOutputStream imgout = null;
                try {
                    imgout = ImageIO.createImageOutputStream(out);
                    ImageWriteParam iwParam = getDefaultWriteParam(iiowriter, image, params);

                    ImageTypeSpecifier type;
                    if (iwParam.getDestinationType() != null) {
                        type = iwParam.getDestinationType();
                    } else {
                        type = ImageTypeSpecifier.createFromRenderedImage(image);
                    }

                    //Handle metadata
                    IIOMetadata meta = iiowriter.getDefaultImageMetadata(
                            type, iwParam);
                    //meta might be null for some JAI codecs as they don't support metadata
                    if (params != null && meta != null) {
                        meta = updateMetadata(meta, params); 
                    }

                    //Write image
                    iiowriter.setOutput(imgout);
                    IIOImage iioimg = new IIOImage(image, null, meta);
                    iiowriter.write(null, iioimg, iwParam);
                } finally {
                    if (imgout != null) {
                        imgout.close();
                    }
                }
            } else {
                throw new UnsupportedOperationException("No ImageIO codec for writing " 
                        + getMIMEType() + " is available!");
            }
        } finally {
            if (iiowriter != null) {
                iiowriter.dispose();
            }
        }
    }
    
    /**
     * Returns the default write parameters for encoding the image.
     * @param iiowriter The IIO ImageWriter that will be used
     * @param image the image to be encoded
     * @param params the parameters for this writer instance
     * @return the IIO ImageWriteParam instance
     */
    protected ImageWriteParam getDefaultWriteParam(
            javax.imageio.ImageWriter iiowriter, RenderedImage image, 
            ImageWriterParams params) {
        ImageWriteParam param = iiowriter.getDefaultWriteParam();
        if ((params != null) && (params.getCompressionMethod() != null)) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionType(params.getCompressionMethod());
        }
        return param; 
    }
    
    /**
     * Updates the metadata information based on the parameters to this writer.
     * @param meta the metadata
     * @param params the parameters
     * @return the updated metadata
     */
    protected IIOMetadata updateMetadata(IIOMetadata meta, ImageWriterParams params) {
        final String stdmeta = "javax_imageio_1.0";
        if (meta.isStandardMetadataFormatSupported()) {
            IIOMetadataNode root = (IIOMetadataNode)meta.getAsTree(stdmeta);
            IIOMetadataNode dim = getChildNode(root, "Dimension");
            IIOMetadataNode child;
            if (params.getResolution() != null) {
                child = getChildNode(dim, "HorizontalPixelSize");
                if (child == null) {
                    child = new IIOMetadataNode("HorizontalPixelSize");
                    dim.appendChild(child);
                }
                child.setAttribute("value", 
                        Double.toString(params.getResolution().doubleValue() / 25.4));
                child = getChildNode(dim, "VerticalPixelSize");
                if (child == null) {
                    child = new IIOMetadataNode("VerticalPixelSize");
                    dim.appendChild(child);
                }
                child.setAttribute("value", 
                        Double.toString(params.getResolution().doubleValue() / 25.4));
            }
            try {
                meta.mergeTree(stdmeta, root);
            } catch (IIOInvalidTreeException e) {
                throw new RuntimeException("Cannot update image metadata: " 
                            + e.getMessage());
            }
        }
        return meta;
    }
    
    /**
     * Returns a specific metadata child node
     * @param n the base node
     * @param name the name of the child
     * @return the requested child node
     */
    protected static IIOMetadataNode getChildNode(Node n, String name) {
        NodeList nodes = n.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node child = nodes.item(i);
            if (name.equals(child.getNodeName())) {
                return (IIOMetadataNode)child;
            }
        }
        return null;
    }

    /**
     * @see ImageWriter#getMIMEType()
     */
    public String getMIMEType() {
        return this.targetMIME;
    }

    /**
     * @see IIOWriteWarningListener#warningOccurred(javax.imageio.ImageWriter, int, String)
     */
    public void warningOccurred(javax.imageio.ImageWriter source, 
            int imageIndex, String warning) {
        System.err.println("Problem while writing image using ImageI/O: " 
                + warning);
    }
}
