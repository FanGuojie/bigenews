package garbagemayor.bigenews;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import garbagemayor.bigenews.newssrc.PagePlus;
import garbagemayor.bigenews.newssrc.PageItem;

public class MainActivity extends AppCompatActivity {

    //调试Log的标记
    private static final String TAG = "MainActivityTag";
    //侧滑菜单布局
    private DrawerLayout mDrawerLayout;
    //顶部模块
    private Toolbar mToolbar;
    private TextView mToolBarText;
    //include的模式
    private View mIncludeNormal;
    private View mIncludeHistory;
    private View mIncludeFavorite;
    private View mIncludeSetting;
    //筛选器部分
    private int nowCategoryId = 0;
    private String nowSearchText = "";
    //类别筛选
    private Button mCategoryBtn;
    private GridView mCategoryGridView;
    private ArrayAdapter<String> mCategoryAdapter;
    private List<String> mCategoryList;
    private PopupWindow mCategoryPopupWindow;
    //搜索模块
    private SearchView mSearchView;
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

        //语音 初始化
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=59b15923");
        //用ToolBar代替ActionBar
        initToolBar();
        //侧滑菜单的属性设置
        initDrawerLayout();
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
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer_home);
        }
        mToolBarText = (TextView) findViewById(R.id.main_toolbar_text);
        mToolBarText.setText("：主页");
    }

    //点击左上角显示侧滑菜单
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        closeInputMethodAnyaway();
        switch (item.getItemId()) {
            //侧滑
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    //按退出键也要连按两次，但是如果打开了侧滑菜单就优先关闭侧滑菜单
    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            pressQuit();
        }
    }

    private void initDrawerLayout() {
        //自定义侧滑菜单各种事件的行为
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View content = mDrawerLayout.getChildAt(0);
                content.setTranslationX(drawerView.getWidth() * slideOffset * 0.5f);
                content.setScaleX((content.getWidth() - drawerView.getWidth() * slideOffset) / content.getWidth());
                drawerView.setTranslationX(drawerView.getWidth() * (1.0f - slideOffset) * 0.5f);
                drawerView.setScaleX(slideOffset);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                closeInputMethodAnyaway();
            }
            @Override
            public void onDrawerClosed(View drawerView) {
            }
            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    //侧滑菜单里的按钮的行为
    private void initNavigationView() {
        initIncludeMode();
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
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
                        mToolBarText.setText("：主页");
                        mIncludeNormal.setVisibility(View.VISIBLE);
                        mIncludeHistory.setVisibility(View.GONE);
                        mIncludeFavorite.setVisibility(View.GONE);
                        mIncludeSetting.setVisibility(View.GONE);
                        mDrawerLayout.closeDrawers();
                        break;
                    //查看历史
                    case R.id.nav_history:
                        mToolBarText.setText("：历史");
                        Toast.makeText(MainActivity.this, "这部分代码还没写", Toast.LENGTH_SHORT).show();
                        mIncludeNormal.setVisibility(View.GONE);
                        mIncludeHistory.setVisibility(View.VISIBLE);
                        mIncludeFavorite.setVisibility(View.GONE);
                        mIncludeSetting.setVisibility(View.GONE);
                        mDrawerLayout.closeDrawers();
                        break;
                    //进入收藏夹
                    case R.id.nav_favorite:
                        mToolBarText.setText("：收藏");
                        Toast.makeText(MainActivity.this, "这部分代码还没写", Toast.LENGTH_SHORT).show();
                        mIncludeNormal.setVisibility(View.GONE);
                        mIncludeHistory.setVisibility(View.GONE);
                        mIncludeFavorite.setVisibility(View.VISIBLE);
                        mIncludeSetting.setVisibility(View.GONE);
                        mDrawerLayout.closeDrawers();
                        break;
                    //进入设置界面
                    case R.id.nav_setting:
                        mToolBarText.setText("：设置");
                        Toast.makeText(MainActivity.this, "这部分代码还没写", Toast.LENGTH_SHORT).show();
                        mIncludeNormal.setVisibility(View.GONE);
                        mIncludeHistory.setVisibility(View.GONE);
                        mIncludeFavorite.setVisibility(View.GONE);
                        mIncludeSetting.setVisibility(View.VISIBLE);
                        mDrawerLayout.closeDrawers();
                        break;
                    //连按两次退出程序
                    case R.id.nav_quit:
                        pressQuit();
                        break;
                    default:
                }
                return true;
            }
        });
    }

    //正常模式、查看历史模式、设置菜单模式的选择
    private void initIncludeMode() {
        mIncludeNormal = findViewById(R.id.main_include_normal);
        mIncludeHistory = findViewById(R.id.main_include_history);
        mIncludeFavorite = findViewById(R.id.main_include_favorite);
        mIncludeSetting = findViewById(R.id.main_include_setting);
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
                if (nowCategoryId != i) {
                    closeInputMethodAnyaway();
                    nowCategoryId = i;
                    mCategoryBtn.setText("分类：" + mCategoryList.get(i));
                    refreshNews();
                }
                mCategoryPopupWindow.dismiss();
            }
        });
        //设置“分类”按钮行为
        mCategoryBtn = (Button) findViewById(R.id.main_filter_category);
        mCategoryBtn.setText("分类：" + "最新");
        mCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeInputMethodAnyaway();
                mCategoryPopupWindow.showAsDropDown(mCategoryBtn);
            }
        });
        //设置搜索模块行为
        mSearchView = (SearchView) findViewById(R.id.main_filter_search);
        mSearchView.setFocusable(true);
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setSubmitButtonEnabled(true);
        //mSearchView.onActionViewExpanded();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                //Toast.makeText(MainActivity.this, "搜索：" + queryText, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onQueryTextSubmit");
                nowSearchText = queryText;
                refreshNews();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                Log.d(TAG, "onQueryTextChange");
                nowSearchText = text;
                return true;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d(TAG, "onClose");
                closeInputMethodAnyaway();
                nowSearchText = "";
                refreshNews();
                return false;
            }
        });
    }

    //尝试关闭搜索模块默认开启的输入法
    public void closeInputMethodAnyaway() {
        Log.d(TAG, "closeInputMethodAnyaway");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            //imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
            imm.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
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
                closeInputMethodAnyaway();
                PageItem pageItem = mNewsList.get(postion);
                if (pageItem != null) {
                    //Toast.makeText(MainActivity.this, "点击：" + pageItem.getTitle(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                    intent.putExtra("id", pageItem.getId());
                    intent.putExtra("pictures", pageItem.getImageUrlList().toArray());
                    startActivity(intent);
                }
            }
        });
        mNewsAdapter.setOnItemLongClickListener(new NewsAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int postion) {
                //长按第postion条新闻
                closeInputMethodAnyaway();
                PageItem pageItem = mNewsList.get(postion);
                if (pageItem != null) {
                    Toast.makeText(MainActivity.this, "长按：" + pageItem.getTitle(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        mPageItemRecView.setAdapter(mNewsAdapter);

        //靠近底部自动加载功能
        mPageItemRecView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //这是滑动事件，需要自己判断是不是滑到接近底部
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolledn dx = " + dx + " dy = " + dy);
                //滑动的时候，如果输入法还开着，就把它关了。
                if(dx != 0 || dy != 0) {
                    closeInputMethodAnyaway();
                }
                if (isLoading)
                    return;
                int itemCount = mNewsLayoutManager.getItemCount();
                int[] lastPosList = mNewsLayoutManager.findLastVisibleItemPositions(null);
                int lastPos = Integer.MIN_VALUE;
                for (int i = 0; i < lastPosList.length; i++) {
                    if (lastPos < lastPosList[i]) {
                        lastPos = lastPosList[i];
                    }
                }
                if (lastPos >= (itemCount - visibleThreshold)) {
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
                closeInputMethodAnyaway();
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
                Log.d(TAG, "loadAPageOfNewNews.run()  nowCategoryId = " + nowCategoryId + " nowSearchText = " + nowSearchText);
                if (nowCategoryId == 0) {
                    if (nowSearchText.equals("")) {
                        mPage = new PagePlus(++mPageId, mPageSize);
                    } else {
                        mPage = new PagePlus(nowSearchText, ++mPageId, mPageSize);
                    }
                } else {
                    if (nowSearchText.equals("")) {
                        mPage = new PagePlus(nowCategoryId, ++mPageId, mPageSize);
                    } else {
                        mPage = new PagePlus(nowSearchText, nowCategoryId, ++mPageId, mPageSize);
                    }
                }
            }
        });
        t.start();
        try {
            t.join(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mPage.cont != null && mPage.cont.length > 0) {
            for (PageItem pageItem : mPage.cont) {
                mNewsList.add(pageItem);
                mNewsAdapter.notifyItemInserted(mNewsList.size());
            }
        }
        else {
            Toast.makeText(MainActivity.this, "找不到新闻", Toast.LENGTH_SHORT).show();
        }
        isLoading = false;
    }

    //设置返回顶部的按钮
    private void initBackToTopButtom() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.main_floating);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeInputMethodAnyaway();
                mPageItemRecView.scrollToPosition(0);
            }
        });
    }
}

