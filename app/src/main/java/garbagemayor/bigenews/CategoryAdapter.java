package garbagemayor.bigenews;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mCategoryList;

    static public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mText;

        public ViewHolder(View view) {
            super(view);
            mText = (TextView) view.findViewById(R.id.category_text);
        }
    }

    public CategoryAdapter(List<String> categoryList) {
        mCategoryList = categoryList;
    }

    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.category_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String text = mCategoryList.get(position);
        holder.mText.setText(text);
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }
}
