package garbagemayor.bigenews;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import garbagemayor.bigenews.newssrc.PageItem;

/*
 *  新闻浏览的RecyclerView每一条新闻的嵌套的CardView里面横向图片列表的适配器
 */
public class NewsImageAdapter extends RecyclerView.Adapter<NewsImageAdapter.ViewHolder> {

    public static String TAG = "NewsImageAdapterTag";

    private Context mContext;
    private List<String> mImgUrlList;
    private ViewHolder mViewHolder;
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    public static interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public static interface OnItemLongClickListener {
        public void onItemLongClick(View view, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        private ImageView mImageView;
        private OnItemClickListener mListener;
        private OnItemLongClickListener mLongClickListener;

        public ViewHolder(View view, OnItemClickListener listener, OnItemLongClickListener longClickListener) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.news_image);
            this.mListener = listener;
            this.mLongClickListener = longClickListener;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        public ImageView getImageView() {
            return mImageView;
        }

        @Override
        public void onClick(View view) {
            if(mListener != null){
                mListener.onItemClick(view, getPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if(mLongClickListener != null){
                mLongClickListener.onItemLongClick(view, getPosition());
            }
            return true;
        }
    }

    public NewsImageAdapter(List<String> imgUrlList) {
        mImgUrlList = imgUrlList;
    }

    @Override
    public int getItemCount() {
        return mImgUrlList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String imgUrl = mImgUrlList.get(position);
        //holder.mImageView.setImageBitmap(bitmap);
        AsynImageLoader.getnstance().showImageAsyn(holder.mImageView, imgUrl, R.mipmap.loading);
        Log.d(TAG, "加载图片");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.news_image_item_layout, parent, false);
        mViewHolder = new ViewHolder(view, mItemClickListener, mItemLongClickListener);
        return mViewHolder;
    }

    //RecyclerView不带有这两个函数，就自己添加
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        this.mItemLongClickListener = listener;
    }
}
