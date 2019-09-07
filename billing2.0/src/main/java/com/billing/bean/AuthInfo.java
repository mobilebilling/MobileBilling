package com.billing.bean;

public class AuthInfo {
	public String status;
	public String authtime;
	public String errorcode;
	public String errormessage;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAuthtime() {
		return authtime;
	}

	public void setAuthtime(String authtime) {
		this.authtime = authtime;
	}

	public String getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}

	public String getErrormessage() {
		return errormessage;
	}

	public void setErrormessage(String errormessage) {
		this.errormessage = errormessage;
	}

	// public static AuthInfo getAuthInfo() {
	// if (instance == null) {
	//
	//
	//
	// instance = new AuthInfo();
	//
	// }
	// return instance;
	// }

}
