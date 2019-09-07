package com.billing.bean;

import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.Serializable;

public class AppInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	public String appid;
	public String apphash;
	public String spid;
	public String channelid;
	public String imsi;
	public String imei;
	public String clientip;

	public AppInfo() {
		super();
	}

	private static AppInfo _instance;

	public static AppInfo sharedAppInfo() {
		if (_instance == null) {
			_instance = new AppInfo();
		}
		return _instance;
	}

	public void getSerializable(SharedPreferences sp) {
		if (_instance != null && TextUtils.isEmpty(_instance.appid)) {
			_instance.appid = sp.getString("Appid", "");
			_instance.apphash = sp.getString("Apphash", "");
			_instance.spid = sp.getString("Spid", "");
			_instance.channelid = sp.getString("Channelid", "");
			if ("null".equals(sp.getString("Imsi", ""))
					|| sp.getString("Imsi", "") == null) {
				_instance.imsi = "";
			}
			_instance.imsi = sp.getString("Imsi", "");
			if ("null".equals(sp.getString("imei", ""))
					|| sp.getString("imei", "") == null) {
				_instance.imei = "";
			}
			_instance.imei = sp.getString("Imei", "");
			_instance.clientip = sp.getString("Clientip", "");
		}

	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getApphash() {
		return apphash;
	}

	public void setApphash(String apphash) {
		this.apphash = apphash;
	}

	public String getSpid() {
		return spid;
	}

	public void setSpid(String spid) {
		this.spid = spid;
	}

	public String getChannelid() {
		return channelid;
	}

	public void setChannelid(String channelid) {
		this.channelid = channelid;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getClientip() {
		return clientip;
	}

	public void setClientip(String clientip) {
		this.clientip = clientip;
	}

}
