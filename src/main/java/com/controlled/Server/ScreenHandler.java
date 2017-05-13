package com.controlled.Server;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.server.message.ObjectMessage;
import com.server.net.MsgType;

public class ScreenHandler extends IoHandlerAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(ScreenHandler.class);
	
	private static ScreenHandler screenHandler = null;
	
	public static ScreenHandler getInstance(){
		if(screenHandler == null){
			screenHandler = new ScreenHandler();
		}
		return screenHandler;
	}
	
	
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		// TODO Auto-generated method stub
		super.messageReceived(session, message);
		ObjectMessage objectMessage = (ObjectMessage) message;
		
		String msgType = objectMessage.getMsgType();
		if(msgType != null){
			if(msgType.equals(MsgType.MONITOR)){
				new Thread(new ScreenModule()).start();
//				ScreenModule.getInstance().start();
			}else if(msgType.equals(MsgType.STOP_MONITOR)){
				ScreenModule.getInstance().flag = false;
			}else {
				ScreenModule.getInstance().handleMessage(objectMessage);
			}
		}
		
	}
	
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		super.sessionClosed(session);
		logger.info(session.getRemoteAddress().toString()+"断开画面传输");
	}
	
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		// TODO Auto-generated method stub
		super.messageSent(session, message);
		logger.info("发送消息:"+message);
	}
	
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		super.sessionOpened(session);
		logger.info("ScreenModule连接打开");
	}
}
