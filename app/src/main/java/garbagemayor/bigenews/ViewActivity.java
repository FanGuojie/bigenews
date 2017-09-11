package garbagemayor.bigenews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import garbagemayor.bigenews.newssrc.NewsItem;
import garbagemayor.bigenews.newssrc.PagePlus;

public class ViewActivity extends AppCompatActivity {

    final String TAG = "ViewActivityTag";

    NewsItem news = new NewsItem();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        TextView TitleView = (TextView) findViewById(R.id.news_view_title);
        TextView ContentView = (TextView) findViewById(R.id.news_view_content);

        Intent intent = getIntent();
        final String id = intent.getStringExtra("id");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.d(TAG, "gettingnews"+id);
                news = PagePlus.getNewsItem(id);
            }
        });
        t.start();
        try {
            t.join(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TitleView.setText(news.getTitle());
        ContentView.setText(news.getContent());

    }
}
