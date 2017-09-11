package garbagemayor.bigenews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageDetailActivity extends AppCompatActivity {
    String url;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_news_image);
        imageView = (ImageView) findViewById(R.id.imageView);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");

        Glide.with(this).load(url).into(imageView);
    }
}
