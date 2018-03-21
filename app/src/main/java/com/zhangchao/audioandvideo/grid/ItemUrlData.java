package com.zhangchao.audioandvideo.grid;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zhangchao.audioandvideo.webview.WebViewActivity;

/**
 * Created by zhangchao on 18-3-20.
 */

public class ItemUrlData extends BaseItem<String>{
    public ItemUrlData(String title, String url) {
        super(title,url);
    }

    @Override
    public void onClick(RecyclerView parent, View view, int position, long id) {
        WebViewActivity.ViewUrl(parent.getContext(), data);
    }
}
