package com.example.commonlib.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * UIUtils
 * @author zhangchao
 * @date 2018-03-17
 * @description 视图工具类
 */
public class UIUtils {

    static boolean deviceDataInited = false;

    /**
     * 屏幕密度
     */
    private static float density;

    /**
     * 屏幕密度
     */
    private static int densityDpi;

    /**
     * 屏幕宽度
     */
    private static int screenWidth;

    /**
     * 屏幕高度
     */
    private static int screenHeight;

    public static void initDeviceData(Context context) {
        DisplayMetrics displayMetrics = null;
        if (context.getResources() != null && (displayMetrics = context.getResources().getDisplayMetrics()) != null) {
            density = displayMetrics.density;
            densityDpi = displayMetrics.densityDpi;
            screenWidth = displayMetrics.widthPixels;
            screenHeight = displayMetrics.heightPixels;
        }
        deviceDataInited = true;
    }

    /**
     * px to sp
     *
     * @param context 上下文对象,建议使用Application的context
     * @param pxValue 需要转换的px值
     * @return int sp值
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * dip to px
     *
     * @param context 上下文对象,建议使用Application的context
     * @param dpValue 需要转换的dip值
     * @return int px值
     */
    public static int dip2px(Context context, float dpValue) {
        if (deviceDataInited) {
            return (int) (dpValue * density + 0.5f);
        } else {
            if (context != null && context.getResources() != null) {
                float scale = context.getResources().getDisplayMetrics().density;
                return (int) (dpValue * scale + 0.5f);
            } else {
                return -1;
            }
        }
    }

    /**
     * 获取屏幕宽度
     *
     * @param context 上下文对象,建议使用Application的context
     * @return int 屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        if (deviceDataInited) {
            return screenWidth;
        }

        if (context != null && context.getResources() != null) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            return displayMetrics.widthPixels;
        }
        return -1;
    }

    /**
     * 获取屏幕高度
     *
     * @param context 上下文对象,建议使用Application的context
     * @return int 屏幕高度
     */
    public static int getScreenHeight(Context context) {
        if (deviceDataInited) {
            return screenHeight;
        }

        if (context != null && context.getResources() != null) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            return displayMetrics.heightPixels;
        }
        return -1;
    }

    /**
     * 请勿在onCreate方法中使用
     *
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * 获取屏幕密度
     *
     * @param context 上下文对象,建议使用Application的context
     * @return float 屏幕密度
     */
    public static float getScreenDensity(Context context) {
        if (deviceDataInited) {
            return density;
        }

        if (context != null && context.getResources() != null) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            return displayMetrics.density;
        }
        return 0f;
    }

    /**
     * 获取屏幕密度dpi
     *
     * @param context 上下文对象,建议使用Application的context
     * @return int  DENSITY_LOW:120
     * DENSITY_MEDIUM:160
     * DENSITY_HIGH:240
     * DENSITY_XHIGH:320
     */
    public static int getScreenDPI(Context context) {
        if (deviceDataInited) {
            return densityDpi;
        }

        if (context != null && context.getResources() != null) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            return displayMetrics.densityDpi;
        }
        return 0;
    }


    /**
     * 设置屏幕亮度
     *
     * @param activity
     * @param value    亮度值 (暗)0~(亮)255(不在0~255范围内时,系统将设置为刚进入时的亮度)
     */
    public static void setScreenBrightness(Activity activity, float value) {
        if (activity == null) {
            return;
        }
        //设置亮度,使生效
        android.view.Window localWindow = activity.getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
        float percentage = value / 255f;
        localLayoutParams.screenBrightness = percentage;
        localWindow.setAttributes(localLayoutParams);
    }

    /**
     * 获取屏幕亮度
     *
     * @param activity
     * @return int -255:activity为空; 0~255:屏幕亮度(不在0~255范围内时,系统将设置为刚进入时的亮度)
     */
    public static int getScreenBrightness(Activity activity) {
        if (activity == null) {
            return -255;
        }
        int value = 0;
        ContentResolver cr = activity.getContentResolver();
        try {
            value = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
            return value;
        } catch (Settings.SettingNotFoundException e) {
            LogUtils.print("UIUtils", e.getMessage());
            return -255;
        }
    }

    /**
     * 获取屏幕尺寸
     *
     * @param context 上下文对象,建议使用Application的context
     * @return
     */
    public static int[] getScreenDimensions(Context context) {
        int[] dimensions = new int[2];
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        dimensions[0] = display.getWidth();
        dimensions[1] = display.getHeight();
        return dimensions;
    }

    /**
     * 根据屏幕宽度作为100%，根据比例来计算出相应距离
     *
     * @param context 上下文对象,建议使用Application的context
     * @param scale   缩放比例
     * @return long 缩放后的屏幕宽度
     */
    public static long getPxByScreenWidth(Context context, double scale) {
        return Math.round(getScreenWidth(context) * scale);
    }

    /**
     * 根据屏幕高度作为100%，根据比例来计算出相应距离
     *
     * @param context 上下文对象,建议使用Application的context
     * @param scale   比例
     * @return long 缩放后的屏幕高度
     */
    public static long getPxByEquipHeight(Context context, double scale) {
        return Math.round(getScreenHeight(context) * scale);
    }

    /**
     * 获取资源绝对尺寸
     *
     * @param context 上下文对象,建议使用Application的context
     * @param dimenId 资源id
     * @return int
     */
    public static int getDimensionPixelSize(Context context, int dimenId) {
        if (context == null) {
            return 0;
        }
        return context.getResources().getDimensionPixelSize(dimenId);

    }

    /**
     * 获取字体高度
     *
     * @param context  上下文对象,建议使用Application的context
     * @param fontSize 字体大小
     * @return int -1:上下文对象为空; 字体高度
     */
    public static int getFontHeight(Context context, float fontSize) {
        if (context == null) {
            return -1;
        }

        TextPaint paint = new TextPaint();
        setTextSize(context, paint, fontSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.ascent);
    }

    /**
     * 精确计算文字宽度
     *
     * @param paint 画笔
     * @param str   文字字符串
     * @return int 文字宽度
     */
    public static int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }

    public static String getTextOmit(TextPaint paint, String str, int width) {
        String des = null;
        CharSequence sequence = TextUtils.ellipsize(str, paint, width, TextUtils.TruncateAt.END);
        if (sequence != null) {
            des = sequence.toString();
        }
        return des;
    }

    /**
     * 根据资源名，获取资源 ID
     *
     * @param context 上下文对象,建议使用Application的context
     * @param name    资源名称
     * @param defType 资源类型
     * @return
     */
    public static int getResId(Context context, String name, String defType) {
        Resources res = context.getResources();
        int id = res.getIdentifier(name, defType, context.getPackageName());

        return id;
    }

    /**
     * 设置窗口透明度
     *
     * @param activity 窗口实例
     * @param alpha    透明度
     */
    public static void setWindowAlpha(Activity activity, float alpha) {
        if (activity == null) {
            return;
        }

        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        // params.alpha = alpha;
        params.screenBrightness = alpha;
        activity.getWindow().setAttributes(params);
    }

    /**
     * 设置文字大小
     *
     * @param context 上下文对象,建议使用Application的context
     * @param paint   画笔
     * @param size    文字大小
     * @return TextPaint
     */
    public static TextPaint setTextSize(Context context, TextPaint paint, float size) {
        Resources r;
        if (context == null) {
            r = Resources.getSystem();
        } else {
            r = context.getResources();
        }
        if (r != null) {
            paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, r.getDisplayMetrics()));
        }
        return paint;
    }

    /**
     * 粗略计算文字宽度
     *
     * @param paint 画笔
     * @param str   文字字符串
     * @return int 0:测量出错;other:文字宽度
     */
    public static float measureTextWidth(Paint paint, String str) {
        if (paint == null || TextUtils.isEmpty(str)) {
            return 0;
        }
        return paint.measureText(str);
    }

    /**
     * 计算文字所在矩形，可以得到宽高
     *
     * @param paint 画笔
     * @param str   文字字符串
     * @return Rect 文字矩形
     */
    public static Rect measureText(Paint paint, String str) {
        Rect rect = new Rect();
        paint.getTextBounds(str, 0, str.length(), rect);
        return rect;
    }


    public static void setEditHintSize(EditText editText, String text, int size) {
        // 新建一个可以添加属性的文本对象
        SpannableString ss = new SpannableString(text);
        // 新建一个属性对象,设置文字的大小
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(size, true);
        // 附加属性到文本
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        StyleSpan span = new StyleSpan(Typeface.NORMAL);
        ss.setSpan(span, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 设置hint
        editText.setHint(new SpannedString(ss)); // 一定要进行转换,否则属性会消失
    }

    public static void setBold(TextView textView, String text) {
        // 新建一个可以添加属性的文本对象
        SpannableString ss = new SpannableString(text);
        StyleSpan span = new StyleSpan(Typeface.BOLD);
        ss.setSpan(span, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 设置hint
        textView.setText(new SpannedString(ss)); // 一定要进行转换,否则属性会消失
    }

    public static void setBoldAndSize(TextView textView, String text, int size) {
        // 新建一个可以添加属性的文本对象
        SpannableString ss = new SpannableString(text);
        // 新建一个属性对象,设置文字的大小
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(size, true);
        // 附加属性到文本
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        StyleSpan span = new StyleSpan(Typeface.NORMAL);
        ss.setSpan(span, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 设置hint
        textView.setHint(new SpannedString(ss)); // 一定要进行转换,否则属性会消失
    }

    /**
     * textview 字体加粗
     *
     * @param textView
     */
    public static void setTextBold(TextView textView) {
        textView.getPaint().setFakeBoldText(true);
    }


    /**
     * 代码设置 textView的leftDrawable属性； 参见：android:drawableLeft="@drawable/xxx"
     */
    public static void setTextLeftDrawable(Context context, int drawableID, TextView textView) {
        if (textView == null) {
            return;
        }
        Drawable drawable = context.getResources().getDrawable(drawableID);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textView.setCompoundDrawables(drawable, null, null, null);
    }

    /**
     * textView设置删除线
     *
     * @param textView 设置删除线的textView
     * @param isAdd    true 增加删除线， false 移除
     */
    public static void setTextDeleteLine(TextView textView, boolean isAdd) {
        if (textView == null) {
            return;
        }
        if (isAdd) {
            textView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        } else {
            textView.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
        }
        textView.invalidate();
    }

}
