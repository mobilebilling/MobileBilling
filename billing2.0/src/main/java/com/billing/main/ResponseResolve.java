package com.billing.main;

import android.os.Handler;
import android.os.Message;

import com.billing.bean.AuthInfo;
import com.billing.bean.GoodsInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.IOException;

public class ResponseResolve implements Closeable {

	private static ResponseResolve instance = null;

	public static synchronized ResponseResolve getInstance() {
		if (instance == null) {
			instance = new ResponseResolve();
		}
		return instance;
	}

	// 销毁实例
	public static void destroyInstance() {
		if (instance != null)
			instance = null;
	}

	public void AppAuth(String json_result, Handler handler) {
		if(json_result!=null){
			Logs.logE("AppAuth", json_result);
		}
		else{
			Logs.logE("AppAuth", "空");
		}
		Message msg = handler.obtainMessage();
		try {
			JSONObject json = new JSONObject(json_result);
			int status = json.getInt("status");
			msg.what = status;
			if (status == 0) {
				// 处理结果
				String authtime = json.getString("authtime");
				String errorcode = json.getString("errorcode");
				String errormessage = json.getString("errormessage");
				AuthInfo authInfo = new AuthInfo();
				authInfo.setStatus(status + "");
				authInfo.setAuthtime(authtime);
				authInfo.setErrorcode(errorcode);
				authInfo.setErrormessage(errormessage);
				msg.arg1 = Integer.parseInt(errorcode);
				msg.obj = authInfo;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.what = 1;
		}
		handler.sendMessage(msg);
	}

	public void GetPhoneNum(String json_result, Handler handler) {
		Message msg = handler.obtainMessage();
		try {
			JSONObject json = new JSONObject(json_result);
			int status = json.getInt("status");
			msg.what = status;
			if (status == 0) {
				String phonenum = "";
				String errorcode = json.getString("errorcode");
				msg.arg1 = Integer.parseInt(errorcode);
				if (errorcode.equals("0")) {
					phonenum = json.getString("phonenum");
				}
				msg.obj = phonenum;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.what = 1;
		}
		handler.sendMessage(msg);
	}

	public void UserAuth(String json_result, Handler handler) {
		Message msg = handler.obtainMessage();
		try {
			JSONObject json = new JSONObject(json_result);
			int status = json.getInt("status");
			msg.what = status;
			String errorcode = json.getString("errorcode");
			msg.arg1 = Integer.parseInt(errorcode);
			String outbill = json.getString("outbill");
			msg.obj = outbill;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.what = 1;
		}
		handler.sendMessage(msg);
	}

	public void GetGoodsInfo(String json_result, Handler handler) {
		Message msg = handler.obtainMessage();
		try {
			JSONObject json = new JSONObject(json_result);
			int status = json.getInt("status");
			msg.what = status;
			if (status == 0) {
				GoodsInfo goodsInfo = new GoodsInfo();
				goodsInfo.setAppname(json.getString("appname"));
				goodsInfo.setCharges(json.getString("charges"));
				goodsInfo.setAppprovide(json.getString("appprovide"));
				goodsInfo.setServicephonenum(json.getString("servicephonenum"));
				msg.obj = goodsInfo;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.what = 1;
		}
		handler.sendMessage(msg);
	}

	public void GetPhoneVerify(String json_result, Handler handler) {
		Message msg = handler.obtainMessage();
		try {
			JSONObject json = new JSONObject(json_result);
			int status = json.getInt("status");
			msg.what = status;
			String errorcode = json.getString("errorcode");
			msg.arg1 = Integer.parseInt(errorcode);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.what = 1;
		}
		handler.sendMessage(msg);
	}

	public void BuyGoods(String json_result, Handler handler) {
		Message msg = handler.obtainMessage();
		try {
			JSONObject json = new JSONObject(json_result);
			int status = json.getInt("status");
			msg.what = status;
			String errorcode = json.getString("errorcode");
			msg.arg1 = Integer.parseInt(errorcode);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.what = 1;
		}
		handler.sendMessage(msg);
	}

	public void update(String json_result, Handler handler) {
		Message msg = handler.obtainMessage();
		try {
			JSONObject json = new JSONObject(json_result);
			int status = json.getInt("status");
			msg.what = status;
			String errorcode = json.getString("errorcode");
			msg.arg1 = Integer.parseInt(errorcode);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.what = 1;
		}
		handler.sendMessage(msg);
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}
}
