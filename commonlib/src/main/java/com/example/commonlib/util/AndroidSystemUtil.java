package com.example.commonlib.util;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Window;

import com.example.commonlib.safe.JavaTypesHelper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;

/**
 * @author zhangchao
 * @date 2018/03/17
 * @description 系统Utils
 */
public class AndroidSystemUtil {

    /**
     * 判断是否为主线程
     *
     * @return true:主线程;false:非主线程
     */
    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    /**
     * 安装APK
     *
     * @param context      上下文
     * @param fullFilePath 文件的全目录
     */
    public static void installApk(Context context, String fullFilePath) {
        if (fullFilePath == null || fullFilePath.length() <= 0) {
            return;
        }
        File file = new File(fullFilePath);

        if (file.exists()) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            String type = "application/vnd.android.package-archive";
            intent.setDataAndType(Uri.fromFile(file), type);
            context.startActivity(intent);
        }
    }

    /**
     * 判断特定的apk是否有安装
     *
     * @param context     上下文对象
     * @param packageName 安装包名称
     * @return true:已安装;false:未安装
     */
    public static boolean isInstallApk(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName) || context == null) {
            return false;
        }
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }

        return packageInfo != null;
    }

    /**
     * 判断是否为Debuggable模式
     *
     * @param context
     * @return
     */
    public static boolean isDebuggable(Context context) {
        if (context == null) {
            return false;
        }
        return (0 != (context.getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE));
    }

    /**
     * 获取状态栏的高度
     *
     * @param context 上下文实例对象
     * @return int
     * 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        if (context == null)
            return 0;
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取状态栏的高度
     *
     * @param activity Activity实例
     * @return int
     * 状态栏高度
     */
    public static int getStatusBarHeight(Activity activity) {
        int height = 0;
        Rect rect = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        height = rect.top;
        if (height == 0) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = JavaTypesHelper.toInt(localClass.getField("status_bar_height").get(localObject).toString(), 0);
                height = activity.getResources().getDimensionPixelSize(i5);
            } catch (ClassNotFoundException e) {
                LogUtils.print("AndroidSystemUtil", e.getMessage());
            } catch (IllegalAccessException e) {
                LogUtils.print("AndroidSystemUtil", e.getMessage());
            } catch (InstantiationException e) {
                LogUtils.print("AndroidSystemUtil", e.getMessage());
            } catch (NumberFormatException e) {
                LogUtils.print("AndroidSystemUtil", e.getMessage());
            } catch (IllegalArgumentException e) {
                LogUtils.print("AndroidSystemUtil", e.getMessage());
            } catch (SecurityException e) {
                LogUtils.print("AndroidSystemUtil", e.getMessage());
            } catch (NoSuchFieldException e) {
                LogUtils.print("AndroidSystemUtil", e.getMessage());
            }
        }
        return height;
    }

    /**
     * 获取NavigationBar高度
     *
     * @param context 上下文实例对象
     * @return int
     * NavigationBar高度
     */
    public static int getNavigationBarHeight(Context context) {
        if (context == null)
            return 0;
        int result = 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取NavigationBarLandscape的高度
     *
     * @param context 上下文实例对象
     * @return int
     * NavigationBarLandscape的高度
     */
    public static int getNavigationBarLandscapeHeight(Context context) {
        if (context == null)
            return 0;
        int result = 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height_landscape", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    /**
     * 获取应用的签名
     *
     * @param context 上下文
     * @return int 应用的数字签名
     */
    public static int getSignatureOfThisApp(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo info = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = info.signatures;
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(signs[0]
                    .toByteArray()));
            PublicKey key = cert.getPublicKey();
            int modulusHash = ((RSAPublicKey) key).getModulus().hashCode();

            return modulusHash;
        } catch (Throwable e) {

        }

        return 0;
    }


    /**
     * 获取本地已安装的应用版本号
     *
     * @param context     上下文对象
     * @param packageName 包名称
     * @return int
     * 成功，返回版本号；失败，返回-1
     */
    public static int getInstallApkVersion(Context context, String packageName) {

        if (TextUtils.isEmpty(packageName)) {
            return -1;
        }

        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            if (packageInfo != null) {
                return packageInfo.versionCode;
            }
        } catch (NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * 复制到剪贴板
     *
     * @param content 上下文
     */
    public static void copyToClipboard(Context context, String content) {
        if (content == null) {
            content = "";
        }

        try {
            ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clip.setText(content);
        } catch (Throwable ex) {
            LogUtils.print("AndroidSystemUtil", ex.getMessage());
        }
    }

    /**
     * 添加快捷方式
     *
     * @param context     上下文对象
     * @param appName     app名称
     * @param packageName 安装包名称
     * @param className   类名称
     * @param iconResId   快捷方式图标
     */
    public static void addShortcut(Context context, String appName,
                                   String packageName, String className, int iconResId) {
        Intent target = new Intent();
        target.addCategory(Intent.CATEGORY_LAUNCHER);
        target.setAction(Intent.ACTION_MAIN);
        ComponentName comp = new ComponentName(packageName, className);
        target.setComponent(comp);

        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // 快捷方式的名称
        shortcut.putExtra("duplicate", false);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, target);

        // 快捷方式的图标
        Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(context, iconResId);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
        context.sendBroadcast(shortcut);
    }

    /**
     * 检查是否有快捷方式
     *
     * @param context 上下文对象
     * @param appName 被检测app名称
     * @return true:有快捷方式;false:无快捷方式
     */
    public static boolean checkShortCut(Context context, String appName) {
        boolean hasShortCut = false;
        try {
            ContentResolver cr = context.getContentResolver();
            final String AUTHORITY1 = "com.android.launcher.settings";
            final String AUTHORITY2 = "com.android.launcher2.settings";
            String contentUri = "";
            if (android.os.Build.VERSION.SDK_INT < 8) {
                contentUri = "content://" + AUTHORITY1 + "/favorites?notify=true";
            } else {
                contentUri = "content://" + AUTHORITY2 + "/favorites?notify=true";
            }
            final Uri CONTENT_URI = Uri.parse(contentUri);
            Cursor c = cr.query(CONTENT_URI, new String[]{"title", "iconResource"}, "title=?",
                    new String[]{appName}, null);
            if (c != null && c.getCount() > 0) {
                hasShortCut = true;
            }
        } catch (Exception e) {
            LogUtils.print("AndroidSystemUtil", e.getMessage());
        }
        return hasShortCut;
    }

    /**
     * Activity转场动画设置
     *
     * @param activity Activity实例
     * @param inAnim   进入动画Res id
     * @param exitAnim 退出动画Res id
     */
    public static void overridePendingTransition(Activity activity, int inAnim, int exitAnim) {
        Class<?> myTarget;
        Method myMethod = null;
        Class<?>[] paramTypes = {Integer.TYPE, Integer.TYPE};

        try {
            myTarget = Class.forName("android.app.Activity");
            myMethod = myTarget.getDeclaredMethod("overridePendingTransition", paramTypes);
            myMethod.invoke(activity, inAnim, exitAnim); // this - your Activity
            // instance
        } catch (Exception e) {
            LogUtils.print("AndroidSystemUtil", e.getMessage());
        }
    }
}
