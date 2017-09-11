package garbagemayor.bigenews;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import garbagemayor.bigenews.newssrc.PageItem;

/*
 *  RecyclerView嵌套CardView的适配器
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private Context mContext;
    private List<PageItem> mNewsList;
    private ViewHolder mViewHolder;
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    public static interface OnItemClickListener {
        public void onItemClick(View view, int postion);
    }

    public static interface OnItemLongClickListener {
        public void onItemLongClick(View view, int postion);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        private CardView cardView;
        private TextView newsTitle;
        private TextView newsIntro;
        private TextView newsTime;
        private OnItemClickListener mListener;
        private OnItemLongClickListener mLongClickListener;

        public ViewHolder(View view, OnItemClickListener listener, OnItemLongClickListener longClickListener) {
            super(view);
            cardView = (CardView) view;
            newsTitle = (TextView) view.findViewById(R.id.news_title);
            newsIntro = (TextView) view.findViewById(R.id.news_intro);
            newsTime = (TextView) view.findViewById(R.id.news_time);
            this.mListener = listener;
            this.mLongClickListener = longClickListener;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
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

    public NewsAdapter(List<PageItem> newsList) {
        mNewsList = newsList;
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PageItem pageItem = mNewsList.get(position);
        holder.newsTitle.setText(pageItem.getTitle());
        holder.newsIntro.setText(pageItem.getIntro());
        holder.newsTime.setText(pageItem.getTime());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.news_item_layout, parent, false);
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
