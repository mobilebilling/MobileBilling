package com.billing.main;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressBar {

	private ProgressDialog mProgressDialog;

	public void showProgressBar(Context context, String message) {
		mProgressDialog = new ProgressDialog(context);
		if (!mProgressDialog.isShowing()) {
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setMessage(message);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}

	}

	public void closeProgressBar() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}
}
