package garbagemayor.bigenews;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import at.markushi.ui.CircleButton;
import garbagemayor.bigenews.newssrc.NewsItem;
import garbagemayor.bigenews.newssrc.PagePlus;

public class ViewActivity extends AppCompatActivity {

    final String TAG = "ViewActivityTag";

    int textlen = 100;

    private NewsItem news = new NewsItem();
    private Html.ImageGetter imgGetter;
    private Bitmap bitmap;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_detail_activity_layout);

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
        final String[] picsurl = intent.getStringArrayExtra("pictures");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                //Log.d(TAG, "gettingnews"+id);
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


        /*ContentView.setText(Html.fromHtml(reconsitution(),
                new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(final String source) {
                        //这里分割了 http://localhost:8080/img/ddd.jpg
                        Log.d(TAG, "pictures:" + source);
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //Log.d(TAG, "gettingnews"+id);
                                bitmap = getBitmapFromUrl(source);
                            }
                        });
                        t.start();
                        try {
                            t.join(5000);
                            int width = getWindowManager().getDefaultDisplay().getWidth();
                            Drawable drawable = new BitmapDrawable(bitmap);
                            drawable.setBounds(0, 0, width, width/drawable.getIntrinsicWidth()*drawable.getIntrinsicHeight());
                            return drawable;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return null;
                        }

                    }
                }, null));*/

        ContentView.setMovementMethod(LinkMovementMethod.getInstance());
        HtmlImageGetter ReviewImgGetter = new HtmlImageGetter(ViewActivity.this, ContentView);//实例化URLImageGetter类
        ContentView.setText(Html.fromHtml(reconsitution(),ReviewImgGetter,null));
    }

    private String reconsitution(){
        String[] urls = news.getPictures().split(";| ");
        String content = news.getContent();
        Log.d(TAG, content);
        String result = "\t";
        if (urls.length<2)
            return result + content;
        //result+=content+"\n";
        int len = content.length() / textlen;
        for (int i=0; i<urls.length; i++){
            if (i < len)
                result += content.substring(i*textlen, i*textlen+textlen) + "\n";
            else if (i == len)
                result += content.substring(i*textlen) + "\n";
            result += "<img src='" + urls[i] + "'>";

            Log.d(TAG, urls[i]);
        }
        if (urls.length >= len)
            result += content.substring((urls.length-1)*textlen) + "\n";
        Log.d(TAG, result);
        return result;
    }

    private static Bitmap getBitmapFromUrl(String urlStr) {
        Bitmap bitmap = null;
        try {
            URL imgUrl = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
            conn.setDoInput(true);
            conn.setConnectTimeout(2000);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public Bitmap resizeBitmap(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        //放大為屏幕的1/2大小
        float screenWidth  = getWindowManager().getDefaultDisplay().getWidth();     // 屏幕宽（像素，如：480px）
        //float screenHeight = getWindowManager().getDefaultDisplay().getHeight();        // 屏幕高（像素，如：800p）
        Log.d("screen",screenWidth+"");
        float scaleWidth = screenWidth/width;
        //float scaleHeight = screenWidth/2/width;

        // 取得想要缩放的matrix參數
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);
        // 得到新的圖片
        Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,true);
        return newbm;
    }


}
