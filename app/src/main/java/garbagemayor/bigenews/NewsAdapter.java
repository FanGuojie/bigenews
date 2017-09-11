package garbagemayor.bigenews;

import android.content.Context;
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
    private List<List<String>> mImageUrlListList;

    private PopupWindow mViewBigPicturePopupWindow;
    private PhotoView mViewBigPicturePhotoView;

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
        mImageUrlListList = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PageItem pageItem = mNewsList.get(position);
        holder.mTitleTextView.setText(pageItem.getTitle());
        holder.mIntroTextView.setText(pageItem.getIntro());
        holder.mTimeTextView.setText(pageItem.getTime());
        //加载小图列表RecyclerView的属性
        Log.d(TAG, "加载小图列表RecyclerView的属性");
        mNewsImageLayoutManager = new LinearLayoutManager(mContext);
        mNewsImageLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.mImageListRecView.setLayoutManager(mNewsImageLayoutManager);
        List<String> imageUrlList = pageItem.getImageUrlList();
        mImageUrlListList.add(imageUrlList);
        mNewsImageAdapter = new NewsImageAdapter(imageUrlList);
        initBitPicture(holder, position);
        holder.mImageListRecView.setAdapter(mNewsImageAdapter);
    }

    private void initBitPicture(ViewHolder holder, final int position) {
        //设置点击图片查看大图的popupWindow的属性
        Log.d(TAG, "设置点击图片查看大图的popupWindow的属性");
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.view_big_picture_layout, null);
        mViewBigPicturePopupWindow = new PopupWindow(contentView, GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT, true);
        mViewBigPicturePopupWindow.setContentView(contentView);
        mViewBigPicturePopupWindow.setBackgroundDrawable(new ColorDrawable(0xDF000000));
        mViewBigPicturePopupWindow.setAnimationStyle(R.style.BigPictureTranslateAnimation);
        //设置大图PhotoView的特性
        Log.d(TAG, "设置大图PhotoView的特性");
        mViewBigPicturePhotoView = (PhotoView) contentView.findViewById(R.id.view_big_picture);
        mViewBigPicturePhotoView.enable();
        //设置小图点击事件：查看大图
        mNewsImageAdapter.setOnItemClickListener(new NewsImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int innerPosition) {
                String imgUrl = mImageUrlListList.get(position).get(innerPosition);
                AsynImageLoader.getnstance().showImageAsyn(mViewBigPicturePhotoView, imgUrl, R.mipmap.welcome);
                mViewBigPicturePopupWindow.showAtLocation(mViewHolder.mCardView, Gravity.CENTER, 0, 0);
            }
        });
        //设置打开大图之后的点击事件：关闭大图
        mViewBigPicturePhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewBigPicturePopupWindow.dismiss();
            }
        });
        //设置打开大图之后的长按事件：存图
        mViewBigPicturePhotoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Random random = new Random();
                String fileName = "";
                for (int i = 0; i < 16; i ++) {
                    fileName = fileName + random.nextInt(10);
                }
                fileName = fileName + ".png";
                Toast.makeText(mContext, "图片以保存到" + "sdcard/BigeNews/Download/image/" + fileName, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
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
