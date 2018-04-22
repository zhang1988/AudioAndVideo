package com.zhangchao.audioandvideo;

import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public abstract class BaseActivity extends AppCompatActivity {
    protected CustomActionBar mActionBar;
    protected RelativeLayout rlRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_base);
    }

    private void initActionBar() {
        mActionBar = new CustomActionBar(this,getSupportActionBar());
        mActionBar.setLeftIconAsBack();
    }

    protected abstract void initView();

    public void setContentView(View view) {
        super.setContentView(R.layout.activity_base);
        rlRoot = (RelativeLayout) findViewById(R.id.rlRoot);
        rlRoot.addView(view);
    }


    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(R.layout.activity_base);
        rlRoot = (RelativeLayout) findViewById(R.id.rlRoot);
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        setBusinessPageView(view, null);
    }

    void setBusinessPageView(View view, ViewGroup.LayoutParams params) {
        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        rlRoot.addView(view,params);
        initActionBar();
        initView();
    }
}
