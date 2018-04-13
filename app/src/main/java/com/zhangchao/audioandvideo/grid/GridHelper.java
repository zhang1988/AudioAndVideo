package com.zhangchao.audioandvideo.grid;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xrecyclerview.BaseAdapter;
import com.example.xrecyclerview.CommonViewHolder;
import com.example.xrecyclerview.XRecyclerView;
import com.zhangchao.audioandvideo.R;
import com.zhangchao.audioandvideo.webview.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangchao on 18-3-20.
 */

public class GridHelper {
    private AppCompatActivity mActivity;
    private XRecyclerView xRecyclerView;
    private GridAdapter mAdapter;

    public GridHelper(AppCompatActivity activity) {
        mActivity = activity;
        initGridView();

    }

    public static GridHelper newInstance(AppCompatActivity activity) {
        return new GridHelper(activity);
    }

    public GridHelper addItem(BaseItem item){
        mAdapter.addItem(item);
        return this;
    }

    public void notifyDataAddCompleted(){
        int count = mAdapter.getItemCount();
        if (count < 4) {
            xRecyclerView.setGridLayoutManager(count);
        } else {
            xRecyclerView.setGridLayoutManager(3);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void initGridView() {
        xRecyclerView = (XRecyclerView) mActivity.findViewById(R.id.grid);
        xRecyclerView.setLoadingEnabled(false);
        //xRecyclerView.setGridLayoutManager(4);
        mAdapter = new GridAdapter();
        mAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                BaseItem itemData = mAdapter.getItemData(position);
                itemData.onClick(parent, view ,position, id);
            }
        });
        xRecyclerView.setAdapter(mAdapter);
    }

    private static class GridAdapter extends BaseAdapter<BaseItem> {

        public GridAdapter() {
            mDataList = new ArrayList<>(4);
        }

        public void addItem(BaseItem item){
            mDataList.add(item);
        }

        @Override
        public void onBindViewHolder(CommonViewHolder holder, int position, List payloads) {
            super.onBindViewHolder(holder, position, payloads);
        }

        @Override
        public void onBindViewHolder(CommonViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
        }

        @Override
        public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GridViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false));
        }


        public static class GridViewHolder extends CommonViewHolder<BaseItem> {
            private TextView tvTitle;

            public GridViewHolder(View itemView) {
                super(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            }

            @Override
            public void bindData(BaseItem item) {
                if (item == null) {
                    return;
                }
                itemView.setTag(item.title);//onclick使用
                tvTitle.setText(item.title);
            }
        }
    }
}
