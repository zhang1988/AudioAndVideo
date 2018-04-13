package com.zhangchao.audioandvideo.grid;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zhangchao on 18-3-20.
 */

public class ItemBtnData extends BaseItem<ItemBtnData.onItemBtnClickListener> {
    public ItemBtnData(String title, ItemBtnData.onItemBtnClickListener listener) {
        super(title, listener);
    }

    @Override
    public void onClick(RecyclerView parent, View view, int position, long id) {
        data.onItemBtnClick(view, title);
    }

    /**
     *
     */
    public static interface onItemBtnClickListener{
        void onItemBtnClick(View view,String key);
    }
}
