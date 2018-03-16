package com.miuibbs.xrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toast;

import java.util.List;

/**
 * Created by zhangchao on 18-3-15.
 */

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<CommonViewHolder> {
    private OnItemClickListener mOnItemClickListener;
    public List<T> mDataList;

    public BaseAdapter() {
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, final int position, List payloads) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewParent parent = v.getParent();
                XRecyclerView xRecyclerView;
                if (parent instanceof XRecyclerView) {
                    xRecyclerView = (XRecyclerView) parent;
                    mOnItemClickListener.onItemClick(xRecyclerView,v,position,getItemId(position));
                }
            }
        });
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),""+position,Toast.LENGTH_LONG);
                ViewParent parent = v.getParent();
                XRecyclerView xRecyclerView;
                if (parent instanceof XRecyclerView && mOnItemClickListener != null) {
                    xRecyclerView = (XRecyclerView) parent;
                    mOnItemClickListener.onItemClick(xRecyclerView,v,position,0);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mDataList == null || mDataList.isEmpty()) {
            return 0;
        }
        return mDataList.size();
    }

    public T getItemData(int position){
        return mDataList != null ? mDataList.get(position) : null;
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(RecyclerView parent, View view, int position, long id);
    }
}
