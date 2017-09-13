package garbagemayor.bigenews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GravityCompat;
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
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import at.markushi.ui.CircleButton;
import garbagemayor.bigenews.newssrc.NewsItem;
import garbagemayor.bigenews.newssrc.PagePlus;

public class ViewActivity extends AppCompatActivity {

    public static final String TAG = "ViewActivityTag";
    private SpeechSynthesizer mTts;
    private boolean mTtsInited;
    private boolean mTtsPaused;
    private static int textlen = 100;

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
                // Do something in response to button
                voice_playback(view);
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

    private void voice_playback(View view) {
        if (!mTtsInited) {
            //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
            //2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
            mTts = SpeechSynthesizer.createSynthesizer(ViewActivity.this, null);
            if (mTts != null) {
                mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
                mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
                mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
                mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
                //设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
                //保存在SD卡需要在AndroidManifest.xml添加写SD卡权限
                //如果不需要保存合成音频，注释该行代码
//                mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
                mTtsInited = true;
            } else {
                Toast.makeText(ViewActivity.this,
                        "语音合成初始化失败", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (!mTts.isSpeaking()) {
            //3.开始合成
            int ret = mTts.startSpeaking(news.getContent(), mSynListener);
            if (ret != ErrorCode.SUCCESS) {
                Toast.makeText(ViewActivity.this,
                        "语音合成失败，错误码: " + ret,
                        Toast.LENGTH_SHORT).show();
            } else {
                //TODO 改按钮图
            }
        } else if (mTtsPaused) {
            mTts.resumeSpeaking();
            //TODO 改按钮图
            mTtsPaused = false;
        } else {
            mTts.pauseSpeaking();
            //TODO 改按钮图
            mTtsPaused = true;
        }
    }

    @Override
    public void onBackPressed() {
        mTts.stopSpeaking();
//        Toast.makeText(ViewActivity.this,
//                "停止播放...", Toast.LENGTH_SHORT).show();
        finish();
    }

    //合成监听器
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
            if (error == null) {
                Log.d("mTts", "播放完成");
                Toast.makeText(ViewActivity.this,
                        "播放完成", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("mTts", error.getPlainDescription(true));
                Toast.makeText(ViewActivity.this,
                        error.getPlainDescription(true), Toast.LENGTH_SHORT).show();
            }
        }

        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        }

        //开始播放
        public void onSpeakBegin() {
            Toast.makeText(ViewActivity.this,
                    "朗读中...", Toast.LENGTH_SHORT).show();
        }

        //暂停播放
        public void onSpeakPaused() {
            Toast.makeText(ViewActivity.this,
                    "暂停播放...", Toast.LENGTH_SHORT).show();
        }

        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        //恢复播放回调接口
        public void onSpeakResumed() {
            Toast.makeText(ViewActivity.this,
                    "继续播放...", Toast.LENGTH_SHORT).show();
        }

        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }
    };


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
