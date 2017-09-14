package garbagemayor.bigenews;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //设置滚动条属性
        mTextView = (TextView) findViewById(R.id.adv_text);

        //欢迎界面持续3秒
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                getHome();
                super.handleMessage(msg);
            }
        };
        mHandler.sendEmptyMessageDelayed(0,500);

        findViewById(R.id.welcome_relative)
                .setBackgroundColor(getResources().getColor(
                        getSharedPreferences("setting", Activity.MODE_PRIVATE)
                                .getBoolean("NightStyleOn", false)?R.color.night_background:R.color.daytime_background));
    }

    public void getHome(){
        Log.d(TAG, "getHome()");
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}