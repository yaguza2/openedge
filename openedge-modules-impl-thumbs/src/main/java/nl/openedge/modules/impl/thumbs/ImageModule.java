/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Open Edge B.V.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions 
 * in binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution. Neither the name of OpenEdge B.V. 
 * nor the names of its contributors may be used to endorse or promote products 
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package nl.openedge.modules.impl.thumbs;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.activation.DataSource;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.swing.ImageIcon;

import nl.openedge.modules.types.base.SingletonType;
import nl.openedge.util.ImageInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * @author Eelco Hillenius
 * Module image operations
 */
public final class ImageModule implements SingletonType
{

	/* logger */
	private Log log = LogFactory.getLog(ImageModule.class);
	/* suffix to use when caching; max size will be appended */

	/**
	 * get image info
	 * @param dataSource
	 * @return image info if type is image, null otherwise
	 */
	public ImageInfo getImageInfo(DataSource dataSource) throws IOException
	{

		ImageInfo info = null;
		if (dataSource.getContentType().toLowerCase().startsWith("image"))
		{

			info = new ImageInfo(dataSource.getInputStream());
		}
		return info;
	}
	/**
	 * get resized image instance
	 * @param is inputstream
	 * @param maxWidth
	 * @param maxHeight
	 * @param maxSize
	 * @return BufferedImage
	 * @throws IOException
	 */
	public BufferedImage getImage(InputStream is, int maxSize) 
		throws IOException
	{
		return getImage(is, maxSize, true);
	}
	/**
	 * get resized image instance
	 * @param is inputstream
	 * @param maxWidth
	 * @param maxHeight
	 * @param maxSize
	 * @param soften
	 * @return BufferedImage
	 * @throws IOException
	 */
	public BufferedImage getImage(InputStream is, int maxSize, boolean soften) 
		throws IOException
	{

		BufferedImage img = ImageIO.read(is);
		Image resizedImage = img;
		if (img == null)
		{
			is.close();
			throw new IOException("unable to read image");
		}
		int width = img.getWidth();
		int height = img.getHeight();
		
		if(width > maxSize  || height > maxSize)
		{
			if (width > height)
			{
				resizedImage =
					img.getScaledInstance(maxSize, 
						(maxSize * height) / width, Image.SCALE_SMOOTH);
			}
			else
			{
				resizedImage =
					img.getScaledInstance(
						(maxSize * width) / height, maxSize, Image.SCALE_SMOOTH);
			}
		}
		else if(!soften)
		{
			return img;
		}

		// ensure that all the pixels in the image are loaded.
		resizedImage = new ImageIcon(resizedImage).getImage();
		// Create the buffered image.
		img = new BufferedImage(resizedImage.getWidth(null), resizedImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
		// Copy image to buffered image.
		Graphics g = img.createGraphics();
		// Clear background and paint the image.
		//g.setColor(Color.white);
		//g.fillRect(0, 0, temp.getWidth(null), temp.getHeight(null));
		g.drawImage(resizedImage, 0, 0, null);
		g.dispose();
		// soften thumbnail
		if(soften)
		{
			float softenFactor = 0.05f;
			float[] softenArray =
				{
					0,
					softenFactor,
					0,
					softenFactor,
					1 - (softenFactor * 4),
					softenFactor,
					0,
					softenFactor,
					0 };
			Kernel kernel = new Kernel(3, 3, softenArray);
			ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
			img = cOp.filter(img, null);
		}
		return img;
	}

	/**
	 * write image as JPG to outputstream based on datasource with resizing
	 * @param is inputstream
	 * @param os outputstream
	 * @param maxSize max size (width or height) for resize op
	 * @throws IOException
	 */
	public boolean writeImage(InputStream is, OutputStream os, int maxSize) 
		throws IOException
	{
		return writeImage(is, os, maxSize, true);
	}
	/**
	 * write image as JPG to outputstream based on datasource with resizing
	 * @param is inputstream
	 * @param os outputstream
	 * @param maxSize max size (width or height) for resize op
	 * @throws IOException
	 */
	public boolean writeImage(InputStream is, OutputStream os, int maxSize, boolean soften) 
		throws IOException
	{

		BufferedImage img = getImage(is, maxSize,soften);
		return internalWriteImage(img, os);
	}

	/**
	 * write image as JPG to outputstream based on datasource without resizing
	 * @param is inputstream
	 * @param os outputstream
	 * @throws IOException
	 */
	public boolean writeImage(InputStream is, OutputStream os) throws IOException
	{

		BufferedImage img = ImageIO.read(is);
		return internalWriteImage(img, os);
	}

	/* write */
	private boolean internalWriteImage(BufferedImage img, OutputStream os) 
		throws IOException
	{

		if (img == null)
		{
			return false;
		}
		Iterator it = ImageIO.getImageWritersByMIMEType("image/jpeg");
		if(it.hasNext())
		{
			ImageWriter ir = (ImageWriter) it.next();
			ir.setOutput(ImageIO.createImageOutputStream(os));
			ir.write(img);
			ir.dispose();
		}
		else
		{
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(img);
			param.setQuality(.75f, false);
			encoder.setJPEGEncodeParam(param);
			encoder.encode(img);
		}
		return true;
	}

}
