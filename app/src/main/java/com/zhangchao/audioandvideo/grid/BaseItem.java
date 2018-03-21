package com.zhangchao.audioandvideo.grid;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zhangchao on 18-3-20.
 */

public abstract class BaseItem<T> {
    String title;
    T data;

    public BaseItem(String title, T data) {
        this.title = title;
        this.data = data;
    }

    /**
     * 当gridview的item被点击的时候调用，子类根据具体的情况实现
     */
    public abstract void onClick(RecyclerView parent, View view, int position, long id);
}
