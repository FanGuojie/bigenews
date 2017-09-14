package garbagemayor.bigenews;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import static android.content.ContentValues.TAG;

public class ItemNewsImageAdapter extends RecyclerView.Adapter<ItemNewsImageAdapter.MyViewHolder> {
    private List<String> mImages;
    ItemNewsImageAdapter() {}

    ItemNewsImageAdapter(List<String> urls) {
        mImages = urls;
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView mImage;
        private String mItem;
        private View mView;
        MyViewHolder(View view)
        {
            super(view);
            mView = view;
            mImage = (ImageView) view.findViewById(R.id.imageView);
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_image, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {
        Glide
                .with(holder.mView.getContext())
                .load(mImages.get(position))
                .placeholder(R.mipmap.loading)
                .into(holder.mImage);
        holder.mItem = mImages.get(position);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, ImageDetailActivity.class);
                intent.putExtra("url", holder.mItem);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mImages.size();
    }

}
