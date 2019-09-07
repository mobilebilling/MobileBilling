package com.billing.main;

import android.content.Context;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileUtils {
	private String TAG = "ssssss";
	private String path = "libapi_sdk.so";

	public int saveso(Context context) {
		try {
			String localPath = Environment.getExternalStorageDirectory()
					+ "/sdkdownload/" + path;

			File inFile = new File(localPath);
			// 判断需加载的文件是否存在
			if (!inFile.exists()) {
				// 文件不存在
				return -1;
			}
			FileInputStream fis = new FileInputStream(inFile);
			File dir = context.getDir("libs", Context.MODE_PRIVATE);
			// 获取驱动文件输出流
			File soFile = new File(dir, path);
			if (soFile.exists()) {
				soFile.delete();
				Logs.logE(TAG, "文件存在,删除从新导入");
			}
			FileOutputStream fos = new FileOutputStream(soFile);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = fis.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			// 从内存到写入到具体文件
			fos.write(baos.toByteArray());
			// 关闭文件流
			baos.close();
			fos.close();
			fis.close();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
}
