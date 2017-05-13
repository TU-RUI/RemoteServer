package com.remotecontrol.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Test {

	public void test() {
		InetAddress address = getLocalIpAddress();
		System.out.println(address.getHostAddress());
	}

	public static InetAddress getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& !inetAddress.isLinkLocalAddress()) {
						String ip = inetAddress.getHostAddress();
						if (ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
							return inetAddress;
						}
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return null;
	}

	@org.junit.Test
	public void test1() {
		BufferedReader br = null;
		String cmd = "ipconfig";
		String result = "";
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			br = new BufferedReader(new InputStreamReader(p.getInputStream(),"GBK"));
			String line = null;

			while ((line = br.readLine()) != null) {
				result = result + line + "\n";
			}
			System.out.println(result);
		} catch (Exception e) {
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	@org.junit.Test
	public void test2(){
		int originImageHeight = 1080;
		int originImageWidth = 1920;
//		int originImageHeight = ComputerModel.model
//				.getScreenheight();
//		int originImageWidth = ComputerModel.model.getScreenwidth();
		double rate = (double)originImageWidth / (double)originImageHeight;
		System.out.println(rate);
	}
	
	
	
	
	
}
