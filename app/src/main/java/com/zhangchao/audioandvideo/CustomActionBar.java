package com.zhangchao.audioandvideo;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * CustomActionBar
 *
 * @author zhangchao
 * @date 2018/3/17-下午6:06
 * @desc 自定义ActionBar
 */
public class CustomActionBar {
    private ActionBar mActionBar;
    private View root;
    private TextView tvLeftTitle;
    private TextView tvRight;
    private TextView tvMidTitle;

    private ImageView ivLeft;
    private ImageView ivRight;
    private BaseActivity mActivity;


    public CustomActionBar(BaseActivity activity, ActionBar actionBar) {
        mActionBar = actionBar;
        mActivity = activity;
        init();
    }

    private void init() {
        mActionBar.setCustomView(R.layout.actinbar);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        root = mActionBar.getCustomView();
        tvLeftTitle = (TextView) root.findViewById(R.id.tvLeftTitle);
        tvMidTitle = (TextView) root.findViewById(R.id.tvMidTitle);
        tvRight = (TextView) root.findViewById(R.id.tvRightTitle);
        ivLeft = (ImageView) root.findViewById(R.id.ivLeft);
        ivRight = (ImageView) root.findViewById(R.id.ivRight);
    }

    public void setLeftIconAsBack() {
        ivLeft.setImageResource(R.mipmap.icon_home_back);
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });
    }

    public void setLeftTitle(String title) {
        tvLeftTitle.setText(title);
    }

    public void setLeftTitle(int titleRes) {
        tvLeftTitle.setText(titleRes);
    }

    public void setTitle(String title) {
        tvMidTitle.setText(title);
    }

    public void setTitle(int titleRes) {
        tvMidTitle.setText(titleRes);
    }

    public void setRightTitle(String s) {
        tvRight.setText(s);
    }

    public void setRightTitle(int res) {
        tvRight.setText(res);
    }

    public void setRightIcon(int res) {
        ivRight.setImageResource(res);
    }
}
