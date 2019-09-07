package com.billing.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.EditText;

@SuppressLint("AppCompatCustomView")
public class MyEditText extends EditText {
	public MyEditText(Context context) {
		super(context);
		int strokeWidth = 2; // 边框线粗
		int roundRadius = 2; // 弧度
		int strokeColor = Color.parseColor("#000000");// 边框颜色
		int fillColor = Color.parseColor("#FFFFFF");// 内部填充颜色
		GradientDrawable gd = new GradientDrawable();// 创建drawable
		gd.setColor(fillColor);
		gd.setCornerRadius(roundRadius);
		gd.setStroke(strokeWidth, strokeColor);
		this.setBackgroundDrawable(gd);
	}

}
