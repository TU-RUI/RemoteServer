package com.controlled.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.controlled.Model.ComputerModel;
import com.controlled.windows.LoginWindow;
import com.server.net.ClientType;
import com.server.net.MsgType;

public class BusinessHandler extends IoHandlerAdapter {

	private final static Logger logger = LoggerFactory
			.getLogger(BusinessHandler.class);

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		if (message instanceof String) {
			String msg = message.toString();
			logger.info(msg);
			JSONObject json = JSON.parseObject(msg);
			if (json.containsKey("msgType")) {
				String type = json.getString("msgType");
				switch (type) {
				// 登陆结果
				case MsgType.LOGIN_RESULT:
					String login_result = json.getString(MsgType.LOGIN_RESULT);
					if (login_result.equals(MsgType.LOGIN_SUCCESS)) {
						// 登陆成功
						ComputerModel.getInstance().setTempid(
								json.getString("tempid"));
						ComputerModel.getInstance().setUsername(
								LoginWindow.username);
						ComputerModel.getInstance().setPassword(
								LoginWindow.password);
						LoginWindow.jump();
					} else if (login_result.equals(MsgType.WRONGPASSWORD)) {
						// 密码错误
						LoginWindow.informationDialog(null, "密码错误");
					} else if (login_result.equals(MsgType.NOSUCHUSER)) {
						// 没有该用户
						LoginWindow.informationDialog(null, "没有该用户");
					}
					break;
				case MsgType.REGIST_RESULT:
					String regist_result = json
							.getString(MsgType.REGIST_RESULT);
					if (regist_result.equals(MsgType.REGIST_SUCCESS)) {
						// 注册成功
						LoginWindow.informationDialog(null, "注册成功");
					} else if (regist_result.equals(MsgType.EMAIL_REGISTERED)) {
						// 邮箱已被注册
						LoginWindow.informationDialog(null, "邮箱已被注册");
					} else if (regist_result.equals(MsgType.DUPLICATENAME)) {
						// 用户名重复
						LoginWindow.informationDialog(null, "用户名已被占用");
					}
					break;
				case MsgType.CMD:
					System.out.println("收到cmd命令:"+message);
					String tempid = json.getString("tempid");
					String cmd = json.getString("message");
					JSONObject resultjson = new JSONObject();

					BufferedReader  br = null;
					try {
						Process p = Runtime.getRuntime().exec(cmd);
						br = new BufferedReader(new InputStreamReader(
								p.getInputStream(),"GBK"));
						String line = null;
						String result = "";
						while ((line = br.readLine()) != null) {
							result = result + line + "\n";
						}						
						resultjson.put("message",result);
					} catch (Exception e) {
						resultjson.put("message", "命令执行异常");
					}finally{
						if(br != null){
							br.close();
						}
					}
					
					resultjson.put("client", ClientType.CONTROLLED);
					resultjson.put("tempid", tempid);
					resultjson.put("msgType", MsgType.CMD);
					session.write(resultjson.toJSONString());

					break;
				case MsgType.CHANGEPWD_RESULT:
					break;
				case MsgType.RESTPWD_RESULT:
					break;
					
				}
			}
		}

	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		super.sessionOpened(session);
		// logger.info("ServerModule连接打开");
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		super.sessionClosed(session);
		logger.info(session.getRemoteAddress().toString() + "连接断开");
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		// TODO Auto-generated method stub
		super.messageSent(session, message);
		logger.info("发送消息:" + message);
	}
}
