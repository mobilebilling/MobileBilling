package com.billing.main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.WindowManager;

import com.billing.bean.AppInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Random;

public class Util {
    private static Random randGen = null;

    private static char[] numbersAndLetters = null;

    private static SharedPreferences sp;

    // private String SMS_SEND_ACTIOIN = "SMS_SEND_ACTIOIN";
    // private String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";

    public static int getScreenWidth(Context context) {

        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        android.view.Display display = wm.getDefaultDisplay();
        return display.getWidth();
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        android.view.Display display = wm.getDefaultDisplay();
        return display.getHeight();
    }

    public static final String randomString(int length) {
        if (length < 1) {
            return null;
        }
        if (randGen == null) {
            randGen = new Random();
            numbersAndLetters = ("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ")
                    .toCharArray();
        }
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(35)];
        }
        return new String(randBuffer);
    }

    public static void callno(Context context, String phone_number) {

        phone_number = phone_number.trim();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 1);
            return;
        }
        if (phone_number != null && !phone_number.equals("")) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                    + phone_number));
            context.startActivity(intent);
        }
    }

    public static String getAppHash(Context context) {
        String path = context.getPackageResourcePath();
        File file = new File(path);
        try {
            return Util.Encrypt(Util.getByte(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String Encrypt(byte[] bt) {
        MessageDigest md = null;
        String strDes = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(bt);
            strDes = bytes2Hex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return strDes;
    }

    public static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }

    public static byte[] getByte(File file) throws Exception {
        byte[] bytes = null;
        if (file != null) {
            InputStream is = new FileInputStream(file);
            int length = (int) file.length();
            if (length > 50 * 1024 * 1024) // 当文件的长度超过了int的最大值
            {
                length = 50 * 1024 * 1024;
            }
            bytes = new byte[length];
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
            // 如果得到的字节长度和file实际的长度不一致就可能出错了
            if (offset < bytes.length) {
                System.out.println("file length is error");
                return null;
            }
            is.close();
        }
        return bytes;
    }

    public static void sendmes(Context context, String channelid) {
        sp = context.getSharedPreferences("SMSsend", context.MODE_PRIVATE);
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);

            return;
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.SEND_SMS}, 1);
            return;
        }
        String deviceid = tm.getDeviceId() + "";// 获取智能设备唯一编号
        String imsi = tm.getSubscriberId() + "";// 得到用户Id
        String dev = sp.getString("Deviceid", "") + "";
        String im = sp.getString("Imsi", "") + "";
        String ch = sp.getString("Channelid", "") + "";
        Logs.logE("deviceid", deviceid);
        Logs.logE("dev值", dev);
        Logs.logE("im", im);
        Logs.logE("ch值", ch);
        if (!deviceid.equals(dev) || !imsi.equals(im) || !channelid.equals(ch)) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(Config.DOWNSIDE_MES_NO, null, deviceid
                    + "," + imsi + "," + channelid, null, null);

            Editor edit = sp.edit();
            edit.putString("Deviceid", deviceid);
            edit.putString("Imsi", imsi);
            edit.putString("Channelid", channelid);
            edit.commit();
        }

    }

    public static String[] phoneinfo(Context context) {
        String phoneinfo[] = new String[3];
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return phoneinfo;
        }
        String phonenum = tm.getLine1Number();
        if (phonenum == null) {
            phonenum = "";
            Logs.logE("Util", phonenum);
        }
        String DeviceId = tm.getDeviceId();
        if (DeviceId == null) {
            DeviceId = "";
            Logs.logE("Util", DeviceId);
        }
        String Subscriberid = tm.getSubscriberId();
        if (Subscriberid == null) {
            Subscriberid = "";
            Logs.logE("Util", Subscriberid);
        }
        phoneinfo[0] = phonenum;// 获取本机号码
        phoneinfo[1] = DeviceId;// 获取智能设备唯一编号
        phoneinfo[2] = Subscriberid;// 得到用户Id
        return phoneinfo;
    }

    public static String phoneformat(String phonenum) {
        if (phonenum == null || "".equals(phonenum)) {
            return null;
        }
        if (phonenum.length() == 13) {
            phonenum = phonenum.substring(2, 13);
            Logs.logE("phonenum", phonenum);
        }
        if (phonenum.length() == 14) {
            phonenum = phonenum.substring(3, 14);
        }
        return phonenum;

    }

    public static void saveAppInfo(Context context, AppInfo appInfo) {

        AppInfo appinfo = AppInfo.sharedAppInfo();
        sp = context.getSharedPreferences("billing", context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putString("Appid", appInfo.getAppid() + "");
        edit.putString("Apphash", appInfo.getApphash() + "");
        edit.putString("Spid", appInfo.getSpid() + "");
        edit.putString("Channelid", appInfo.getChannelid() + "");
        edit.putString("Imsi", appInfo.getImsi() + "");
        edit.putString("Imei", appInfo.getImei() + "");
        edit.putString("Clientip", appInfo.getClientip() + "");
        edit.commit();
        appinfo.getSerializable(sp);
    }

    public static String getIP() {
        String IP = null;
        StringBuilder IPStringBuilder = new StringBuilder();
        try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface
                    .getNetworkInterfaces();
            while (networkInterfaceEnumeration.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaceEnumeration
                        .nextElement();
                Enumeration<InetAddress> inetAddressEnumeration = networkInterface
                        .getInetAddresses();
                while (inetAddressEnumeration.hasMoreElements()) {
                    InetAddress inetAddress = inetAddressEnumeration
                            .nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && !inetAddress.isLinkLocalAddress()
                            && inetAddress.isSiteLocalAddress()) {
                        IPStringBuilder.append("_"
                                + inetAddress.getHostAddress().toString());
                    }
                }
            }
        } catch (SocketException ex) {

        }

        IP = IPStringBuilder.toString();
        if (IP == null) {
            IP = "";
        }
        return IP;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static byte[] b64Decode(String str) {
        byte[] bt = null;
        try {
            bt = android.util.Base64.decode(str, android.util.Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bt;
    }

    public static int loadSystemSo(Context context) {
        File dir = context.getDir("libs", Context.MODE_PRIVATE);
        File soFile = new File(dir, "libapi_sdk.so");
        if (soFile.exists()) {
            System.load(soFile.getAbsolutePath());
            return 0;
        } else {
            return -1;
        }
    }
}
