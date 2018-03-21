package com.zhangchao.audioandvideo.grid;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zhangchao on 18-3-20.
 */

public class ItemBtnData extends BaseItem<View.OnClickListener>{
    public ItemBtnData(String title, View.OnClickListener listener) {
        super(title, listener);
    }

    @Override
    public void onClick(RecyclerView parent, View view, int position, long id) {
        data.onClick(view);
    }
}
