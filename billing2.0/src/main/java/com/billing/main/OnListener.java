package com.billing.main;

public interface OnListener {
	public void success(String errorcode, String phonenum);

	public void faile(String errorcode, String phonenum);

	public void cancel();
}
