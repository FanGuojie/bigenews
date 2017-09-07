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

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView newsTitle;
        TextView newsIntro;
        TextView newsTime;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            newsTitle = (TextView) view.findViewById(R.id.news_title);
            newsIntro = (TextView) view.findViewById(R.id.news_intro);
            newsTime = (TextView) view.findViewById(R.id.news_time);
        }
    }

    public NewsAdapter(List<PageItem> newsList) {
        mNewsList = newsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.news_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PageItem pageItem = mNewsList.get(position);
        holder.newsTitle.setText(pageItem.getTitle());
        holder.newsIntro.setText(pageItem.getIntro());
        holder.newsTime.setText(pageItem.getTime());
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }
}
