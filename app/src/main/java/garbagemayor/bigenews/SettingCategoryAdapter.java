package garbagemayor.bigenews;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator_nodgd on 2017/09/14.
 */

public class SettingCategoryAdapter extends RecyclerView.Adapter<SettingCategoryAdapter.ViewHolder> {

    public static String TAG = "SettingCategoryAdapterTag";

    private Context mContext;
    private List<String> mAllCategoryList;
    private ViewHolder mViewHolder;
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    public static interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }
    public static interface OnItemLongClickListener {
        public void onItemLongClick(View view, int position);
    }
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private RelativeLayout mRelativeLayout;
        private TextView mTextView;
        private CheckBox mCheckBox;
        private OnItemClickListener mListener;
        private OnItemLongClickListener mLongClickListener;

        public ViewHolder(View view, OnItemClickListener listener, OnItemLongClickListener longClickListener) {
            super(view);
            mRelativeLayout = (RelativeLayout) view;
            mTextView = (TextView) view.findViewById(R.id.setting_category_item_textview);
            mCheckBox = (CheckBox) view.findViewById(R.id.setting_category_item_checkbox);
            this.mListener = listener;
            this.mLongClickListener = longClickListener;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mListener != null) {
                mListener.onItemClick(view, getPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if(mLongClickListener != null) {
                mLongClickListener.onItemLongClick(view, getPosition());
            }
            return true;
        }
    }

    public SettingCategoryAdapter(List<String> allCategoryList) {
        mAllCategoryList = allCategoryList;
    }

    @Override
    public int getItemCount() {
        return mAllCategoryList.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        if(mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.setting_category_item_layout, parent, false);
        mViewHolder = new ViewHolder(view, mItemClickListener, mItemLongClickListener);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        String option = mAllCategoryList.get(position);
        holder.mTextView.setText(option);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        holder.mCheckBox.setChecked(sharedPreferences.getBoolean(option, true));
        //点击修改之后在SharedPreferences里面也修改
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d(TAG, "Option: " + holder.mTextView.getText() + "    ChangeTo : " + b);
                SharedPreferences sharedPreferences = mContext.getSharedPreferences("setting", Activity.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean((String) holder.mTextView.getText(), b).apply();
            }
        });
    }
}
