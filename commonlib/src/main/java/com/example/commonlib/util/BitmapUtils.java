package com.example.commonlib.util;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.SparseArray;

import com.example.commonlib.safe.CloseUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author zhangchao
 * @date 2018/03/17
 * @description Bitmap操作工具
 */
public class BitmapUtils {
    public static final int ROTATE_LEFT_RIGHT = 2;
    public static final int ROTATE_UP_DOWN = 3;

    static private BitmapUtils sInstance = null;

    private SparseArray<Bitmap> mBitmapHash = new SparseArray<Bitmap>();
    private Context mContext = null;
    private Config mBitmapConfig = Config.RGB_565;

    private BitmapUtils() {
    }

    /**
     * 获取BitmapUtils的单例
     *
     * @return BitmapUtils
     */
    static synchronized public BitmapUtils getInstance() {
        if (sInstance == null) {
            sInstance = new BitmapUtils();
        }
        return sInstance;
    }

    /**
     * 翻转图片
     *
     * @param bm        需要旋转的图片
     * @param direction 度数
     * @return 翻转后的图片
     */
    public static Bitmap reverseBitmap(Bitmap bm, int direction) {
        Bitmap returnBm;
        Bitmap btt;
        Matrix mx = new Matrix();
        int w = bm.getWidth();
        int h = bm.getHeight();

        // 产生镜像
        if (direction == ROTATE_LEFT_RIGHT) {
            mx.setScale(1, -1);
        } else if (direction == ROTATE_UP_DOWN) {
            mx.setScale(-1, 1);
        }

        btt = Bitmap.createBitmap(bm, 0, 0, w, h, mx, true);
        mx.setRotate(180);
        returnBm = Bitmap.createBitmap(btt, 0, 0, btt.getWidth(), btt.getHeight(), mx, true);

        if (btt != returnBm) {
            btt.recycle();
        }
        if (bm != returnBm) {
            bm.recycle();
        }

        return returnBm;
    }

    /**
     * 把小边放大至要求值，同时限制大边不超过要求值
     *
     * @param bitmap
     * @param shortSideGoal 短边需要放大到的目标值
     * @param longSideLimit 长边需要被限制的值得
     * @return
     */
    public static Bitmap expandShortAndLimitLong(Bitmap bitmap, float shortSideGoal, float longSideLimit) {
        if (bitmap == null) {
            return null;
        }
        if (bitmap.isRecycled()) {
            return null;
        }

        // 如果图片的两边都比短边限制值长，并且图片的两边都比长边限制值短，则不用处理，直接返回
        if (shortSideGoal < Math.min(bitmap.getWidth(), bitmap.getHeight())
                && longSideLimit > Math.max(bitmap.getWidth(), bitmap.getHeight())) {
            return bitmap;
        }

        float sx = 1.0f;
        float sy = 1.0f;

        if (bitmap.getWidth() > bitmap.getHeight()) {// 如果y方向是短边
            sy = shortSideGoal / bitmap.getHeight();
            sx = sy;
            if (bitmap.getWidth() * sx > longSideLimit) {
                sx = longSideLimit / bitmap.getWidth();
            }
        } else {// 如果x方向是短边
            sx = shortSideGoal / bitmap.getWidth();
            sy = sx;
            if (bitmap.getHeight() * sy > longSideLimit) {
                sy = longSideLimit / bitmap.getHeight();
            }
        }

        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy);
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    /**
     * 通过角度旋转图片
     *
     * @param bm     Bitmap图片
     * @param degree 要旋转的角度
     * @return Bitmap格式的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {

        }

        if (returnBm == null) {
            returnBm = bm;
        }

        if (bm != returnBm) {
            bm.recycle();
        }

        return returnBm;
    }

    /**
     * 压缩图片
     *
     * @param imgPath 图片路径
     * @param w       压缩宽
     * @param h       压缩高
     * @return Bitmap
     */
    public static Bitmap compressImage(String imgPath, int w, int h) {
        File picture = new File(imgPath);
        BitmapFactory.Options bitmapFactoryOptions = new BitmapFactory.Options();
        bitmapFactoryOptions.inJustDecodeBounds = true;
        bitmapFactoryOptions.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(picture.getAbsolutePath(), bitmapFactoryOptions);
        float imageW = w;
        float imageH = h;
        int yRatio = (int) Math.ceil(bitmapFactoryOptions.outHeight / imageH);
        int xRatio = (int) Math.ceil(bitmapFactoryOptions.outWidth / imageW);
        if (yRatio > 1 || xRatio > 1) {
            if (yRatio > xRatio) {
                bitmapFactoryOptions.inSampleSize = yRatio;
            } else {
                bitmapFactoryOptions.inSampleSize = xRatio;
            }
        }
        bitmapFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(picture.getAbsolutePath(), bitmapFactoryOptions);

        int de = getBitmapDegree(imgPath);
        if (de != 0) {
            bitmap = rotateBitmapByDegree(bitmap, de);
        }

        if (bitmap != null) {
            return bitmap;
        }
        return null;
    }

    /**
     * 获取一张本地图片的尺寸，不会加载bitmap
     *
     * @param photoPath
     * @return
     */
    public static int[] getPhotoSize(String photoPath, int maxWidth, int maxHeight) {
        if (StringUtils.isEmpty(photoPath) || maxWidth <= 0 || maxHeight <= 0) {
            return null;
        }

        File picture = new File(photoPath);
        if (!picture.exists()) {
            return null;
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            int s = 1;
            while ((options.outWidth / (s * 2) > maxWidth) || (options.outHeight / (s * 2) > maxHeight)) {
                s *= 2;
            }

            options.inJustDecodeBounds = true;
            options.inSampleSize = s;
            BitmapFactory.decodeFile(picture.getAbsolutePath(), options);
            return new int[]{options.outWidth, options.outHeight};

        } catch (Throwable t) {
            LogUtils.print("BitmapUtils", t.getMessage());
        }

        return null;
    }

    /**
     * 压缩Bitmap文件，指定其最大宽度和高度
     *
     * @param picPath   图片路径
     * @param isRotate  是否旋转
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return 压缩后的图片路径
     */
    public static String compressBitmapFile(String picPath, boolean isRotate, int maxWidth, int maxHeight) {
        String uploadTmpDir = FileUtils.getPath("upload");
        if (!FileUtils.checkTempDir(uploadTmpDir)) {
            uploadTmpDir = FileUtils.getPath(null);
        }

        String srcName = new File(picPath).getName();
        String path = uploadTmpDir + srcName + ".temp";
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap image = BitmapFactory.decodeFile(picPath, options);
            double ratio = 1.0D;
            double withRatio = options.outWidth / maxWidth;
            double heightRatio = options.outHeight / maxHeight;
            double _widthRatio = Math.ceil(withRatio);
            double _heightRatio = Math.ceil(heightRatio);
            ratio = _widthRatio > _heightRatio ? _widthRatio : _heightRatio;

            if (ratio > 1) {
                options.inSampleSize = (int) ratio;
            }
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Config.RGB_565;
            try {
                image = BitmapFactory.decodeFile(picPath, options);
            } catch (OutOfMemoryError error) {
                //if oom，return null
                return null;
            }

            int de = getBitmapDegree(picPath);
            if (de != 0 && isRotate) {
                image = rotateBitmapByDegree(image, de);
            }

            File file = new File(path);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                if (image.compress(Bitmap.CompressFormat.JPEG, 80, out)) {
                    out.flush();
                }
            } catch (Exception e) {
                path = null;
                LogUtils.print("BitmapUtils", e.getMessage());
            } finally {
                CloseUtil.close(out);

                if (image != null && !image.isRecycled()) {
                    image.recycle();
                }
            }
        } catch (Exception e) {
            LogUtils.print("BitmapUtils", e.getMessage());
        }
        return path;
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            LogUtils.print("BitmapUtils", e.getMessage());
        }
        return degree;
    }

    /**
     * 通过图片文件名称获取图片
     *
     * @param appPath  app目录
     * @param filename 图片文件名称
     * @return Bitmap
     */
    public Bitmap getImage(String appPath, String filename) {
        return BitmapFactory.decodeFile(FileUtils.getFilePath(appPath, filename));
    }

    /**
     * 通过图片文件路径获取图片
     *
     * @param filePath 图片文件路径
     * @return Bitmap
     */
    public Bitmap getImage(String filePath) {
        return BitmapFactory.decodeFile(FileUtils.getFilePath(filePath));
    }

    /**
     * 通过图片文件绝对路径获取图片
     *
     * @param filePath 图片文件绝对路径
     * @return Bitmap
     */
    public Bitmap getImageAbsolutePath(String filePath) {
        return BitmapFactory.decodeFile(filePath);
    }

    /**
     * {@hide}
     */
    public synchronized void initial(Context context) {
        mContext = context;
    }

    public void setBitmapConfig(Config config) {
        mBitmapConfig = config;
    }

    /**
     * 装载图片的大小，返回的是sample的，不是精确的尺寸。
     *
     * @param fullFilePath 图片文件路径
     * @param maxWidth     允许的最大宽度
     * @param maxHeight    允许的最大高度
     * @return Bitmap
     */
    public Bitmap loadResizedBitmap(String fullFilePath, int maxWidth, int maxHeight) {
        int s = 1;
        if (fullFilePath == null || fullFilePath.length() <= 0 || maxWidth <= 0 || maxHeight <= 0) {
            return null;
        }

        InputStream in = null;
        File file = new File(fullFilePath);

        if (!file.exists()) {
            return null;
        }

        try {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            in = new FileInputStream(file);
            BitmapFactory.decodeStream(in, null, opt);
            opt.inPreferredConfig = mBitmapConfig;
            CloseUtil.close(in);

            while ((opt.outWidth / (s * 2) > maxWidth) || (opt.outHeight / (s * 2) > maxHeight)) {
                s *= 2;
            }

            opt.inJustDecodeBounds = false;
            opt.inSampleSize = s;
            in = new FileInputStream(file);
            Bitmap b = BitmapFactory.decodeStream(in, null, opt);

            return b;

        } catch (Throwable ex) {
            return null;
        } finally {
            CloseUtil.close(in);
        }
    }

    /**
     * 获得缓存的Bitmap
     *
     * @param id cashId
     * @return Bitmap
     */
    public synchronized Bitmap getCacheBitmap(int id) {
        Bitmap bm = mBitmapHash.get(id);
        if (bm != null) {
            return bm;
        } else {
            bm = getResBitmap(mContext, id);
            if (bm != null) {
                mBitmapHash.put(id, bm);
            }
            return bm;
        }
    }

    public synchronized void removeCacheBitmap(int id) {
        mBitmapHash.remove(id);
    }

    public synchronized void clearCacheBitmap() {
        mBitmapHash.clear();
    }

    /**
     * 在文件夹中保存图片文件
     *
     * @param appPath  路径
     * @param filename 临时文件名
     * @param bm       图片
     * @param quality  图片质量
     * @return 成功：保存文件的全路径 ； 失败：空
     */
    public String saveFile(String appPath, String filename, Bitmap bm, int quality) {
        if (FileUtils.checkDir(appPath) == false || bm == null) {
            return null;
        }

        if (FileUtils.checkAndMkdirs(appPath, filename) != true) {
            return null;
        }

        File file = FileUtils.getFile(appPath, filename);
        FileOutputStream fOut = null;
        try {
            if (file.exists()) {
                if (file.delete() == false) {
                    return null;
                }
            }
            if (file.createNewFile() == false) {
                return null;
            }
            fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, quality, fOut);
            fOut.flush();
            return file.getAbsolutePath();
        } catch (Exception ex) {
            LogUtils.print("BitmapUtils", ex.getMessage());
            return null;
        } finally {
            if (fOut != null) {
                try {
                    fOut.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 保存图片
     *
     * @param filename 文件名
     * @param bm       Bitmap
     * @param quality  图片质量
     * @return 成功：保存文件的全路径 ； 失败：空
     */
    public String saveFile(String filename, Bitmap bm, int quality) {
        return saveFile(null, filename, bm, quality);
    }

    /**
     * 从系统Gallery中获得图片资源
     *
     * @param fileName
     * @return
     */
    public boolean addImageFileToSystemGallary(Context context, String fileName) {
        try {
            File file = FileUtils.getFile(fileName);
            if (file == null || !file.exists()) {
                return false;
            }

            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (Throwable e) {
            LogUtils.print("BitmapUtils", fileName + " : " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * 获取图片资源
     *
     * @param context 上下文
     * @param resId   资源id
     * @return 图片
     */
    public Bitmap getResBitmap(Context context, int resId) {
        Bitmap bm = null;
        try {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            bm = BitmapFactory.decodeResource(context.getResources(), resId, opt);
        } catch (Exception ex) {
            LogUtils.print("BitmapUtils", ex.getMessage());
        }
        return bm;
    }

    /**
     * 调整图片的大小
     *
     * @param bitmap
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        if (maxWidth <= 0 || maxHeight < 0 || bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        if (bitmap.getWidth() <= maxWidth && bitmap.getHeight() <= maxHeight) {
            return bitmap;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float temp = 0;
        if (((float) maxHeight / (float) height) > (((float) maxWidth) / (float) width)) {
            temp = (((float) maxWidth) / (float) width);
        } else {
            temp = ((float) maxHeight / (float) height);
        }
        Matrix matrix = new Matrix();
        // resize bitmap
        matrix.postScale(temp, temp);
        // 旋转45°
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        if (resizedBitmap != bitmap) {
            bitmap.recycle();
        }
        return resizedBitmap;
    }

    /**
     * 调整图片的大小
     *
     * @param bitmap
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public Bitmap getResizedBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        if (maxWidth <= 0 || maxHeight < 0 || bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        if (bitmap.getWidth() <= maxWidth && bitmap.getHeight() <= maxHeight) {
            return bitmap;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float temp = 0;
        if (((float) maxHeight / (float) height) < (((float) maxWidth) / (float) width)) {
            temp = (((float) maxWidth) / (float) width);
        } else {
            temp = ((float) maxHeight / (float) height);
        }
        Matrix matrix = new Matrix();
        // resize the bit map
        float x = (maxWidth - width * temp) / 2;
        float y = (maxHeight - height * temp) / 2;
        matrix.postScale(temp, temp);
        matrix.postTranslate(x, y);
        Bitmap resizedBitmap = Bitmap.createBitmap(maxWidth, maxHeight, bitmap.getConfig());
        Canvas canvas = new Canvas(resizedBitmap);
        canvas.drawBitmap(bitmap, matrix, null);
        return resizedBitmap;
    }

    /**
     * 等比缩小给定的图片，不保留原图片
     *
     * @param bitmap  需要缩小的图片
     * @param maxsize 缩小后的最大值
     * @return 缩小后的图片
     */
    public Bitmap resizeBitmap(Bitmap bitmap, int maxsize) {
        return resizeBitmap(bitmap, maxsize, maxsize);
    }

    /**
     * 等比缩小给定的图片,保留原图片,截取正方形图片
     *
     * @param bitmap  需要缩小的图片
     * @param maxsize 缩小后的最大值
     * @return 缩小后的图片
     */
    public Bitmap getResizedBitmap(Bitmap bitmap, int maxsize) {
        return getResizedBitmap(bitmap, maxsize, maxsize);
    }

    /**
     * 等比缩小给定的图片文件
     *
     * @param appPath 需要缩小的图片全路径
     * @param maxsize 缩小后的最大值
     * @return 缩小后的图片
     */
    public Bitmap resizeBitmap(String appPath, String filename, int maxsize) {
        Bitmap b = subSampleBitmap(appPath, filename, maxsize);
        return resizeBitmap(b, maxsize);
    }

    /**
     * @param file
     * @param maxsize
     * @return
     */
    public Bitmap resizeBitmap(String file, int maxsize) {
        Bitmap b = subSampleBitmap(null, file, maxsize);
        return resizeBitmap(b, maxsize);
    }

    /**
     * @param appPath
     * @param filename
     * @param maxsize
     * @return
     */
    public Bitmap subSampleBitmap(String appPath, String filename, int maxsize) {
        int s = 1;
        if (maxsize <= 0) {
            return null;
        }
        try {
            InputStream in = null;
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            in = FileUtils.getInStreamFromFile(appPath, filename);
            BitmapFactory.decodeStream(in, null, opt);
            opt.inPreferredConfig = mBitmapConfig;
            try {
                in.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            while ((opt.outWidth / (s * 2) > maxsize) || (opt.outHeight / (s * 2) > maxsize)) {
                s *= 2;
            }
            opt.inJustDecodeBounds = false;
            opt.inSampleSize = s;
            in = FileUtils.getInStreamFromFile(appPath, filename);
            Bitmap b = BitmapFactory.decodeStream(in, null, opt);
            try {
                in.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return b;
        } catch (Exception ex) {
            return null;
        }
    }

    public Bitmap subSampleBitmap(String file, int maxsize) {
        return subSampleBitmap(null, file, maxsize);
    }

    /**
     * 等比缩小给定的图片文件
     *
     * @param context 上下文
     * @param uri     需要缩小的图片的URI
     * @param maxsize 缩小后的最大值
     * @return 缩小后的图片
     */
    public Bitmap resizeBitmap(Context context, Uri uri, int maxsize) {
        Bitmap b = subSampleBitmap(context, uri, maxsize);
        return resizeBitmap(b, maxsize);
    }

    /**
     * @param context
     * @param uri
     * @param maxsize
     * @return
     */
    public Bitmap subSampleBitmap(Context context, Uri uri, int maxsize) {
        ContentResolver res = context.getContentResolver();
        ParcelFileDescriptor fd = null;
        int s = 1;

        try {
            fd = res.openFileDescriptor(uri, "r");
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = mBitmapConfig;
            opt.inDither = false;
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, opt);
            while ((opt.outWidth / (s + 1) > maxsize) || (opt.outHeight / (s + 1) > maxsize)) {
                s++;
            }
            opt.inJustDecodeBounds = false;
            opt.inSampleSize = s;
            return BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, opt);
        } catch (Exception e) {
            try {
                if (fd != null) {
                    fd.close();
                }
            } catch (Exception ex) {
            }
            return null;
        }
    }

    /**
     * 获得圆角图片
     *
     * @param bitmap  原始图片
     * @param roundPx 圆角
     * @return 圆角后的图片
     */
    public Bitmap getRoundedCornerBitmapARGB4444(Bitmap bitmap, float roundPx) {
        if (bitmap == null) {
            return null;
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_4444);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 获得圆角图片，不带处理ARGB_4444
     *
     * @param bitmap  原始图片
     * @param roundPx 圆角
     * @return 圆角后的图片
     */
    public Bitmap getRoundedCornerBitmapARGB8888(Bitmap bitmap, float roundPx) {
        if (bitmap == null) {
            return null;
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * bitmap 转换图片为 byte[]，存为jpg格式
     *
     * @param bm      bitmap实例
     * @param quality 转换质量
     * @return 字节数组
     */
    public byte[] Bitmap2Bytes(Bitmap bm, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        return baos.toByteArray();
    }

    /**
     * bitmap 转换图片为 byte[]，存为png格式
     *
     * @param bm
     * @return
     */
    public byte[] Bitmap2BytesAndSavePng(Bitmap bm, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
        return baos.toByteArray();
    }

    /**
     * 字节转换成图片
     *
     * @param b
     * @return
     */
    public Bitmap Bytes2Bitmap(byte[] b) {
        if (b != null && b.length != 0) {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = mBitmapConfig;
            return BitmapFactory.decodeByteArray(b, 0, b.length, opt);
        } else {
            return null;
        }
    }

    /**
     * 旋转图片
     *
     * @param bm     需要旋转的图片
     * @param degree 度数
     * @return Bitmap 旋转后的图片
     */

    public Bitmap rotateBitmapBydegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;
        int w = bm.getWidth();
        int h = bm.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            returnBm = Bitmap.createBitmap(bm, 0, 0, w, h, matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }

        return returnBm;
    }

    /**
     * 加载图片，返回的是sample的，不是精确的尺寸。
     *
     * @param fullFilePath 全路径
     * @return
     */
    public Bitmap loadBitmap(String fullFilePath) {
        if (fullFilePath == null || fullFilePath.length() <= 0) {
            return null;
        }

        InputStream in = null;
        File file = new File(fullFilePath);

        if (!file.exists()) {
            return null;
        }

        try {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = mBitmapConfig;
            opt.inJustDecodeBounds = false;

            in = new FileInputStream(file);
            Bitmap b = BitmapFactory.decodeStream(in, null, opt);

            return b;
        } catch (Throwable ex) {
            return null;
        } finally {
            CloseUtil.close(in);
        }
    }

    /**
     * 读取图片
     *
     * @param context 上下文
     * @param uri     地址
     * @return Bitmap
     */
    public Bitmap loadBitmap(Context context, Uri uri) {
        ContentResolver res = context.getContentResolver();
        ParcelFileDescriptor fd = null;

        try {
            fd = res.openFileDescriptor(uri, "r");
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = mBitmapConfig;

            if (fd == null) {
                return null;
            }
            return BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, opt);
        } catch (Throwable e) {
            return null;
        } finally {
            try {
                if (fd != null) {
                    fd.close();
                }
            } catch (Throwable ex) {
                //do nothing
            }
        }
    }


    /**
     * bitmap 图片压缩至 小于指定大小的kb
     *
     * @param image      bitmap实例
     * @param targetSize 目标大小（KB）
     * @return 返回bitmap的数组
     */
    public static byte[] bitmap2ByteArray(Bitmap image, int targetSize) {
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            int tOptions = 100;
            image.compress(Bitmap.CompressFormat.JPEG, tOptions, out);
            while (out.toByteArray().length > (targetSize * 1024) && tOptions != 0) {  //循环判断如果压缩后图片是否大于maxKB kb,大于继续压缩
                out.reset();
                tOptions -= 5;//每次都减少5
                image.compress(Bitmap.CompressFormat.JPEG, tOptions, out);
            }
            return out.toByteArray();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * bitmap 缩放指定kb
     *
     * @param image     原始图片
     * @param maxSizeKB 缩放至大小
     * @return 缩放后的字节数组
     */
    public static byte[] bitmapZoom(Bitmap image, int maxSizeKB) {
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 85, out);
            float zoom = (float) Math.sqrt(maxSizeKB * 1024 / (float) out.toByteArray().length);

            Matrix matrix = new Matrix();
            matrix.setScale(zoom, zoom);

            Bitmap result = Bitmap.createBitmap(image, 0, 0,
                    image.getWidth(), image.getHeight(), matrix, true);

            out.reset();
            result.compress(Bitmap.CompressFormat.JPEG, 85, out);
            while (out.toByteArray().length > maxSizeKB * 1024) {
                matrix.setScale(0.9f, 0.9f);
                result = Bitmap.createBitmap(result, 0, 0,
                        result.getWidth(), result.getHeight(), matrix, true);
                out.reset();
                result.compress(Bitmap.CompressFormat.JPEG, 85, out);
            }

            return out.toByteArray();

        } catch (OutOfMemoryError error) {
            return bitmap2ByteArray(image, maxSizeKB);
        } catch (Throwable t) {
            t.printStackTrace();
            return bitmap2ByteArray(image, maxSizeKB);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 检测当前图片是否是一张已损坏的图片
     *
     * @param imagePath 图片路径
     * @return true已经损坏的图片，false 是一张完整未损坏的图片
     */
    public static boolean isDamagedImage(String imagePath) {
        if (StringUtils.isEmpty(imagePath)) {
            return true;
        }
        File picture = new File(imagePath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picture.getAbsolutePath(), options);
        return options.mCancel || options.outHeight == -1 || options.outWidth == -1;
    }

    /**
     * Bitmap -> byte[]
     *
     * @param bmp 原始图片
     * @return 字节数组
     */
    public static byte[] bitmapToByteArray(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * bitmap 缩放
     *
     * @param bitmap
     * @param scaleX 宽 > 1 放大 < 1 缩小
     * @param scaleY 高 > 1 放大 < 1 缩小
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, float scaleX, float scaleY) {
        Matrix matrix = new Matrix();
        matrix.postScale(scaleX, scaleY); //长和宽放大缩小的比例
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
