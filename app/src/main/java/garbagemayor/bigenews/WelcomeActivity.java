package garbagemayor.bigenews;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeActivityTag";
    private Handler mHandler;
    private ImageView mImageView;
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
        mHandler.sendEmptyMessageDelayed(0,3000);

        //图片属性
        mImageView = (ImageView) findViewById(R.id.welcome_image);
        mImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                findViewById(R.id.welcome_relative).post(new Runnable() {
                    public void run() {
                        mImageView.setMaxHeight(findViewById(R.id.welcome_relative).getHeight() - findViewById(R.id.adv_text).getHeight());
                    }
                });
            }
        });

        //夜间模式
        findViewById(R.id.welcome_relative)
                .setBackgroundColor(getResources().getColor(
                        getSharedPreferences("setting", Activity.MODE_PRIVATE)
                                .getBoolean("NightStyleOn", false)? R.color.night_background: R.color.daytime_background));
    }

    public void getHome(){
        Log.d(TAG, "getHome()");
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
