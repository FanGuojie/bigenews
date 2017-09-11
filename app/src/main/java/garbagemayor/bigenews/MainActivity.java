package garbagemayor.bigenews;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import garbagemayor.bigenews.newssrc.PagePlus;
import garbagemayor.bigenews.newssrc.PageItem;

public class MainActivity extends AppCompatActivity{

    //调试Log的标记
    private static final String TAG = "MainActivityTag";
    //侧滑菜单布局
    private DrawerLayout mDrawerLayout;
    //筛选器部分
    private int nowCategoryId = 0;
    //类别筛选
    private Button mCategoryBtn;
    private GridView mCategoryGridView;
    private StaggeredGridLayoutManager mCategoryLayoutManager;
    //private CategoryAdapter mCategoryAdapter;
    private ArrayAdapter<String> mCategoryAdapter;
    private List<String> mCategoryList;
    private PopupWindow mCategoryPopupWindow;
    //新闻浏览部分
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mPageItemRecView;
    private StaggeredGridLayoutManager mNewsLayoutManager;
    private NewsAdapter mNewsAdapter;
    private static int visibleThreshold = 5;
    private List<PageItem> mNewsList = new ArrayList<>();
    //新闻加载器部分
    private boolean isLoading = false;
    private PagePlus mPage;
    private int mPageId;
    private static int mPageSize = 10;
    //连按两次“退出”按钮才退出
    private Date lastPressQuit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        //用ToolBar代替ActionBar
        initToolBar();
        //侧滑菜单里的按钮的行为
        initNavigationView();
        //筛选器里按钮的行为
        initNewsFilter();
        //新闻模块的显示，下拉刷新，自动加载的功能
        initShowNews();
        //第一页新闻
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

    //筛选器里面的按钮的行为
    private void initNewsFilter() {
        //“类别”按钮的内容
        mCategoryList = new ArrayList<>();
        mCategoryList.add("最新");
        mCategoryList.add("科技");
        mCategoryList.add("教育");
        mCategoryList.add("军事");
        mCategoryList.add("国内");
        mCategoryList.add("社会");
        mCategoryList.add("文化");
        mCategoryList.add("汽车");
        mCategoryList.add("国际");
        mCategoryList.add("体育");
        mCategoryList.add("财经");
        mCategoryList.add("健康");
        mCategoryList.add("娱乐");
        //设置弹出窗口的属性
        View contentView = MainActivity.this.getLayoutInflater().inflate(R.layout.filter_category_layout, null);
        mCategoryPopupWindow = new PopupWindow(contentView, GridView.LayoutParams.WRAP_CONTENT, GridView.LayoutParams.WRAP_CONTENT, true);
        mCategoryPopupWindow.setContentView(contentView);
        mCategoryPopupWindow.setAnimationStyle(R.style.FilterTranslateAnimation);
        //设置内容适配器
        mCategoryGridView = (GridView) contentView.findViewById(R.id.category_list);
        mCategoryAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.category_item_layout, mCategoryList);
        mCategoryGridView.setAdapter(mCategoryAdapter);
        mCategoryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(nowCategoryId != i) {
                    nowCategoryId = i;
                    mCategoryBtn.setText("分类：" + mCategoryList.get(i));
                    refreshNews();
                }
                mCategoryPopupWindow.dismiss();
            }
        });
        //设置“类别”按钮行为
        mCategoryBtn = (Button) findViewById(R.id.main_filter_category);
        mCategoryBtn.setText("分类：" + "最新");
        mCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategoryPopupWindow.showAsDropDown(mCategoryBtn);
            }
        });
    }

    //新闻显示模块
    private void initShowNews() {
        //RecyclerView里面初始化
        mPageItemRecView = (RecyclerView) findViewById(R.id.main_news_list);
        mNewsLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mPageItemRecView.setLayoutManager(mNewsLayoutManager);
        mNewsAdapter = new NewsAdapter(mNewsList);
        mNewsAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                //点击第postion条新闻
                PageItem pageItem = mNewsList.get(postion);
                if(pageItem != null){
                    Toast.makeText(MainActivity.this, "点击：" + pageItem.getTitle(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        mNewsAdapter.setOnItemLongClickListener(new NewsAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int postion) {
                //长按第postion条新闻
                PageItem pageItem = mNewsList.get(postion);
                if(pageItem != null){
                    Toast.makeText(MainActivity.this, "长按：" + pageItem.getTitle(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        mPageItemRecView.setAdapter(mNewsAdapter);


        //靠近底部自动加载功能
        mPageItemRecView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(isLoading)
                    return;
                int itemCount = mNewsLayoutManager.getItemCount();
                int[] lastPosList = mNewsLayoutManager.findLastVisibleItemPositions(null);
                int lastPos = Integer.MIN_VALUE;
                for(int i = 0; i < lastPosList.length; i ++) {
                    if(lastPos < lastPosList[i]) {
                        lastPos = lastPosList[i];
                    }
                }
                if(lastPos >= (itemCount - visibleThreshold)) {
                    loadAPageOfNewNews();
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
    //刷新，得到第一页新闻
    private void refreshNews() {
        mSwipeRefreshLayout.setRefreshing(true);
        mNewsList.clear();
        mNewsAdapter.notifyDataSetChanged();
        mPageId = 0;
        loadAPageOfNewNews();
        mSwipeRefreshLayout.setRefreshing(false);
    }
    //向RecyclerView里加入新的新闻
    private void loadAPageOfNewNews() {
        isLoading = true;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if(nowCategoryId == 0) {
                    mPage = new PagePlus(++mPageId, mPageSize);
                }
                else {
                    mPage = new PagePlus(nowCategoryId, ++mPageId, mPageSize);
                }
            }
        });
        t.start();
        try {
            t.join(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(mPage.cont != null) {
            for (PageItem pageItem : mPage.cont) {
                mNewsList.add(pageItem);
                mNewsAdapter.notifyItemInserted(mNewsList.size());
            }
        }
        isLoading = false;
    }
    //设置返回顶部的按钮
    private void initBackToTopButtom() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.main_floating);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPageItemRecView.scrollToPosition(0);
            }
        });
    }
}

