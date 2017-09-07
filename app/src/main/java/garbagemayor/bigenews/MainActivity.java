package garbagemayor.bigenews;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //上次点击“退出”按钮的时间
    private Date lastPressQuit = null;
    private boolean isLoading = false;
    private int visibleThreshold = 5;

    //新闻内容
    private List<NewsItem> mNewsList = new ArrayList<>();
    private NewsAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        //用ToolBar代替ActionBar
        initToolBar();

        //侧滑菜单里的按钮的行为
        initNavigationView();

        //新闻模块的显示，下拉刷新，自动加载
        initShowNews();
        //填充新闻内容
        refreshNews();

        //悬浮按钮
        initBackToTopButtom();
    }

    //用ToolBar代替ActionBar
    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer_home);
        }
    }
    //显示侧滑菜单
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //侧滑
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }
    //侧滑菜单里的按钮的行为
    private void initNavigationView() {

        NavigationView navView = (NavigationView)findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.nav_homepage);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    //进入编辑用户资料的界面
                    case R.id.nav_head_portrait:
                        Toast.makeText(MainActivity.this, "这部分代码还没写", Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        break;
                    //暂时没想到有什么骚操作
                    case R.id.nav_user_name:
                        mDrawerLayout.closeDrawers();
                        break;
                    //回到主页
                    case R.id.nav_homepage:
                        mDrawerLayout.closeDrawers();
                        break;
                    //查看历史
                    case R.id.nav_history:
                        Toast.makeText(MainActivity.this, "这部分代码还没写", Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        break;
                    //进入收藏夹
                    case R.id.nav_favorite:
                        Toast.makeText(MainActivity.this, "这部分代码还没写", Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        break;
                    //进入设置界面
                    case R.id.nav_setting:
                        Toast.makeText(MainActivity.this, "这部分代码还没写", Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        break;
                    //连按两次退出程序
                    case R.id.nav_quit:pressQuit();
                        break;
                    default:
                }
                return true;
            }
        });
    }

    //按退出键也要连按两次
    @Override
    public void onBackPressed() {
        pressQuit();
    }

    //连按两次退出的逻辑实现
    private void pressQuit() {
        if (lastPressQuit == null) {
            lastPressQuit = new Date();
            Toast.makeText(MainActivity.this, "连按两次退出程序", Toast.LENGTH_SHORT).show();
        } else {
            Date now = new Date();
            if (lastPressQuit.after(now)) {
                Log.d(TAG, "获取系统时间发生错误");
                Toast.makeText(MainActivity.this, "获取系统时间发生错误", Toast.LENGTH_SHORT).show();
                finish();
            } else if (now.getTime() - lastPressQuit.getTime() < 1000) {
                finish();
            } else {
                Toast.makeText(MainActivity.this, "连按两次退出程序", Toast.LENGTH_SHORT).show();
                lastPressQuit = now;
            }
        }
    }

    //新闻显示模块
    private void initShowNews() {
        //RecyclerView里面初始化
        mRecyclerView = (RecyclerView) findViewById(R.id.main_news_list);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new NewsAdapter(mNewsList);
        mRecyclerView.setAdapter(mAdapter);

        //靠近底部自动加载功能
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(isLoading)
                    return;
                int itemCount = mLayoutManager.getItemCount();
                int[] lastPosList = mLayoutManager.findLastVisibleItemPositions(null);
                int lastPos = Integer.MIN_VALUE;
                for(int i = 0; i < lastPosList.length; i ++) {
                    if(lastPos < lastPosList[i]) {
                        lastPos = lastPosList[i];
                    }
                }
                if(lastPos >= (itemCount - visibleThreshold)) {
                    loadNewNews(20);
                }
            }
        });
        //下拉自动刷新功能
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshNews();
            }
        });
    }
    //新闻刷新
    private void refreshNews() {
        mSwipeRefreshLayout.setRefreshing(true);
        isLoading = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mNewsList.clear();
                        mAdapter.notifyDataSetChanged();
                        loadNewNews(20);
                        mSwipeRefreshLayout.setRefreshing(false);
                        isLoading = false;
                    }
                });
            }
        }).start();
    }
    //向RecyclerView里加入新的新闻
    private void loadNewNews(int cnt) {
        isLoading = true;
        int n = mNewsList.size();
        for(int i = 0; i < cnt; i ++) {
            NewsItem news = getOneNews(n + i);
            mNewsList.add(n + i, news);
            mAdapter.notifyItemInserted(n + i);
        }
        isLoading = false;
    }
    //向后加载cnt条新闻
    private NewsItem getOneNews(int i) {





        //用随机数字串作为新闻内容
        Random random = new Random();
        String title = "第" + i + "条新闻";
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String content = "";
        int contentLength = random.nextInt(300);
        for (int j = 0; j < contentLength; j++) {
            content = content + random.nextInt(10);
        }
        return new NewsItem(title, content, time);
    }
    //设置返回顶部的按钮
    private void initBackToTopButtom() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.main_floating);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.scrollToPosition(0);
            }
        });

    }
}

