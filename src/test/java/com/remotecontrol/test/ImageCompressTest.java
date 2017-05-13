/**
 * 
 */
package com.remotecontrol.test;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.junit.Before;
import org.junit.Test;

import com.server.net.ScreenModule;

public class ImageCompressTest {

	private static int width = 1920;
	private static int height = 1080;
	private static Robot robot;
	private static Polygon pointer = new Polygon(new int[] { 0, -7, 0, 7 },
			new int[] { 0, -15, -10, -15 }, 4);

	@Before
	public void before() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void test() throws IOException {

		ScreenModule.getInstance();

		BufferedImage img = getScreenCap();
//		File file = new File("C:/Users/john/Desktop/1.jpg");
//		ImageIO.write(img, "jpg", file);
		File file1 = new File("C:/Users/john/Desktop/qwer.jpeg");
//		ImageIO.write(compress1(img), "jpeg", file1);
		ByteArrayOutputStream baos = compressImage(img,0.3f);
//		byte[] b = baos.toByteArray();
//		baos.close();
//		BufferedImage temp = ImageIO.read(new ByteArrayInputStream(b));
//		
//		img = compress1(temp);
//		ImageIO.write(img, "jpeg", file1);
		
		baos.writeTo(new FileOutputStream(file1));
	}

	public static BufferedImage getScreenCap() {

		// get the current location of the mouse
		// this is used to actually draw the mouse
		Point mousePosition = MouseInfo.getPointerInfo().getLocation();
		Rectangle captureSize = new Rectangle(0, 0, width, height);
		BufferedImage img = robot.createScreenCapture(captureSize);
		Graphics2D grfx = img.createGraphics();
		grfx.translate(mousePosition.x, mousePosition.y);
		grfx.setColor(new Color(100, 100, 255, 255));
		grfx.rotate(15);
		grfx.fillPolygon(pointer);
		grfx.setColor(Color.red);
		grfx.drawPolygon(pointer);
		grfx.dispose();
		return img;
		// ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// try {
		// ImageIO.write(img, "jpg",baos);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// return baos.toByteArray();
	}

	public static BufferedImage compress1(BufferedImage src) {
		float rate = 0.8f;
		int destWidth = (int) (width * rate);
		int destHeight = (int) (height * rate);
		BufferedImage tempImage = new BufferedImage(destWidth, destHeight,src.getType());
		Graphics2D g2D = (Graphics2D) tempImage.getGraphics();
		g2D.drawImage(src.getScaledInstance(destWidth, destHeight,
				Image.SCALE_AREA_AVERAGING), 0, 0, null);
		return tempImage;
	}
	
	
	
	private static ByteArrayOutputStream compressImage(BufferedImage image, float quality) throws IOException 
	{
		// Get a ImageWriter for jpeg format.
		Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix("jpeg");
		if (!writers.hasNext()) throw new IllegalStateException("No writers found");
		ImageWriter writer = (ImageWriter) writers.next();
		
		while(!writer.getDefaultWriteParam().canWriteCompressed() && writers.next() != null)
		{
			writer = writers.next();
		}
		
		// Create the ImageWriteParam to compress the image.
		ImageWriteParam param = writer.getDefaultWriteParam();
		
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(quality);
        ColorModel colorModel = ColorModel.getRGBdefault();  
        // 指定压缩时使用的色彩模式  
        param.setDestinationType(new javax.imageio.ImageTypeSpecifier(colorModel, colorModel  
                .createCompatibleSampleModel(16, 16)));
		// The output will be a ByteArrayOutputStream (in memory)
		ByteArrayOutputStream bos = new ByteArrayOutputStream(32768);
		ImageOutputStream ios = ImageIO.createImageOutputStream(bos);
		writer.setOutput(ios);
		writer.write(null, new IIOImage(image, null, null), param);
		ios.flush();
		return bos;
	}

}
