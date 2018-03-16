package com.zhangchao.audioandvideo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.miuibbs.xrecyclerview.BaseAdapter;
import com.miuibbs.xrecyclerview.XRecyclerView;


public class MainActivity extends AppCompatActivity {

    public XRecyclerView xRecyclerView;

    private MainAdapter mAdapter;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initRecyclerView();
    }

    private void initRecyclerView(){
        mAdapter = new MainAdapter();
        xRecyclerView = (XRecyclerView) findViewById(R.id.xRecyclerView);
        xRecyclerView.setDefaultLayoutManager();
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingEnabled(false);
        mAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                ClassInfo info = mAdapter.getItemData(position);
                if (info != null) {
                    Intent intent = new Intent(MainActivity.this, info.targetClass);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
