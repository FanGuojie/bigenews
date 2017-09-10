package garbagemayor.bigenews;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<NewsList.ListBean> newsList = new ArrayList<>();
    private AdapterView.OnItemClickListener mListener;

    public void addAllData(List<NewsList.ListBean> l) {
        this.newsList.addAll(l);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.newsList.clear();
    }

    public RecyclerViewAdapter(Context context) {
        mContext = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView mTitleView;
        public NewsList.ListBean mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.title);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mTitleView.setText(newsList.get(position).getNews_Title());
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