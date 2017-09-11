package garbagemayor.bigenews;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewsListViewAdapter extends RecyclerView.Adapter<NewsListViewAdapter.ViewHolder> {

    private Context mContext;
    private List<NewsList.ListBean> newsList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    MyCallBack callback;

    void addData (int category) {
        callback = new MyCallBack() {
            @Override
            public void callbackCall() {
                newsList.addAll(MainActivity.pageProvider.getNewsList().getList());
                notifyDataSetChanged();
            }
        };

        // TODO: category & search
        MainActivity.pageProvider.loadNewsList(category, 1, 20, callback);
    }

    void clearData() {
        newsList.clear();
    }

    NewsListViewAdapter(Context context) {
        mContext = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView mTitleView;
        RecyclerView mImagesView;
        NewsList.ListBean mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = view.findViewById(R.id.title);
            mImagesView = view.findViewById(R.id.images);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.mImagesView.setLayoutManager(linearLayoutManager);
        holder.mTitleView.setText(newsList.get(position).getNews_Title());
        String url = newsList.get(position).getNews_Pictures();
        if (!url.equals("")) {
            ImageAdapter adapter = new ImageAdapter(Arrays.asList(url.split(";")), mContext);
            holder.mImagesView.setAdapter(adapter);
        }
        holder.mItem = newsList.get(position);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
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