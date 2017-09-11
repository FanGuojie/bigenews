package garbagemayor.bigenews;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeActivity";
    private Handler mHandler;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity_layout);

        //隐藏状态栏
        Log.d(TAG, "隐藏状态栏");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //设置滚动条属性
        mTextView = (TextView) findViewById(R.id.adv_text);

        //欢迎界面持续3秒
        Log.d(TAG, "等待3秒");
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                getHome();
                super.handleMessage(msg);
            }
        };
        mHandler.sendEmptyMessageDelayed(0,500);
    }

    public void getHome(){
        Log.d(TAG, "getHome()");
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
