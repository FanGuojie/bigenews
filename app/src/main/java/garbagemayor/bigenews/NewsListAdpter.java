package garbagemayor.bigenews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static android.content.ContentValues.TAG;

public class NewsListAdpter extends RecyclerView.Adapter<NewsListAdpter.ViewHolder> {

    private Context mContext;
    private List<NewsList.ListBean> newsList = new ArrayList<>();
    private ItemNewsImageAdapter itemNewsImageAdapter = new ItemNewsImageAdapter();
    MyCallBack callback;
    private Vector<Boolean> visited;

    NewsListAdpter(Context context) {
        mContext = context;
        visited = new Vector<>();
    }
    void addData (int category) {
        callback = new MyCallBack() {
            @Override
            public void callbackCall() {
                newsList.addAll(MainActivity.pageProvider.getNewsList().getList());
                for (NewsList.ListBean item : newsList) {
                    try {
                        FileInputStream fis = mContext.openFileInput(item.getNews_ID() + ".json");
                        visited.add(true);
                    } catch (FileNotFoundException e) {
                        visited.add(false);
                    }
                }
                notifyDataSetChanged();
                itemNewsImageAdapter.notifyDataSetChanged();
            }
        };

        // TODO: category & search
        MainActivity.pageProvider.loadNewsList(category, 1, 20, callback);
    }

    void clearData() {
        newsList.clear();
        visited.clear();
        notifyDataSetChanged();
        itemNewsImageAdapter.notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView mTitleView;
        TextView mIntroView;
        TextView mTimeView;
        RecyclerView mImagesView;
        NewsList.ListBean mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = view.findViewById(R.id.title);
            mIntroView = view.findViewById(R.id.intro);
            mImagesView = view.findViewById(R.id.images);
            mTimeView = view.findViewById(R.id.time);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = newsList.get(position);
        holder.mTitleView.setText(holder.mItem.getNews_Title());
        if(visited.get(position)) {
            holder.mTitleView.setTextColor(Color.RED);
        } else {
            holder.mTitleView.setTextColor(Color.GRAY);
        }
        holder.mIntroView.setText(holder.mItem.getNews_Intro());
        String url = holder.mItem.getNews_Pictures();
        if (!url.equals("")) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            holder.mImagesView.setLayoutManager(linearLayoutManager);
            List<String> mImages = Arrays.asList(url.split(";| "));
            Log.d(TAG, "onBindViewHolder: " + mImages.size());
            for (String image : mImages) {
                Log.d(TAG, "onBindViewHolder: " + image);
            }
            ItemNewsImageAdapter itemNewsImageAdapter = new ItemNewsImageAdapter(mImages);
            Log.d(TAG, "onBindViewHolder: " + itemNewsImageAdapter.toString());
            holder.mImagesView.setAdapter(itemNewsImageAdapter);
            Log.d(TAG, "onBindViewHolder: " + mImages.size());
        }
        holder.mTimeView.setText(holder.mItem.getNews_Time());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                holder.mTitleView.setTextColor(Color.RED);
                visited.set(position, true);
                Intent intent = new Intent(context, NewsDetailActivity.class);
                intent.putExtra("category", 0);
                intent.putExtra("id", holder.mItem.getNews_ID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

}