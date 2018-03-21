package com.example.commonlib.safe;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import com.example.commonlib.util.LogUtils;


/**
 * @author zhangchao
 * @date 2018-03-17
 * @description 需要依赖window显示的dialog、popupwindow显示的安全工具类
 */
public class ShowUtil {

    public static final boolean showDialog(Dialog dialog, Activity activity) {
        if (dialog != null && activity != null) {
            if (activity.isFinishing()) {
                return false;
            }
            // 当window没有激活的时候调用dialog.show，是没有问题的，此时如果之后调用finish，
            // 只会出现windowleaked(出现window leak是dialog在activity被kill前没有dismiss)
            if (activity.getWindow() != null && !activity.getWindow().isActive()) {
                try {
                    dialog.show();
                    return true;
                } catch (Exception e) {
                    LogUtils.print("ShowUtil", e.getMessage());
                }
            }
            if (activity.getWindow() != null && isTokenValid(activity.getWindow().getDecorView())) {
                try {
                    dialog.show();
                    return true;
                } catch (Exception e) {
                    LogUtils.print("ShowUtil", e.getMessage());
                }
            }
        }
        return false;
    }

    public static final boolean dismissDialog(Dialog dialog, Activity activity) {
        if (dialog != null && activity != null) {
            if (activity.isFinishing()) {
                return false;
            }
            if (activity.getWindow() != null && isTokenValid(activity.getWindow().getDecorView())) {
                dialog.dismiss();
                return true;
            }
        }
        return false;
    }

    /**
     * 取消dialog的方法，区别于dismiss
     *
     * @param dialog
     * @param activity
     * @return
     */
    public static final boolean cancelDialog(Dialog dialog, Activity activity) {
        if (dialog != null && activity != null) {
            if (activity.isFinishing()) {
                return false;
            }
            if (activity.getWindow() != null && isTokenValid(activity.getWindow().getDecorView())) {
                dialog.cancel();
                return true;
            }
        }
        return false;
    }

    public static final boolean dismissPopupWindow(PopupWindow window) {
        if (window != null) {
            if (isActivityFinishing(window.getContentView().getContext())) {
                return false;
            }
            if (isTokenValid(window.getContentView())) {
                window.dismiss();
                return true;
            }
        }
        return false;
    }

    public static final boolean dismissPopupWindow(PopupWindow window, Activity activity) {
        if (window != null && activity != null) {
            if (isActivityFinishing(activity)) {
                return false;
            }
            if (isTokenValid(activity.getWindow().getDecorView())) {
                window.dismiss();
                return true;
            }
            return false;
        } else {
            return dismissPopupWindow(window);
        }
    }

    public static final boolean showPopupWindowAtLocation(PopupWindow window, View parent, int gravity, int x, int y) {
        if (window != null && parent != null) {
            if (isActivityFinishing(parent.getContext())) {
                return false;
            }
            if (isTokenValid(parent)) {
                try {
                    window.showAtLocation(parent, gravity, x, y);
                    return true;
                } catch (Exception e) {
                }
            }
        }
        return false;
    }

    public static final boolean showPopupWindowAsDropDown(PopupWindow window, View anchor) {
        if (window != null && anchor != null) {
            if (isActivityFinishing(anchor.getContext())) {
                return false;
            }
            if (isTokenValid(anchor)) {
                try {
                    window.showAsDropDown(anchor);
                    return true;
                } catch (Exception e) {
                }
            }
        }
        return false;
    }

    public static final boolean showPopupWindowAsDropDown(PopupWindow window, View anchor, int xoff, int yoff) {
        if (window != null && anchor != null) {
            if (isActivityFinishing(anchor.getContext())) {
                return false;
            }
            if (isTokenValid(anchor)) {
                try {
                    window.showAsDropDown(anchor, xoff, yoff);
                    return true;
                } catch (Exception e) {
                }
            }
        }
        return false;
    }

    public static final boolean showPopupWindowAsAboveUp(PopupWindow window, View parent, int x, int y) {
        if (window != null && parent != null) {
            if (isActivityFinishing(parent.getContext())) {
                return false;
            }
            if (isTokenValid(parent)) {
                try {
                    window.showAtLocation(parent, Gravity.TOP, x, y);
                    return true;
                } catch (Exception e) {
                }
            }
        }
        return false;
    }

    public static final boolean showPopupWindowAsAboveUp(PopupWindow window, View parent, Activity activity, int x, int y) {
        if (window != null && parent != null) {
            if (isActivityFinishing(activity)) {
                return false;
            }
            if (isTokenValid(parent)) {
                try {
                    window.showAtLocation(parent, Gravity.TOP, x, y);
                    return true;
                } catch (Exception e) {
                }
            }
        }
        return false;
    }

    /**
     * 显示在底部，从底部动画弹出，可以不添加动画,animationView为添加动画的view
     */
    public static final boolean showButtonWindowStartAnimationAtBottom(PopupWindow window, View parent,
                                                                       View animationView, int layoutID) {
        if (window != null && parent != null) {
            if (isActivityFinishing(parent.getContext())) {
                return false;
            }
            if (isTokenValid(parent)) {
                try {
                    if (animationView != null) {
                        animationView.startAnimation(AnimationUtils.loadAnimation(parent.getContext(), layoutID));
                    }
                    window.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
                    return true;
                } catch (Exception e) {
                }
            }
        }
        return false;
    }

    /**
     * 隐藏window并清除动画效果
     *
     * @param window
     * @param animationView
     * @return
     */
    public static final boolean dismissPopupWindowClearAnimation(PopupWindow window, View animationView) {
        if (window != null) {
            if (isActivityFinishing(window.getContentView().getContext())) {
                return false;
            }
            if (isTokenValid(window.getContentView())) {
                if (animationView != null) {
                    animationView.clearAnimation();
                }
                window.dismiss();
                return true;
            }
        }
        return false;
    }

    public static final boolean isActivityCanShowDialogOrPopupWindow(Activity activity) {
        if (activity == null) {
            return false;
        }
        if (isActivityFinishing(activity)) {
            return false;
        }
        if (isTokenValid(activity.getWindow().getDecorView())) {
            return true;
        }
        return false;
    }

    private static final boolean isTokenValid(View view) {
        if (view != null) {
            IBinder binder = view.getWindowToken();
            if (binder != null) {
                try {
                    if (binder.isBinderAlive() && binder.pingBinder()) {
                        return true;
                    }
                } catch (Exception e) {

                }
            }
        }
        return false;
    }

    private static final boolean isActivityFinishing(Context context) {
        if (context instanceof Activity) {
            return ((Activity) context).isFinishing();
        }
        return true;
    }

}
