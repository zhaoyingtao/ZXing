package com.snow.zxingl.encoding;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by zyt on 2018/8/30.
 * 二维码工具类
 */

public class QRCodeUtil {
    private static QRCodeUtil qrCodeUtil;

    public static QRCodeUtil init() {
        if (qrCodeUtil == null) {
            synchronized (QRCodeUtil.class) {
                if (qrCodeUtil == null) {
                    qrCodeUtil = new QRCodeUtil();
                }
            }
        }
        return qrCodeUtil;
    }

    /**
     * 创建二维码位图
     *
     * @param content 字符串内容(支持中文)
     * @param width   位图宽度(单位:px)
     * @param height  位图高度(单位:px)
     * @return
     */
    @Nullable
    public Bitmap createQRCodeBitmap(String content, int width, int height) {
        return createQRCodeBitmap(content, width, height, "UTF-8", "H", "0", Color.BLACK, Color.WHITE, false);
    }

    /**
     * 创建二维码====发现logo要是模糊，将传入的宽高放大就好了
     *
     * @param content content
     * @param width   widthPix
     * @param height  heightPix
     * @param logoBm  logoBm
     * @return 二维码
     */
    public Bitmap createQRCodeBitmap(String content, int width, int height, Bitmap logoBm) {
        // 生成二维码图片的格式，使用ARGB_8888
        Bitmap logoBitmap = null;
        Bitmap bitmap = createQRCodeBitmap(content, width, height, "UTF-8", "H", "0", Color.BLACK, Color.WHITE, false);
        if (logoBm != null) {
            logoBitmap = addLogo(bitmap, logoBm);
        }
        //必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
        return logoBitmap;
    }

    /**
     * 创建二维码位图 (支持自定义配置和自定义样式)
     *
     * @param content          字符串内容
     * @param width            位图宽度,要求>=0(单位:px)
     * @param height           位图高度,要求>=0(单位:px)
     * @param character_set    字符集/字符转码格式 (支持格式:{@link  })。传null时,zxing源码默认使用 "ISO-8859-1"
     * @param error_correction 容错级别 (支持级别:{@link  })。传null时,zxing源码默认使用 "L"
     * @param margin           空白边距 (可修改,要求:整型且>=0), 传null时,zxing源码默认使用"4"
     * @param color_black      黑色色块的自定义颜色值
     * @param color_white      白色色块的自定义颜色值
     * @param isKeepWhiteEdge  是否保留白边
     * @return
     */
    @Nullable
    public Bitmap createQRCodeBitmap(String content, int width, int height,
                                     @Nullable String character_set, @Nullable String error_correction, @Nullable String margin,
                                     @ColorInt int color_black, @ColorInt int color_white, boolean isKeepWhiteEdge) {

        /** 1.参数合法性判断 */
        if (TextUtils.isEmpty(content)) { // 字符串内容判空
            return null;
        }
        if (width < 0 || height < 0) { // 宽和高都需要>=0
            return null;
        }
        try {
            /** 2.设置二维码相关配置,生成BitMatrix(位矩阵)对象 */
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();

            if (!TextUtils.isEmpty(character_set)) {
                hints.put(EncodeHintType.CHARACTER_SET, character_set); // 字符转码格式设置
            }

            if (!TextUtils.isEmpty(error_correction)) {
                hints.put(EncodeHintType.ERROR_CORRECTION, error_correction); // 容错级别设置
            }

            if (!TextUtils.isEmpty(margin)) {
                hints.put(EncodeHintType.MARGIN, margin); // 空白边距设置
            }
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值 */
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = color_black; // 黑色色块像素设置
                    } else {
                        pixels[y * width + x] = color_white; // 白色色块像素设置
                    }
                }
            }

            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,之后返回Bitmap对象 */
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return isKeepWhiteEdge ? bitmap : createCenterBitmap(bitMatrix, bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断是否有白边，如果有，就把二维码给截取出来
     */
    private Bitmap createCenterBitmap(BitMatrix bitMatrix, Bitmap bitmap) {
        try {
            int[] topLeftOnBit = bitMatrix.getTopLeftOnBit();
            int[] bottomRightOnBit = bitMatrix.getBottomRightOnBit();
            int left = topLeftOnBit[0];
            int top = topLeftOnBit[1];
            int right = bottomRightOnBit[0];
            int bottom = bottomRightOnBit[1];
            if (left > 0 && top > 0 && left < right && top < bottom) {
                int width = right - left;
                int height = bottom - top;
                return Bitmap.createBitmap(bitmap, left, top, width, height);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return bitmap;
        }
        return bitmap;
    }

    /**
     * 在二维码中间添加Logo图案
     */
    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }
        if (logo == null) {
            return src;
        }
        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();
        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }
        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }
        //logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / 4 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }
}