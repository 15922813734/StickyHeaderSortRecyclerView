package com.example.stickheadersortrecyclerview;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stickheadersortrecyclerview.bean.ItemBean;
import com.luck.library.utils.LogUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder vh, int position) {
        vh.iv.setImageResource(mDataList.get(position).getPicRsId());
        vh.tvName.setText(mDataList.get(position).getName());

        vh.tvNumber.setText(mDataList.get(position).getName() + position);
        if(position + 1 < mDataList.size() && mDataList.get(position).getLetters().equals(mDataList.get(position + 1).getLetters())) {
            vh.line.setVisibility(View.VISIBLE);
        } else {
            vh.line.setVisibility(View.GONE);
        }
        vh.tvCorrect.setText(String.format(Locale.CHINA, "批改(%d)", position + 1));
        vh.tvCorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClick(position, mDataList.get(position).getName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void addData(List<ItemBean> datas) {
        mDataList.addAll(datas);
        notifyDataSetChanged();
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = mDataList.get(i).getLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 先判断给定位置的字母是否和当前字母相同；相同返空，不同返回当前位置字母
     *
     * @param firstVisiblePosition
     * @param lastVisiblePosition
     * @param isFingerSlideUp      手指上滑，界面下滑
     * @return
     */
    public String getCurrentLetter(int firstVisiblePosition, int lastVisiblePosition, boolean isFingerSlideUp) {
        if (mDataList == null || firstVisiblePosition < 0 || firstVisiblePosition >= mDataList.size()
                || lastVisiblePosition < 0 || lastVisiblePosition >= mDataList.size()) {
            return "";
        }
        if (isFingerSlideUp) {
            if (firstVisiblePosition == 0) return mDataList.get(0).getLetters();
            if (mDataList.get(firstVisiblePosition).getLetters().equals(mDataList.get(firstVisiblePosition - 1).getLetters())) {
                return "";
            }
        } else {
            if (firstVisiblePosition + 1 >= mDataList.size())
                return mDataList.get(firstVisiblePosition).getLetters();
            if (mDataList.get(firstVisiblePosition).getLetters().equals(mDataList.get(firstVisiblePosition + 1).getLetters())) {
                return "";
            }
        }
        return mDataList.get(firstVisiblePosition).getLetters();
    }

    public interface onItemViewClickListener {
        void onClick(int position, String s);
    }

    private onItemViewClickListener mListener;

    public void setOnItemViewClickListener(onItemViewClickListener mListener) {
        this.mListener = mListener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView iv;
        TextView tvName;
        TextView tvNumber;
        TextView tvCorrect;
        View line;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
            tvName = itemView.findViewById(R.id.tvName);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            tvCorrect = itemView.findViewById(R.id.tvCorrect);
            line = itemView.findViewById(R.id.line);
        }
    }
}

