package com.zhangchao.audioandvideo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public abstract class BaseActivity extends AppCompatActivity {
    protected CustomActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_base);
        initActionBar();
    }

    private void initActionBar() {
        mActionBar = new CustomActionBar(this,getSupportActionBar());
        mActionBar.setLeftIconAsBack();
    }
}
