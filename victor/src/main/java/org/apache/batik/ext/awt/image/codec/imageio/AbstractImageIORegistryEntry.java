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

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.renderable.DeferRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.RedRable;
import org.apache.batik.ext.awt.image.rendered.Any2sRGBRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.FormatRed;
import org.apache.batik.ext.awt.image.spi.ImageTagRegistry;
import org.apache.batik.ext.awt.image.spi.MagicNumberRegistryEntry;
import org.apache.batik.util.ParsedURL;

/**
 * This is the base class for all ImageIO-based RegistryEntry implementations. They
 * have a slightly lower priority than the RegistryEntry implementations using the
 * internal codecs, so these take precedence if they are available.
 *
 * @version $Id$
 */
public abstract class AbstractImageIORegistryEntry
    extends MagicNumberRegistryEntry {

    /**
     * Constructor
     * @param name Format Name
     * @param exts Standard set of extensions
     * @param magicNumbers array of magic numbers any of which can match.
     */
    public AbstractImageIORegistryEntry(String    name,
                                        String [] exts,
                                        String [] mimeTypes,
                                        MagicNumber [] magicNumbers) {
        super(name, PRIORITY + 100, exts, mimeTypes, magicNumbers);
    }

    /**
     * Constructor, simplifies construction of entry when only
     * one extension and one magic number is required.
     * @param name        Format Name
     * @param ext         Standard extension
     * @param offset      Offset of magic number
     * @param magicNumber byte array to match.
     */
    public AbstractImageIORegistryEntry(String name,
                                    String ext,
                                    String mimeType,
                                    int offset, byte[] magicNumber) {
        super(name, PRIORITY + 100, ext, mimeType, offset, magicNumber);
    }

    /**
     * Decode the Stream into a RenderableImage
     *
     * @param inIS The input stream that contains the image.
     * @param origURL The original URL, if any, for documentation
     *                purposes only.  This may be null.
     * @param needRawData If true the image returned should not have
     *                    any default color correction the file may
     *                    specify applied.
     */
    public Filter handleStream(InputStream inIS,
                               ParsedURL   origURL,
                               boolean     needRawData) {
        final DeferRable  dr  = new DeferRable();
        final InputStream is  = inIS;
        final String      errCode;
        final Object []   errParam;
        if (origURL != null) {
            errCode  = ERR_URL_FORMAT_UNREADABLE;
            errParam = new Object[] {getFormatName(), origURL};
        } else {
            errCode  = ERR_STREAM_FORMAT_UNREADABLE;
            errParam = new Object[] {getFormatName()};
        }

        Thread t = new Thread() {
                @Override
                public void run() {
                    Filter filt;
                    try{
                        Iterator<ImageReader> iter = ImageIO.getImageReadersByMIMEType(
                                getMimeTypes().get(0).toString());
                        if (!iter.hasNext()) {
                            throw new UnsupportedOperationException(
                                    "No image reader for "
                                        + getFormatName() + " available!");
                        }
                        ImageReader reader = iter.next();
                        ImageInputStream imageIn = ImageIO.createImageInputStream(is);
                        reader.setInput(imageIn, true);

                        int imageIndex = 0;
                        dr.setBounds(new Rectangle2D.Double
                                     (0, 0,
                                      reader.getWidth(imageIndex),
                                      reader.getHeight(imageIndex)));
                        CachableRed cr;
                        //Naive approach possibly wasting lots of memory
                        //and ignoring the gamma correction done by PNGRed :-(
                        //Matches the code used by the former JPEGRegistryEntry, though.
                        BufferedImage bi = reader.read(imageIndex);
                        cr = GraphicsUtil.wrap(bi);
                        cr = new Any2sRGBRed(cr);
                        cr = new FormatRed(cr, GraphicsUtil.sRGB_Unpre);
                        WritableRaster wr = (WritableRaster)cr.getData();
                        ColorModel cm = cr.getColorModel();
                        BufferedImage image = new BufferedImage
                            (cm, wr, cm.isAlphaPremultiplied(), null);
                        cr = GraphicsUtil.wrap(image);
                        filt = new RedRable(cr);
                    } catch (IOException ioe) {
                        // Something bad happened here...
                        filt = ImageTagRegistry.getBrokenLinkImage
                            (AbstractImageIORegistryEntry.this,
                             errCode, errParam);
                    } catch (ThreadDeath td) {
                        filt = ImageTagRegistry.getBrokenLinkImage
                            (AbstractImageIORegistryEntry.this,
                             errCode, errParam);
                        dr.setSource(filt);
                        throw td;
                    } catch (Throwable t) {
                        filt = ImageTagRegistry.getBrokenLinkImage
                            (AbstractImageIORegistryEntry.this,
                             errCode, errParam);
                    }

                    dr.setSource(filt);
                }
            };
        t.start();
        return dr;
    }

}
