package com.example.stickheadersortrecyclerview;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stickheadersortrecyclerview.bean.ItemBean;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: 李桐桐
 * Date: 2019-11-21
 * Description:
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<ItemBean> mDataList;

    public MyAdapter() {
        mDataList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder vh, int position) {
        vh.iv.setImageResource(mDataList.get(position).getPicRsId());
        vh.tv.setText(mDataList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void addData(List<ItemBean> datas) {
        mDataList.addAll(datas);
        notifyDataSetChanged();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView iv;
        TextView tv;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
            tv = itemView.findViewById(R.id.tv);
        }
    }
}

