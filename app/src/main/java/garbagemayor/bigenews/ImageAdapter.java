package garbagemayor.bigenews;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {
    private List<String> mImages;
    private Context mContext;
    private LinearLayoutManager linearLayoutManager;

    ImageAdapter(List<String> l, Context context) {
        mImages = l;
        mContext = context;
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
        Glide.with(mContext).load(mImages.get(position)).into(holder.mImage);
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
}
