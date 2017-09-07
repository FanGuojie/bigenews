package garbagemayor.bigenews;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/*
 *  RecyclerView嵌套CardView的适配器
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private Context mContext;

    private List<NewsItem> mNewsList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView newsTitle;
        TextView newsContent;
        TextView newsTime;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            newsTitle = (TextView) view.findViewById(R.id.news_title);
            newsContent = (TextView) view.findViewById(R.id.news_content);
            newsTime = (TextView) view.findViewById(R.id.news_time);
        }
    }

    public NewsAdapter(List<NewsItem> newsList) {
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
        NewsItem newsItem = mNewsList.get(position);
        holder.newsTitle.setText(newsItem.getTitle());
        holder.newsContent.setText(newsItem.getContent());
        holder.newsTime.setText(newsItem.getTime());
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }
}
