package garbagemayor.bigenews;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import garbagemayor.bigenews.newssrc.PageItem;

/*
 *  新闻浏览的RecyclerView的每一条新闻的嵌套的CardView的适配器
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    public static String TAG = "NewsAdapterTag";

    private Context mContext;
    private List<PageItem> mNewsList;
    private ViewHolder mViewHolder;
    private LinearLayoutManager  mNewsImageLayoutManager;
    private NewsImageAdapter mNewsImageAdapter;
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
        private RecyclerView newsImageList;
        private TextView newsTime;
        private OnItemClickListener mListener;
        private OnItemLongClickListener mLongClickListener;

        public ViewHolder(View view, OnItemClickListener listener, OnItemLongClickListener longClickListener) {
            super(view);
            cardView = (CardView) view;
            newsTitle = (TextView) view.findViewById(R.id.news_title);
            newsIntro = (TextView) view.findViewById(R.id.news_intro);
            newsImageList = (RecyclerView) view.findViewById(R.id.news_image_list);
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
        final PageItem pageItem = mNewsList.get(position);
        holder.newsTitle.setText(pageItem.getTitle());
        holder.newsIntro.setText(pageItem.getIntro());
        holder.newsTime.setText(pageItem.getTime());
        Log.d(TAG, "加载新闻图片");
        mNewsImageLayoutManager = new LinearLayoutManager(mContext);
        mNewsImageLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.newsImageList.setLayoutManager(mNewsImageLayoutManager);
        Thread t =  new Thread(new Runnable() {
            @Override
            public void run() {
                /*
                List<Bitmap> imgList = pageItem.getImageList();
                Log.d(TAG, "imgList.size() = " + imgList.size());
                mNewsImageAdapter = new NewsImageAdapter(imgList);
                */
                List<String> imgUrlList = pageItem.getImageUrlList();
                Log.d(TAG, "imgUrlList.size() = " + imgUrlList.size());
                mNewsImageAdapter = new NewsImageAdapter(imgUrlList);
            }
        });
        t.start();
        try {
            t.join(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "setAdapter");
        holder.newsImageList.setAdapter(mNewsImageAdapter);
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
