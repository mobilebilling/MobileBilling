package com.billing.main;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

public class BPUtil {
    private static final char[] CHARS = {'2', '3', '4', '5', '6', '7', '8',
            '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'l', 'n',
            'p', 'q', 'r', 's', 't', 'u', 'v', 'x', 'y', 'z', 'A', 'B', 'C',
            'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'N', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'X', 'Y', 'Z'};

    private static BPUtil bmpCode;
    public String v;

    public static BPUtil getInstance() {
        if (bmpCode == null) {
            bmpCode = new BPUtil();
        }
        return bmpCode;
    }

    private static final int DEFAULT_CODE_LENGTH = 4;
    private static final int DEFAULT_FONT_SIZE = 55;
    private static final int DEFAULT_WIDTH = 160, DEFAULT_HEIGHT = 120;

    private int width = DEFAULT_WIDTH, height = DEFAULT_HEIGHT;

    // number of chars, lines; font size
    private int codeLength = DEFAULT_CODE_LENGTH,
            font_size = DEFAULT_FONT_SIZE;

    // variables
    private String code;
    private Random random = new Random();

    // 验证码图片
    public Bitmap createBitmap() {

        Bitmap bp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bp);

        code = createCode();

        // c.drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setTextSize(font_size);
        for (int i = 0; i < code.length(); i++) {
            randomTextStyle(paint);
            // randomPadding();
            c.drawText(code.charAt(i) + " ", i * 30 + 20, height / 2 + 15,
                    paint);
        }

       c.save();// 保存
        c.restore();//
        return bp;
    }

    public String getCode() {
        return code;
    }

    // 验证码
    private String createCode() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            buffer.append(CHARS[random.nextInt(CHARS.length)]);
        }
        v = buffer.toString();
        return buffer.toString();
    }

    private int randomColor() {
        return randomColor(1);
    }

    private int randomColor(int rate) {
        int red = random.nextInt(256) / rate;
        int green = random.nextInt(256) / rate;
        int blue = random.nextInt(256) / rate;
        return Color.rgb(red, green, blue);
    }

    private void randomTextStyle(Paint paint) {
        int color = randomColor();
        paint.setColor(color);
        paint.setFakeBoldText(random.nextBoolean()); // true为粗体，false为非粗体
        float skewX = random.nextInt(11) / 10;
        skewX = random.nextBoolean() ? skewX : -skewX;
    }

    public String vi() {
        return v;
    }
}
