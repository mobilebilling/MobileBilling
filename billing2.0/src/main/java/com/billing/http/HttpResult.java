package com.billing.http;

import java.io.Serializable;

//实现可序列化接口，用intent来传数据 

public class HttpResult implements Serializable {
	private int status;
	private String errorcode;
	private String errorMsg;
	private String sendSMS;

	private Object resultObject;

	private int intDate;

	public String getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Object getResultObject() {
		return resultObject;
	}

	public void setResultObject(Object resultObject) {
		this.resultObject = resultObject;
	}

	public String getSendSMS() {
		return sendSMS;
	}

	public void setSendSMS(String sendSMS) {
		this.sendSMS = sendSMS;
	}

	public int getIntDate() {
		return intDate;
	}

	public void setIntDate(int intDate) {
		this.intDate = intDate;
	}

}
