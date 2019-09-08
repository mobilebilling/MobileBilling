package com.billing.main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.billing.bean.AppInfo;
import com.billing.bean.AuthInfo;
import com.billing.bean.GoodsInfo;
import com.billing.http.ConstantsInfo;
import com.billing.runnable.BuyGoodsRunnable;
import com.billing.runnable.ContentAuthRunnable;
import com.billing.runnable.GetGoodsInfoRunnable;
import com.billing.runnable.GetPhoneNumRunnable;
import com.billing.runnable.GetPhoneVerifyRunnable;
import com.billing.runnable.GetSPPhoneNumRunnable;
import com.billing.runnable.SaveVerificationRunnable;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

public class Billing {
    private Context context;
    private OnListener onListener;
    private PhoneNumListener phoneNumListener;
    private Dialog dialog;
    private static Billing instance = null;
    private Handler timehandler = new Handler();
    private ProgressBar progressBar;
    private int action = 0;
    private int SHOWTYPE = 0;
    private String phonenum = "";
    private AuthInfo authInfo = null;
    private String httpUrl = "";
    private String channelid = "";
    private String billingid = "";
    private String productid = "";
    private String product = "";
    private String phoneverify = "";
    private GoodsInfo goodsInfo;
    private EditText phonenumedt;
    private Button validationbtn;
    private boolean GetPhoneValiadtion = true;
    private int showtime = Toast.LENGTH_LONG;
    private int color = 0xFF989997;
    private boolean isupdatedone = false;
    private boolean isClickBuy = false;
    private boolean isSendsmsOver = false;
    private GetSPPhoneNumRunnable getSPPhoneNumRunnable;
    private BuyGoodsRunnable buyGoodsRunnable;
    private ContentAuthRunnable contentAuthRunnable;
    private GetGoodsInfoRunnable getGoodsInfoRunnable;
    private GetPhoneNumRunnable getPhoneNumRunnable;
    private GetPhoneVerifyRunnable getPhoneVerifyRunnable;


    private Handler appAuthhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (!isClickBuy) {
                        authInfo = new AuthInfo();
                        authInfo = (AuthInfo) msg.obj;
                    }
                    break;
                case 1:
                    break;
                case 2:
                    break;
            }
        }
    };
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (action == Config.AppAuthAction) {
                switch (msg.what) {
                    case 0:
                        authInfo = new AuthInfo();
                        authInfo = (AuthInfo) msg.obj;
                        buyorder();
                        break;
                    case 1:
                        if (progressBar != null) {
                            progressBar.closeProgressBar();
                        }
                        onListener.faile("400001", "");
                        break;
                    case 2:
                        if (progressBar != null) {
                            progressBar.closeProgressBar();
                        }
                        Toast.makeText(context, "网络连接有问题,请查看网络!", showtime).show();
                        onListener.faile("300001", "");
                        break;
                }
            } else if (action == Config.GetPhoneNumAction) {
                switch (msg.what) {
                    case 0:
                        if (msg.arg1 == 0) {
                            phonenum = (String) msg.obj;
                            ContentAuth();
                        } else {
                            SHOWTYPE = 2;
                            GetGoodsInfo();
                        }
                        break;
                    case 1:
                        progressBar.closeProgressBar();
                        onListener.faile("400001", "");
                        break;
                    case 2:
                        progressBar.closeProgressBar();
                        Toast.makeText(context, "网络连接有问题,请查看网络!", showtime).show();
                        onListener.faile("300001", "");
                        break;
                }
            } else if (action == Config.UserAuthAction) {
                switch (msg.what) {
                    case 0:
                        if (msg.arg1 == 0) {
                            String outbill = (String) msg.obj;
                            if (outbill.equals("1")) {
                                SHOWTYPE = 1;
                            } else {
                                SHOWTYPE = 0;
                            }
                            GetGoodsInfo();
                            return;
                        } else {
                            Toast.makeText(context,
                                    ConstantsInfo.errortoast.get(msg.arg1 + ""),
                                    showtime).show();
                        }
                        progressBar.closeProgressBar();
                        onListener.faile(msg.arg1 + "", phonenum + "");
                        break;
                    case 1:
                        progressBar.closeProgressBar();
                        onListener.faile("400001", phonenum + "");
                        break;
                    case 2:
                        progressBar.closeProgressBar();
                        Toast.makeText(context, "网络连接有问题,请查看网络!", showtime).show();
                        onListener.faile("300001", phonenum + "");
                        break;
                }

            } else if (action == Config.GetGoodsInfoAction) {
                switch (msg.what) {
                    case 0:
                        progressBar.closeProgressBar();
                        if (msg.arg1 == 0) {
                            if (msg.obj != null) {
                                goodsInfo = (GoodsInfo) msg.obj;
                                show();
                            }
                        } else {
                            progressBar.closeProgressBar();
                            if (null == phonenum) {
                                phonenum = "";
                            }
                            onListener.faile(msg.arg1 + "", phonenum + "");
                        }
                        break;
                    case 1:
                        progressBar.closeProgressBar();
                        if (null == phonenum) {
                            phonenum = "";
                        }
                        onListener.faile("400001", phonenum + "");
                        break;
                    case 2:
                        progressBar.closeProgressBar();
                        Toast.makeText(context, "网络连接有问题,请查看网络!", showtime).show();
                        if (null == phonenum) {
                            phonenum = "";
                        }
                        onListener.faile("300001", phonenum + "");
                        break;
                }

            } else if (action == Config.GetPhoneVerifyAction) {
                switch (msg.what) {
                    case 0:
                        progressBar.closeProgressBar();
                        if (msg.arg1 == 0) {
                            validationbtn.setBackgroundColor(0xFF989997);
                            TimeDownRun downRun = new TimeDownRun(validationbtn,
                                    phonenumedt, timehandler);
                            timehandler.postDelayed(downRun, 1000);
                            Toast.makeText(context, "验证码获取成功，请查收！", showtime)
                                    .show();
                        } else {
                            Toast.makeText(context,
                                    ConstantsInfo.errortoast.get(msg.arg1 + ""),
                                    showtime).show();
                        }

                        break;
                    case 1:
                        progressBar.closeProgressBar();
                        break;
                    case 2:
                        progressBar.closeProgressBar();
                        Toast.makeText(context, "网络连接有问题,请查看网络!", showtime).show();
                        break;
                }

            }
            // 购买成功调用回调
            else if (action == Config.BuyGoodsAction) {
                switch (msg.what) {
                    case 0:
                        if (msg.arg1 == 0) {
                            progressBar.closeProgressBar();
                            dialog.dismiss();
                            Toast.makeText(context, "购买成功！", showtime).show();
                            onListener.success(msg.arg1 + "", phonenum + "");
                        } else {
                            progressBar.closeProgressBar();
                            Toast.makeText(context,
                                    ConstantsInfo.errortoast.get(msg.arg1 + ""),
                                    showtime).show();
                        }
                        break;
                    case 1:
                        progressBar.closeProgressBar();
                        dialog.dismiss();
                        onListener.faile("400001", phonenum + "");
                        break;
                    case 2:
                        progressBar.closeProgressBar();
                        Toast.makeText(context, "网络连接有问题,请查看网络!", showtime).show();
                        break;
                }

            } else if (action == Config.SPGetPhoneNumAction) {
                switch (msg.what) {
                    case 0:
                        if (msg.arg1 == 0) {
                            phonenum = (String) msg.obj;
                            progressBar.closeProgressBar();
                            phoneNumListener.success(phonenum + "");
                        } else {
                            progressBar.closeProgressBar();
                            phoneNumListener.faile(msg.arg1 + "");
                        }
                        break;
                    case 1:
                        progressBar.closeProgressBar();
                        phoneNumListener.faile("400001");
                        break;
                    case 2:
                        progressBar.closeProgressBar();
                        phoneNumListener.faile("300001");
                        break;
                }
            }
        }
    };

    public void setPhoneNum(String phoneNum) {
        this.phonenum = phoneNum;
    }

    @SuppressLint("HandlerLeak")
    private Handler sendReqHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ResponseResolve responseResolve = ResponseResolve.getInstance();
            String msgStr = (String) msg.obj;
            if (msgStr == null || "".equals(msgStr)) {
                switch (msg.what) {
                    case Config.UpdateSoAction:
                        msg.what = 2;
                        handler.sendMessage(msg);
                        break;
                    case 0:
                        authInfo = null;
                        msg.what = 2;
                        appAuthhandler.sendMessage(msg);
                        break;
                    case Config.AppAuthAction:
                        authInfo = null;
                        msg.what = 2;
                        handler.sendMessage(msg);
                        break;
                    case Config.GetPhoneNumAction:
                        msg.what = 2;
                        handler.sendMessage(msg);
                        break;
                    case Config.UserAuthAction:
                        msg.what = 2;
                        handler.sendMessage(msg);
                        break;
                    case Config.GetPhoneVerifyAction:
                        msg.what = 2;
                        handler.sendMessage(msg);
                        break;
                    case Config.GetGoodsInfoAction:
                        msg.what = 2;
                        handler.sendMessage(msg);
                        break;
                    case Config.BuyGoodsAction:
                        msg.what = 2;
                        handler.sendMessage(msg);
                        break;
                    case Config.SPGetPhoneNumAction:
                        msg.what = 2;
                        handler.sendMessage(msg);
                        break;
                }

            } else {
                String resultString = "";
                try {
                    resultString = new String(Util.b64Decode(msgStr.toString()),
                            "GBK");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                switch (msg.what) {
                    case 0:
                        if (!isClickBuy) {
                            responseResolve.AppAuth(resultString, appAuthhandler);
                        }
                        break;
                    case Config.AppAuthAction:
                        responseResolve.AppAuth(resultString, handler);
                        break;
                    case Config.GetPhoneNumAction:
                        responseResolve.GetPhoneNum(resultString, handler);
                        break;
                    case Config.UserAuthAction:
                        responseResolve.UserAuth(resultString, handler);
                        break;
                    case Config.GetPhoneVerifyAction:
                        responseResolve.GetPhoneVerify(resultString, handler);
                        break;
                    case Config.GetGoodsInfoAction:
                        responseResolve.GetGoodsInfo(resultString, handler);
                        break;
                    case Config.BuyGoodsAction:
                        responseResolve.BuyGoods(resultString, handler);
                        break;
                    case Config.SPGetPhoneNumAction:
                        responseResolve.GetPhoneNum(resultString, handler);
                        break;
                }

            }
        }
    };


    public static synchronized Billing getInstance() {
        if (instance == null) {
            instance = new Billing();

        }
        return instance;
    }

    public void init(Context context, String httpUrl, String appid,
                     String spid, String channelid) {
        this.context = context;
        this.channelid = channelid;
        this.httpUrl = httpUrl;
        if ("".equals(phonenum)) {
            phonenum = Util.phoneinfo(context)[0];
            phonenum = Util.phoneformat(phonenum);
        }
        AppInfo appInfo = new AppInfo();
        appInfo.setAppid(appid);
        //	appInfo.setApphash(Util.getAppHash(context));
        appInfo.setApphash("5eabd88ecbfe90ff5b69715b19efb52e0c839f3d20add4b8084cf66959bad4b4");
        appInfo.setSpid(spid);
        appInfo.setChannelid(channelid);
        appInfo.setImsi(Util.phoneinfo(context)[2]);
        appInfo.setImei(Util.phoneinfo(context)[1]);
        appInfo.setClientip(Util.getIP());
        Util.saveAppInfo(context, appInfo);
        if (phonenum == null || "".equals(phonenum)) {
            Util.sendmes(context, channelid);
            delsms(context);
        }
    }

    public void GETPhoneNum(Context context) {
        this.context = context;
        if (progressBar == null) {
            progressBar = new ProgressBar();
        }
        progressBar.showProgressBar(context, "请稍候...");
        if (phonenum == null && "".equals(phonenum)) {
            phonenum = Util.phoneinfo(context)[0];
            phonenum = Util.phoneformat(phonenum);
        }
        if (phonenum != null && !"".equals(phonenum)) {
            progressBar.closeProgressBar();
            phoneNumListener.success(phonenum + "");
        } else {
            SPGetPhoneNum();
        }

    }

    public void show() {
        try {

            BillingView view = new BillingView(context, SHOWTYPE);
            ScrollView scrollView = new ScrollView(context);
            scrollView.addView(view);
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false);
            int width = Util.getScreenWidth(context);
            int height = Util.getScreenHeight(context);
            if (width > height) {
                width = height;
            }
            dialog.setContentView(scrollView,
                    new LayoutParams(width - Util.dip2px(context, 20),
                            LinearLayout.LayoutParams.WRAP_CONTENT));
            if (dialog != null && !dialog.isShowing()) {
                dialog.show();
            }
        } catch (Exception e) {
            if (progressBar != null) {
                progressBar.closeProgressBar();
            }
            onListener.faile("300001", phonenum + "");
        }
    }

    public void setBillingListener(OnListener onListener) {
        this.onListener = onListener;
    }

    public void setPhoneNumListener(PhoneNumListener phoneNumListener) {
        this.phoneNumListener = phoneNumListener;
    }

    // 销毁实例
    public static void destroyInstance() {
        if (instance != null) {
            instance = null;
        }
    }

    public void order(String httpUrl, Context context, String billingid,
                      String productid, String product, int color) {
        this.httpUrl = httpUrl;
        this.context = context;
        this.billingid = billingid;
        this.productid = productid;
        this.product = product;
        this.color = color;
        isClickBuy = true;
        if ("".equals(phonenum)) {
            phonenum = Util.phoneinfo(context)[0];
            phonenum = Util.phoneformat(phonenum);
        }
        if (progressBar == null) {
            progressBar = new ProgressBar();
        }
        progressBar.showProgressBar(context, "请稍候...");
        buyorder();

    }

    private void buyorder() {
        if (phonenum == null || "".equals(phonenum)) {
            GetPhoneNum();
        } else {
            ContentAuth();
        }
    }

    private void GetAppAuth() {
        action = Config.AppAuthAction;
    }

    private void GetPhoneNum() {
        action = Config.GetPhoneNumAction;
        getPhoneNumRunnable = new GetPhoneNumRunnable(httpUrl, sendReqHandler, getPhonenum());
        Thread thread = new Thread(getPhoneNumRunnable);
        thread.start();
    }

    private void ContentAuth() {
        action = Config.UserAuthAction;

        contentAuthRunnable = new ContentAuthRunnable(httpUrl, sendReqHandler, contentAuth());
        Thread thread = new Thread(contentAuthRunnable);
        thread.start();

    }

    private void GetPhoneVerify(String phone) {
        action = Config.GetPhoneVerifyAction;
        getPhoneVerifyRunnable = new GetPhoneVerifyRunnable(httpUrl, sendReqHandler, getPhoneverify(phone));
        Thread thread = new Thread(getPhoneVerifyRunnable);
        thread.start();
    }

    private void GetGoodsInfo() {
        action = Config.GetGoodsInfoAction;
        getGoodsInfoRunnable = new GetGoodsInfoRunnable(httpUrl, sendReqHandler, getGoodsInfo());
        Thread thread = new Thread(getGoodsInfoRunnable);
        thread.start();

    }

    private void BuyGoods() {
        action = Config.BuyGoodsAction;
        buyGoodsRunnable = new BuyGoodsRunnable(httpUrl, sendReqHandler, buyGoods());
        Thread thread = new Thread(buyGoodsRunnable);
        thread.start();
    }

    private void SPGetPhoneNum() {
        action = Config.SPGetPhoneNumAction;
        getSPPhoneNumRunnable = new GetSPPhoneNumRunnable(httpUrl, sendReqHandler, getSPPhoneNum());
        Thread thread = new Thread(getPhoneNumRunnable);
        thread.start();
    }


    class BillingView extends LinearLayout {

        private TextView phonenumtxt;// 手机号
        private TextView pricetxt;// 费用
        private TextView appnametxt;// 应用名称
        private TextView providetxt;// 提供商
        private TextView servicenumtxt;
        private ImageView validationimg;
        private MyEditText imgedt;
        private MyEditText validationedt;// 验证码
        private String ervi;// 客户端验证码
        private TextView titleview;
        private Button comitbtn;
        private Button cancelbtn;

        private boolean buy = false;

        @SuppressLint("ResourceType")
        public BillingView(Context c, final int style) {
            super(c);
            setOrientation(LinearLayout.VERTICAL);
            setBackgroundColor(0xFFFFFFFF);
            phonenumtxt = new TextView(c);
            pricetxt = new TextView(c);
            providetxt = new TextView(c);
            appnametxt = new TextView(c);
            servicenumtxt = new TextView(c);
            validationimg = new ImageView(c);
            imgedt = new MyEditText(c);
            imgedt.setHint("请输入验证码");
            imgedt.setTextSize(18);
            imgedt.setTextColor(0xFF686866);
            imgedt.setSingleLine(true);
            phonenumedt = new MyEditText(c);
            phonenumedt.setHint("请输入手机号码");
            phonenumedt.setTextSize(18);
            phonenumedt.setTextColor(0xFF686866);
            phonenumedt.setSingleLine(true);
            phonenumedt.setInputType(EditorInfo.TYPE_CLASS_PHONE);
            validationedt = new MyEditText(c);
            validationedt.setHint("请输入验证码");
            validationedt.setTextSize(18);
            validationedt.setTextColor(0xFF686866);
            validationedt.setSingleLine(true);
            validationedt.setInputType(EditorInfo.TYPE_CLASS_PHONE);
            validationbtn = new Button(c);
            validationbtn.setText("获取验证码");
            validationbtn.setTextSize(18);
            titleview = new TextView(c);
            cancelbtn = new Button(c);
            comitbtn = new Button(c);
            comitbtn.setTextSize(20);
            cancelbtn.setTextSize(20);
            phonenumtxt.setTextSize(24);
            providetxt.setTextSize(20);
            providetxt.setTextColor(0xFF686866);
            pricetxt.setTextSize(20);
            pricetxt.setTextColor(0xFF686866);
            appnametxt.setTextSize(20);
            appnametxt.setTextColor(0xFF686866);
            servicenumtxt.setTextSize(20);
            servicenumtxt.setTextColor(0xFF686866);

            RelativeLayout layouttitle = new RelativeLayout(c);
            RelativeLayout.LayoutParams layouttitleParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.FILL_PARENT, Util.dip2px(
                    context, 50));
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            titleview.setText("中国移动语音杂志");
            titleview.setLayoutParams(layoutParams);
            titleview.setTextSize(20);
            titleview.setTextColor(0xFFFFFFFF);
            layouttitle.setLayoutParams(layouttitleParams);
            layouttitle.addView(titleview);
            layouttitle.setBackgroundColor(color);
            /*
             *
             *
             * 内容布局layout
             */

            LayoutParams layout15 = new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            layout15.topMargin = 15;
            layout15.leftMargin = 15;
            phonenumtxt.setText(Html
                    .fromHtml("<font face=\"微软雅黑\" color=#686866>" + "手机号码:"
                            + "</font> " + "<font face=\"微软雅黑\" color=#0085cb>"
                            + phonenum + "</font>"));
            phonenumtxt.setLayoutParams(layout15);

            RelativeLayout validationrel = new RelativeLayout(c);
            RelativeLayout phonerel = new RelativeLayout(c);
            RelativeLayout.LayoutParams rellayout = new RelativeLayout.LayoutParams(
                    200, Util.dip2px(context, 40));
            rellayout.rightMargin = Util.dip2px(context, 10);
            rellayout.topMargin = 15;
            rellayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            validationimg.setLayoutParams(rellayout);
            validationimg.setId(2);
            RelativeLayout.LayoutParams reledt = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.FILL_PARENT, Util.dip2px(
                    context, 40));
            reledt.rightMargin = 16;
            reledt.addRule(RelativeLayout.LEFT_OF, 2);
            reledt.addRule(RelativeLayout.ALIGN_BOTTOM, 2);
            reledt.leftMargin = 15;
            imgedt.setLayoutParams(reledt);
            imgedt.setPadding(10, 0, 10, 0);
            if (style == 1) {
                validationimg.setImageBitmap(BPUtil.getInstance()
                        .createBitmap());
                ervi = BPUtil.getInstance().v;
                validationimg.setBackgroundColor(0xFF989997);
                validationimg.setScaleType(ScaleType.FIT_XY);
                comitbtn.setBackgroundColor(0xFF989997);
                validationrel.addView(validationimg);
                validationrel.addView(imgedt);
                imgedt.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        if (imgedt.getText().toString().trim().length() > 0) {
                            if (imgedt.getText().toString().trim().length() == 4) {
                                comitbtn.setBackgroundColor(0xFF0085cb);
                                buy = true;
                            } else {
                                buy = false;
                                comitbtn.setBackgroundColor(0xFF989997);
                            }
                        } else {
                            buy = false;
                            comitbtn.setBackgroundColor(0xFF989997);
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                validationimg.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        validationimg.setImageBitmap(BPUtil.getInstance()
                                .createBitmap());
                        ervi = BPUtil.getInstance().v;
                    }
                });
            }
            if (style == 0) {
                buy = true;
                comitbtn.setBackgroundColor(0xFF0085cb);
            }
            if (style == 2) {
                GetPhoneValiadtion = true;
                RelativeLayout.LayoutParams validationbtnlayout = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, Util.dip2px(
                        context, 40));
                validationbtnlayout.rightMargin = Util.dip2px(context, 10);
                validationbtnlayout.topMargin = Util.dip2px(context, 15);
                validationbtnlayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                validationbtn.setPadding(10, 0, 10, 0);
                validationbtn.setId(3);
                validationbtn.setBackgroundColor(0xFF989997);
                validationbtn.setTextColor(0xFFFFFFFF);
                validationbtn.setLayoutParams(validationbtnlayout);
                RelativeLayout.LayoutParams phonenumedtlayout = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.FILL_PARENT, Util.dip2px(
                        context, 40));
                phonenumedtlayout.rightMargin = Util.dip2px(context, 5);
                phonenumedtlayout.addRule(RelativeLayout.LEFT_OF, 3);
                phonenumedtlayout.leftMargin = Util.dip2px(context, 5);
                phonenumedtlayout.topMargin = Util.dip2px(context, 15);
                phonenumedt.setPadding(10, 0, 10, 0);
                phonenumedt.setLayoutParams(phonenumedtlayout);

                RelativeLayout.LayoutParams validationedtlayout = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.FILL_PARENT, Util.dip2px(
                        context, 40));
                validationedtlayout.addRule(RelativeLayout.BELOW, 3);
                validationedtlayout.leftMargin = Util.dip2px(context, 5);
                validationedtlayout.topMargin = Util.dip2px(context, 5);
                validationedtlayout.rightMargin = Util.dip2px(context, 10);
                validationedt.setPadding(10, 0, 10, 0);
                validationedt.setLayoutParams(validationedtlayout);
                phonerel.addView(validationbtn);
                phonerel.addView(phonenumedt);
                phonerel.addView(validationedt);
                validationbtn.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (!"".equals(phonenumedt.getText().toString().trim())
                                && phonenumedt.getText().toString().trim()
                                .length() == 11) {
                            progressBar.showProgressBar(context, "请稍等...");
                            GetPhoneVerify(phonenumedt.getText().toString()
                                    .trim());
                        }
                    }
                });
                phonenumedt.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        if (phonenumedt.getText().toString().trim().length() > 0) {
                            if (phonenumedt.getText().toString().trim()
                                    .length() == 11) {
                                if (GetPhoneValiadtion) {
                                    validationbtn
                                            .setBackgroundColor(0xFF0085cb);
                                }
                            } else {
                                validationbtn.setBackgroundColor(0xFF989997);
                            }
                        }

                        if (validationedt.getText().toString().trim().length() > 0) {
                            if (validationedt.getText().toString().trim()
                                    .length() == 6
                                    && phonenumedt.getText().toString().trim()
                                    .length() == 11) {
                                buy = true;
                                comitbtn.setBackgroundColor(0xFF0085cb);
                            } else {
                                buy = false;
                                comitbtn.setBackgroundColor(0xFF989997);
                            }
                        } else {
                            buy = false;
                            comitbtn.setBackgroundColor(0xFF989997);
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                validationedt.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        if (phonenumedt.getText().toString().trim().length() > 0) {
                            if (phonenumedt.getText().toString().trim()
                                    .length() == 11
                                    && validationedt.getText().toString()
                                    .trim().length() == 6) {
                                buy = true;
                                comitbtn.setBackgroundColor(0xFF0085cb);
                            } else {
                                buy = false;
                                comitbtn.setBackgroundColor(0xFF989997);
                            }
                        } else {
                            buy = false;
                            comitbtn.setBackgroundColor(0xFF989997);
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
            String charges = goodsInfo.getCharges();
            pricetxt.setText("资费：" + charges);
            pricetxt.setLayoutParams(layout15);
            LayoutParams layouts2 = new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            layouts2.topMargin = 2;
            layouts2.leftMargin = 15;
            String appname = goodsInfo.getAppname();
            appnametxt.setText("应用名称：" + appname);
            appnametxt.setLayoutParams(layouts2);
            String appprovide = goodsInfo.getAppprovide();
            providetxt.setText("应用提供商：" + appprovide);
            providetxt.setLayoutParams(layouts2);
            String servicephonenum = goodsInfo.getServicephonenum();
            servicenumtxt.setText(Html
                    .fromHtml("<font face=\"微软雅黑\" color=#686866>" + "客服电话:"
                            + "</font> " + "<u color=#686866>"
                            + servicephonenum + "</u>"));
            servicenumtxt.setLayoutParams(layouts2);
            servicenumtxt.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Util.callno(context, goodsInfo.getServicephonenum());
                }
            });
            LayoutParams layoutbtnparams = new LayoutParams(
                    LayoutParams.FILL_PARENT, Util.dip2px(context,
                    50), 1.0f);
            RelativeLayout.LayoutParams lineimglayout = new RelativeLayout.LayoutParams(
                    Util.dip2px(context, 1), Util.dip2px(context, 50));
            ImageView lineimg = new ImageView(c);
            lineimg.setBackgroundColor(0xFF898989);
            lineimg.setLayoutParams(lineimglayout);
            comitbtn.setText("确认支付");
            cancelbtn.setText("取消");
            cancelbtn.setTextColor(0xFFFFFFFF);
            comitbtn.setTextColor(0xFFFFFFFF);
            cancelbtn.setBackgroundColor(0xFF989997);
            cancelbtn.setLayoutParams(layoutbtnparams);
            cancelbtn.setPadding(0, 0, 0, 0);
            comitbtn.setLayoutParams(layoutbtnparams);
            comitbtn.setPadding(0, 0, 0, 0);
            LinearLayout layoutbtn = new LinearLayout(c);
            LayoutParams parambtn = new LayoutParams(
                    LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT);
            parambtn.topMargin = 15;
            layoutbtn.setLayoutParams(parambtn);
            layoutbtn.setOrientation(LinearLayout.HORIZONTAL);
            layoutbtn.addView(cancelbtn);
            layoutbtn.addView(lineimg);
            layoutbtn.addView(comitbtn);
            addView(layouttitle);
            if (style == 1 || style == 0) {
                addView(phonenumtxt, layouts2);
                addView(validationrel);
            } else if (style == 2) {
                addView(phonerel);
                comitbtn.setBackgroundColor(0xFF989997);
                comitbtn.setTextColor(0xFFFFFFFF);

            }
            addView(pricetxt);
            addView(appnametxt);
            addView(providetxt);
            addView(servicenumtxt);
            addView(layoutbtn);
            comitbtn.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (buy) {

                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            // 更改为按下时的背景图片
                            v.setBackgroundColor(0xFF006aa2);
                            comitbtn.setTextColor(0xFF003f61);
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            // 改为抬起时的图片
                            v.setBackgroundColor(0xFF0085cb);
                            comitbtn.setTextColor(0xFFFFFFFF);
                        }
                        return false;
                    }
                    return false;
                }
            });
            comitbtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (onListener != null) {
                        if (style == 1 && buy) {
                            //
                            if (ervi.toUpperCase().equals(
                                    imgedt.getText().toString().toUpperCase())) {
                                Logs.logE("成功", "成功");
                                if (ervi != null) {
                                    if (ervi.toUpperCase() != null) {
                                        SaveVerificationRunnable saveVerificationRunnable = new SaveVerificationRunnable(
                                                httpUrl, context, phonenum,
                                                phoneverify, billingid,
                                                productid, product, ervi
                                                .toUpperCase(), "0",
                                                "0");
                                        Thread thread = new Thread(
                                                saveVerificationRunnable);
                                        thread.start();
                                    }
                                }
                            } else {
                                Toast.makeText(context, "请输入正确验证码", showtime)
                                        .show();
                                Logs.logE("错误", "成功");
                                if (ervi != null) {
                                    if (ervi.toUpperCase() != null) {
                                        SaveVerificationRunnable saveVerificationRunnable = new SaveVerificationRunnable(
                                                httpUrl, context, phonenum,
                                                phoneverify, billingid,
                                                productid, product, imgedt
                                                .getText().toString(),
                                                "1", "1");
                                        Thread thread = new Thread(
                                                saveVerificationRunnable);
                                        thread.start();
                                    }
                                }
                                return;
                            }
                        }

                        if (style == 2 && buy) {
                            phonenum = phonenumedt.getText().toString().trim();
                            phoneverify = validationedt.getText().toString()
                                    .trim();
                        }
                        if (buy) {
                            progressBar.showProgressBar(context, "请稍候...");
                            BuyGoods();
                        }
                    }
                }
            });
            cancelbtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (onListener != null) {
                        dialog.dismiss();
                        onListener.cancel();
                    }
                }
            });

        }
    }

    class TimeDownRun implements Runnable {
        private Handler handler;
        private EditText editphone;
        private long time = 60;
        private Button btntime;

        public TimeDownRun(Button btntime, EditText editephone, Handler handler) {
            this.btntime = btntime;
            this.handler = handler;
            this.editphone = editephone;
            GetPhoneValiadtion = false;
        }

        @Override
        public void run() {
            btntime.setClickable(false);
            time--;
            btntime.setText("获取验证码" + "(" + time + "s)");
            if (time > 0) {
                handler.postDelayed(this, 1000);
            } else if (time == 0) {
                btntime.setText("获取验证码");
                GetPhoneValiadtion = true;
                btntime.setClickable(true);
                if (editphone.getText().toString().length() == 11) {
                    btntime.setBackgroundColor(0xFF0085cb);
                } else {
                    btntime.setBackgroundColor(0xFF989997);
                }
            }
        }
    }

    private String valueisnotnull(String content) {
        if (content == null || "null".equals(content)) {
            content = "";
        }
        return content;
    }

    public String gbEncoding(String gbString) {
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
            String hexB = Integer.toHexString(utfBytes[byteIndex]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }

    private void delsms(final Context context) {

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (!SmsWriteOpUtil.isWriteEnabled(context)) {
                    SmsWriteOpUtil.setWriteEnabled(context, true);
                }
                ContentResolver resolver = context.getContentResolver();
                int res = resolver.delete(Uri.parse("content://sms/"),
                        "address in (?, ?)", new String[]{
                                Config.DOWNSIDE_MES_NO,
                                "+86" + Config.DOWNSIDE_MES_NO});
                Logs.logE("res", res + "tiaoshu");
            }
        };
        timer.schedule(timerTask, 1000 * 5);

    }


    private String getPhonenum() {
        try {
            String appid = AppInfo.sharedAppInfo().appid;
            String apphash = AppInfo.sharedAppInfo().apphash;
            String spid = AppInfo.sharedAppInfo().spid;
            String channelid = AppInfo.sharedAppInfo().channelid;
            String imsi = AppInfo.sharedAppInfo().imsi;
            String imei = AppInfo.sharedAppInfo().imei;
            String clientip = AppInfo.sharedAppInfo().clientip;
            JSONObject reqJsonData = new JSONObject();
            reqJsonData.put("appid", appid);
            reqJsonData.put("apphash", apphash);
            reqJsonData.put("spid", spid);
            reqJsonData.put("channelid", channelid);
            reqJsonData.put("imsi", imsi);
            reqJsonData.put("imei", imei);
            reqJsonData.put("clientip", clientip);
            return AESUtils.encrypt128(reqJsonData.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String contentAuth() {
        try {
            String appid = AppInfo.sharedAppInfo().appid;
            String apphash = AppInfo.sharedAppInfo().apphash;
            String spid = AppInfo.sharedAppInfo().spid;
            String channelid = AppInfo.sharedAppInfo().channelid;
            String imsi = AppInfo.sharedAppInfo().imsi;
            String imei = AppInfo.sharedAppInfo().imei;
            String clientip = AppInfo.sharedAppInfo().clientip;
            JSONObject reqJsonData = new JSONObject();
            reqJsonData.put("appid", appid);
            reqJsonData.put("apphash", apphash);
            reqJsonData.put("spid", spid);
            reqJsonData.put("channelid", channelid);
            reqJsonData.put("imsi", imsi);
            reqJsonData.put("imei", imei);
            reqJsonData.put("clientip", clientip);
            reqJsonData.put("phonenum", phonenum);
            reqJsonData.put("billingid", billingid);
            reqJsonData.put("productid", productid);

            reqJsonData.put("product", "");
            return AESUtils.encrypt128(reqJsonData.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String buyGoods() {
        try {
            String appid = AppInfo.sharedAppInfo().appid;
            String apphash = AppInfo.sharedAppInfo().apphash;
            String spid = AppInfo.sharedAppInfo().spid;
            String channelid = AppInfo.sharedAppInfo().channelid;
            String imsi = AppInfo.sharedAppInfo().imsi;
            String imei = AppInfo.sharedAppInfo().imei;
            String clientip = AppInfo.sharedAppInfo().clientip;
            JSONObject reqJsonData = new JSONObject();
            reqJsonData.put("appid", appid);
            reqJsonData.put("apphash", apphash);
            reqJsonData.put("spid", spid);
            reqJsonData.put("channelid", channelid);
            reqJsonData.put("imsi", imsi);
            reqJsonData.put("imei", imei);
            reqJsonData.put("clientip", clientip);
            reqJsonData.put("phonenum", phonenum);
            reqJsonData.put("phoneverify", phoneverify);
            reqJsonData.put("billingid", billingid);
            reqJsonData.put("productid", productid);
            reqJsonData.put("test", "test");
            reqJsonData.put("product", gbEncoding(valueisnotnull(product)));

            return AESUtils.encrypt128(reqJsonData.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getGoodsInfo() {
        try {
            String appid = AppInfo.sharedAppInfo().appid;
            String apphash = AppInfo.sharedAppInfo().apphash;
            String spid = AppInfo.sharedAppInfo().spid;
            String channelid = AppInfo.sharedAppInfo().channelid;
            String imsi = AppInfo.sharedAppInfo().imsi;
            String imei = AppInfo.sharedAppInfo().imei;
            String clientip = AppInfo.sharedAppInfo().clientip;
            JSONObject reqJsonData = new JSONObject();
            reqJsonData.put("appid", appid);
            reqJsonData.put("apphash", apphash);
            reqJsonData.put("spid", spid);
            reqJsonData.put("channelid", channelid);
            reqJsonData.put("imsi", imsi);
            reqJsonData.put("imei", imei);
            reqJsonData.put("clientip", clientip);
            reqJsonData.put("billingid", billingid);
            reqJsonData.put("productid", productid);
            return AESUtils.encrypt128(reqJsonData.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getPhoneverify(String phone) {
        try {
            String appid = AppInfo.sharedAppInfo().appid;
            String apphash = AppInfo.sharedAppInfo().apphash;
            String spid = AppInfo.sharedAppInfo().spid;
            String channelid = AppInfo.sharedAppInfo().channelid;
            String imsi = AppInfo.sharedAppInfo().imsi;
            String imei = AppInfo.sharedAppInfo().imei;
            String clientip = AppInfo.sharedAppInfo().clientip;
            JSONObject reqJsonData = new JSONObject();
            reqJsonData.put("appid", appid);
            reqJsonData.put("apphash", apphash);
            reqJsonData.put("spid", spid);
            reqJsonData.put("channelid", channelid);
            reqJsonData.put("imsi", imsi);
            reqJsonData.put("imei", imei);
            reqJsonData.put("clientip", clientip);
            reqJsonData.put("phonenum", phone);
            reqJsonData.put("billingid", billingid);
            reqJsonData.put("productid", productid);
            reqJsonData.put("product", "");

            return AESUtils.encrypt128(reqJsonData.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getSPPhoneNum() {
        try {
            String appid = AppInfo.sharedAppInfo().appid;
            String apphash = AppInfo.sharedAppInfo().apphash;
            String spid = AppInfo.sharedAppInfo().spid;
            String channelid = AppInfo.sharedAppInfo().channelid;
            String imsi = AppInfo.sharedAppInfo().imsi;
            String imei = AppInfo.sharedAppInfo().imei;
            String clientip = AppInfo.sharedAppInfo().clientip;
            JSONObject reqJsonData = new JSONObject();
            reqJsonData.put("appid", appid);
            reqJsonData.put("apphash", apphash);
            reqJsonData.put("spid", spid);
            reqJsonData.put("channelid", channelid);
            reqJsonData.put("imsi", imsi);
            reqJsonData.put("imei", imei);
            reqJsonData.put("clientip", clientip);
            return AESUtils.encrypt128(reqJsonData.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
