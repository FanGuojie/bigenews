package garbagemayor.bigenews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import java.util.ArrayList;

public class NewsDetailActivity extends AppCompatActivity {

    private SpeechSynthesizer mTts;
    private boolean mTtsInited;
    private boolean mTtsPaused;
    MyCallBack callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        String news_id = intent.getStringExtra("id");

        callback = new MyCallBack() {
            @Override
            public void callbackCall() {
                TextView textView = (TextView) findViewById(R.id.textView);
                TextView titleView = (TextView) findViewById(R.id.titleView);
                TextView timeView = (TextView) findViewById(R.id.timeView);
                titleView.setText(MainActivity.pageProvider.getNewsDetail().getNews_Title());
                timeView.setText(MainActivity.pageProvider.getNewsDetail().getNews_Time());
                textView.setText(MainActivity.pageProvider.getNewsDetail().getNews_Content());
            }
        };
        MainActivity.pageProvider.loadNewsDetail(news_id, callback);
//        ObservableListView listView = (ObservableListView) findViewById(R.id.viewPager);
//        ObservableListView listView = (ObservableListView) findViewById(R.id.listView);
//        listView.setScrollViewCallbacks(this);
//        ArrayList<String> items = new ArrayList<String>();
//        items.add(news_id);
//        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));
    }

    @Override
    public void onBackPressed() {
        if (mTts != null && mTts.isSpeaking()) {
            mTts.stopSpeaking();
            Toast.makeText(NewsDetailActivity.this,
                    "停止播放...", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    public void voice_playback() {
        if (!mTtsInited) {
            //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
            //2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
            mTts = SpeechSynthesizer.createSynthesizer(NewsDetailActivity.this, null);
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
                Toast.makeText(NewsDetailActivity.this,
                        "语音合成初始化失败", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (!mTts.isSpeaking()) {
            //3.开始合成
            int ret = mTts.startSpeaking(MainActivity.pageProvider.getNewsDetail().getNews_Content(), mSynListener);
            if (ret != ErrorCode.SUCCESS) {
                Toast.makeText(NewsDetailActivity.this,
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

    //合成监听器
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
            if (error == null) {
                Log.d("mTts", "播放完成");
                Toast.makeText(NewsDetailActivity.this,
                        "播放完成", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("mTts", error.getPlainDescription(true));
                Toast.makeText(NewsDetailActivity.this,
                        error.getPlainDescription(true), Toast.LENGTH_SHORT).show();
            }
        }

        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        }

        //开始播放
        public void onSpeakBegin() {
            Toast.makeText(NewsDetailActivity.this,
                    "朗读中...", Toast.LENGTH_SHORT).show();
        }

        //暂停播放
        public void onSpeakPaused() {
            Toast.makeText(NewsDetailActivity.this,
                    "暂停播放...", Toast.LENGTH_SHORT).show();
        }

        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        //恢复播放回调接口
        public void onSpeakResumed() {
            Toast.makeText(NewsDetailActivity.this,
                    "继续播放...", Toast.LENGTH_SHORT).show();
        }

        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_voice:
                voice_playback();
        }
        return true;
    }
}
