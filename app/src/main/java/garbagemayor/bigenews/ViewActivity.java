package garbagemayor.bigenews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import at.markushi.ui.CircleButton;
import garbagemayor.bigenews.newssrc.NewsItem;
import garbagemayor.bigenews.newssrc.PagePlus;

public class ViewActivity extends AppCompatActivity {

    final String TAG = "ViewActivityTag";

    NewsItem news = new NewsItem();



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        CircleButton btdl = (CircleButton) findViewById(R.id.button_download);
        CircleButton btfavor = (CircleButton) findViewById(R.id.button_favor);
        CircleButton btshare = (CircleButton) findViewById(R.id.button_share);



        TextView TitleView = (TextView) findViewById(R.id.news_view_title);
        TextView TimeView = (TextView) findViewById(R.id.news_view_time);
        TextView ContentView = (TextView) findViewById(R.id.news_view_content);
        TextView AuthorView = (TextView) findViewById(R.id.news_view_Author);
        TextView JournalView = (TextView) findViewById(R.id.news_view_Journal);

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
        TimeView.setText(news.getTime());
        ContentView.setText(news.getContent());
        AuthorView.setText(news.getAuthor());
        JournalView.setText(news.getJournal());

        btdl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btfavor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {Intent intent1=new Intent(Intent.ACTION_SEND);
                intent1.putExtra(Intent.EXTRA_TEXT, news.getTitle() + "\n" + "http://166.111.68.66:2042/news/action/query/NewsItem?newsId=" + id);
                intent1.setType("text/plain");
                startActivity(Intent.createChooser(intent1,"share"));
            }
        });

    }


}
