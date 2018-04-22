package com.zhangchao.audioandvideo.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.commonlib.util.LogUtils;
import com.zhangchao.audioandvideo.R;
import com.example.xrecyclerview.BaseAdapter;
import com.example.xrecyclerview.CommonViewHolder;
import com.zhangchao.audioandvideo.Util.ModelUtil;

/**
 * Created by zhangchao on 18-3-16.
 */

public class MainAdapter extends BaseAdapter<ClassInfo> {
    public MainAdapter() {
        mDataList = ModelUtil.getMainList();
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ClassInfo info = getItemData(position);
        holder.bindData(info);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        try {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false);
        }catch (Exception e){
            LogUtils.print(e.getMessage());
        }
        return new ViewHolder(view);
    }

    static class ViewHolder extends CommonViewHolder<ClassInfo> {
        public TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
        }

        @Override
        public void bindData(ClassInfo itemData) {
            if (itemData != null) {
                title.setText(itemData.titleRes);
            }
        }
    }
}
