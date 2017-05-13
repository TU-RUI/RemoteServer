package com.controlled.Model;

public class ComputerModel {
	private static ComputerModel model = null;
	
	public static ComputerModel getInstance(){
		
		if(model == null){
			model = new ComputerModel();
		}
		return model;
	}
	
	private ComputerModel(){
		
	}
	
	private String username;
	private String password;
	private String tempid;
	private String screenwidth;
	private String screenheight;
	private String os;
	private String devip;

	public String getScreenwidth() {
		return screenwidth;
	}
	public void setScreenwidth(String screenwidth) {
		this.screenwidth = screenwidth;
	}
	public String getScreenheight() {
		return screenheight;
	}
	public void setScreenheight(String screenheight) {
		this.screenheight = screenheight;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String getDevip() {
		return devip;
	}
	public void setDevip(String devip) {
		this.devip = devip;
	}

	public String getTempid() {
		return tempid;
	}

	public void setTempid(String tempid) {
		this.tempid = tempid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
