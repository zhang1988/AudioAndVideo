package com.example.xrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zhangchao on 18-3-16.
 */

public abstract class CommonViewHolder<T> extends RecyclerView.ViewHolder{
    public CommonViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bindData(T itemData);
}
