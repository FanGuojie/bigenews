package garbagemayor.bigenews;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bm.library.PhotoView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    private BigPicturePopupWindow mBigPicture;

    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    public static interface OnItemClickListener {
        public void onItemClick(View view, int postion);
    }

    public static interface OnItemLongClickListener {
        public void onItemLongClick(View view, int postion);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        private CardView mCardView;
        private TextView mTitleTextView;
        private TextView mIntroTextView;
        private RecyclerView mImageListRecView;
        private TextView mTimeTextView;
        private OnItemClickListener mListener;
        private OnItemLongClickListener mLongClickListener;

        public ViewHolder(View view, OnItemClickListener listener, OnItemLongClickListener longClickListener) {
            super(view);
            mCardView = (CardView) view;
            mTitleTextView = (TextView) view.findViewById(R.id.news_title);
            mIntroTextView = (TextView) view.findViewById(R.id.news_intro);
            mImageListRecView = (RecyclerView) view.findViewById(R.id.news_image_list);
            mTimeTextView = (TextView) view.findViewById(R.id.news_time);
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
        holder.mTitleTextView.setText(pageItem.getTitle());


        SharedPreferences sharedPreferences = mContext.getSharedPreferences("visited", Activity.MODE_PRIVATE);
        Log.d(TAG, pageItem.getTitle() + position);
        if(sharedPreferences.getBoolean(pageItem.getTitle(), false)) {
            Log.d(TAG, "RED");
            holder.mTitleTextView.setTextColor(mContext.getResources().getColor(R.color.main_newscard_title_visited));
        } else {
            holder.mTitleTextView.setTextColor(mContext.getResources().getColor(R.color.main_newscard_title));

        }



        holder.mIntroTextView.setText(pageItem.getIntro());
        holder.mTimeTextView.setText(pageItem.getTime());
        //加载小图列表RecyclerView的属性
        Log.d(TAG, "加载小图列表RecyclerView的属性");
        mNewsImageLayoutManager = new LinearLayoutManager(mContext);
        mNewsImageLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.mImageListRecView.setLayoutManager(mNewsImageLayoutManager);
        //设置小图列表的各种事件
        mNewsImageAdapter = new NewsImageAdapter(pageItem.getImageUrlList());
        if(mBigPicture == null) {
            mBigPicture = new BigPicturePopupWindow(mContext, mViewHolder.mCardView);
        }
        //小图点击事件：查看大图
        mNewsImageAdapter.setOnItemClickListener(new NewsImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int innerPosition) {
                mBigPicture.show((ImageView) ((LinearLayout) view).getChildAt(0));
            }
        });
        holder.mImageListRecView.setAdapter(mNewsImageAdapter);
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
