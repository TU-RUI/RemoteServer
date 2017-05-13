package com.controlled.Server;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.controlled.Model.ComputerModel;
import com.server.message.ObjectMessage;
import com.server.net.ClientType;
import com.server.net.MsgType;
import com.server.utils.CoverFXtoAWT;
import com.server.utils.ImageCompress;
import com.server.utils.PropertiesUtil;

public class ScreenModule implements Runnable{

	private static ScreenModule screenModule = null;

	private static Polygon pointer = new Polygon(new int[] { 0, -7, 0, 7 },
			new int[] { 0, -15, -10, -15 }, 4);
	private final static Logger LOGGER = LoggerFactory
			.getLogger(ScreenModule.class);

	private static IoConnector connector;
	private static IoSession session;

	private static final String Address = PropertiesUtil.GetValueByKey("host");
	private static final int PORT = 5433;

	public boolean flag = false;

	private static int width;
	private static int height;

	private static Robot robot;

	public static ScreenModule getInstance() {
		if (screenModule == null) {
			screenModule = new ScreenModule();
		}
		return screenModule;
	}
	
	static{
		connector = new NioSocketConnector();
		connector.setHandler(ScreenHandler.getInstance());
		connector.getFilterChain().addLast("logger", new LoggingFilter());
		ObjectSerializationCodecFactory codecFactory = new ObjectSerializationCodecFactory();
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(codecFactory));
		ConnectFuture connFuture = connector.connect(new InetSocketAddress(
				Address, PORT));
		connFuture.addListener(new IoFutureListener<ConnectFuture>() {
			public void operationComplete(ConnectFuture future) {
				if (future.isConnected()) {
					LOGGER.debug("...connected");
					session = future.getSession();
					String tempid = ComputerModel.getInstance().getTempid();
					int i = 0;
					while (i++ < 3) {
						if (tempid != null && tempid.length() == 16) {
							ObjectMessage authMessage = new ObjectMessage();
							authMessage.setMsgType(MsgType.AUTH);
							authMessage.setTempid(String.valueOf(tempid));
							authMessage.setMessage(ClientType.CONTROLLED);
							session.write(authMessage);
						}
					}
				} else {
					LOGGER.error("Not connected...exiting");
				}
			}
		});
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		width = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
		height = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
		LOGGER.info("开始传送画面");
		
		
	}

	public ScreenModule() {
		screenModule = this;
		flag = true;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		ObjectMessage objectMessage;
		while (flag) {
			objectMessage = new ObjectMessage();
			objectMessage.setMsgType(MsgType.SCREEN);
			byte[] data = getScreenCap();
			if (data == null) {
				continue;
			}
			objectMessage.setImage(data);
			objectMessage.setTempid(ComputerModel.getInstance().getTempid());
			try {
				sendData(objectMessage);
				LOGGER.info("图片大小:" + objectMessage.getImage().length);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				flag = false;
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean sendData(Object message) throws Exception {
		if (!session.isConnected()) {
			return false;
		}
		session.write(message);
		return true;
	}

	/**
	 * @return
	 */
	public static byte[] getScreenCap() {

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

		try {
			ByteArrayOutputStream baos = ImageCompress.compressImage(img, 0.2f);
			byte[] image = baos.toByteArray();
			baos.close();
			return image;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void handleMessage(ObjectMessage objectMessage) {
		String msgType = objectMessage.getMsgType();
		switch (msgType) {
		case MsgType.MOUSE_MOVE:
			int x = (int) objectMessage.getX();
			int y = (int) objectMessage.getY();
			robot.mouseMove(x, y);
			break;
		case MsgType.MOUSE_PRESS:
			MouseButton button = objectMessage.getMouseButton();
			robot.mousePress(CoverFXtoAWT.getMouseCode(button));
			break;
		case MsgType.MOUSE_RELEASE:
			MouseButton button1 = objectMessage.getMouseButton();
			robot.mouseRelease(CoverFXtoAWT.getMouseCode(button1));
			break;
		case MsgType.KEY_PRESS:
			KeyCode keyCode = objectMessage.getKey();
			robot.keyPress(CoverFXtoAWT.getAWTKeyCode(keyCode));
			break;
		case MsgType.KEY_RELEASE:
			KeyCode keyCode1 = objectMessage.getKey();
			robot.keyPress(CoverFXtoAWT.getAWTKeyCode(keyCode1));
			break;
		}
	}

	public static void main(String[] args) {
		// new ScreenModule().start();
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		width = 1920;
		height = 1080;
		getScreenCap();
	}

	public void closeSession() {
		// TODO Auto-generated method stub
		session.close(false);
	}

}
