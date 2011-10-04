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
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.swing.ImageIcon;

import nl.openedge.modules.types.base.SingletonType;
import nl.openedge.util.ImageInfo;

/**
 * Module image operations
 * 
 * @author Eelco Hillenius
 */
public final class ImageModule implements SingletonType
{
	/**
	 * get image info
	 * 
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
	 */
	public BufferedImage getImage(InputStream is, int maxSize) throws IOException
	{
		return getImage(is, maxSize, true);
	}

	/**
	 * get resized image instance
	 */
	public BufferedImage getImage(InputStream is, int maxSize, boolean soften) throws IOException
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

		if (width > maxSize || height > maxSize)
		{
			if (width > height)
			{
				resizedImage =
					img.getScaledInstance(maxSize, (maxSize * height) / width, Image.SCALE_SMOOTH);
			}
			else
			{
				resizedImage =
					img.getScaledInstance((maxSize * width) / height, maxSize, Image.SCALE_SMOOTH);
			}
		}
		else if (!soften)
		{
			return img;
		}

		// ensure that all the pixels in the image are loaded.
		resizedImage = new ImageIcon(resizedImage).getImage();
		// Create the buffered image.
		img =
			new BufferedImage(resizedImage.getWidth(null), resizedImage.getHeight(null),
				BufferedImage.TYPE_INT_RGB);
		// Copy image to buffered image.
		Graphics g = img.createGraphics();
		// Clear background and paint the image.
		// g.setColor(Color.white);
		// g.fillRect(0, 0, temp.getWidth(null), temp.getHeight(null));
		g.drawImage(resizedImage, 0, 0, null);
		g.dispose();
		// soften thumbnail
		if (soften)
		{
			float softenFactor = 0.05f;
			float[] softenArray =
				{0, softenFactor, 0, softenFactor, 1 - (softenFactor * 4), softenFactor, 0,
					softenFactor, 0};
			Kernel kernel = new Kernel(3, 3, softenArray);
			ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
			img = cOp.filter(img, null);
		}
		return img;
	}

	/**
	 * write image as JPG to outputstream based on datasource with resizing
	 */
	public boolean writeImage(InputStream is, OutputStream os, int maxSize) throws IOException
	{
		return writeImage(is, os, maxSize, true);
	}

	/**
	 * write image as JPG to outputstream based on datasource with resizing
	 */
	public boolean writeImage(InputStream is, OutputStream os, int maxSize, boolean soften)
			throws IOException
	{

		BufferedImage img = getImage(is, maxSize, soften);
		return internalWriteImage(img, os);
	}

	/**
	 * write image as JPG to outputstream based on datasource without resizing
	 */
	public boolean writeImage(InputStream is, OutputStream os) throws IOException
	{

		BufferedImage img = ImageIO.read(is);
		return internalWriteImage(img, os);
	}

	/* write */
	private boolean internalWriteImage(BufferedImage img, OutputStream os) throws IOException
	{
		if (img == null)
			return false;
		Iterator<ImageWriter> iter = ImageIO.getImageWritersByMIMEType("image/jpeg");
		if (iter.hasNext())
		{
			ImageWriter writer = iter.next();
			ImageWriteParam iwp = writer.getDefaultWriteParam();
			iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwp.setCompressionQuality(.75f); // an integer between 0 and 1
			writer.setOutput(ImageIO.createImageOutputStream(os));
			writer.write(img);
			writer.dispose();
		}
		else
		{
			throw new RuntimeException("geen jpeg encoder beschikbaar");
		}
		return true;
	}
}
