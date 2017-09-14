package garbagemayor.bigenews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import garbagemayor.bigenews.newssrc.DatabaseLoader;
import garbagemayor.bigenews.newssrc.PagePlus;
import garbagemayor.bigenews.newssrc.PageItem;

public class MainActivity extends AppCompatActivity {

    /*
     *  各个模式公用
     */
    //调试Log的标记
    private static final String TAG = "MainActivityTag";
    //连按两次“退出”按钮才退出
    private Date lastPressQuit = null;
    //侧滑菜单布局
    private DrawerLayout mDrawerLayout;
    //顶部模块
    private Toolbar mToolbar;
    private TextView mToolBarText;

    /*
     *  主页模式
     */
    private View mHomepageInclude;
    //筛选条件
    private int nowCategoryId = -1;
    private String nowSearchText = "";
    //类别筛选
    private Button mCategoryBtn;
    private GridView mCategoryGridView;
    private ArrayAdapter<String> mCategoryAdapter;
    private List<String> mCategoryList = new ArrayList<>();
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
    private List<PageItem> pageItems;
    private int nowPageId;
    private static int sizeOfPage = 10;
    //悬浮按钮
    private FloatingActionButton mBackToTopFAB;

    /*
     *  历史模式
     */
    private View mHistoryInclude;
    private RecyclerView mHistoryRecView;
    private StaggeredGridLayoutManager mHistoryNewsLayoutManager;
    private NewsAdapter mHistoryNewsAdapter;


    /*
     *  收藏模式
     */
    private View mFavoriteInclude;
    private RecyclerView mFavoriteRecView;
    private StaggeredGridLayoutManager mFavoriteNewsLayoutManager;
    private NewsAdapter mFavoriteNewsAdapter;

    /*
     *  设置模式
     */
    private View mSettingInclude;
    //分类菜单设置
    private List<String> mAllCategoryList = Arrays.asList("最新", "科技", "教育", "军事", "国内", "社会", "文化", "汽车", "国际", "体育", "财经", "健康", "娱乐");
    private RelativeLayout mCategoryOptionEmerging;
    private ImageView mCategoryOptionSpread;
    private LinearLayout mCategoryOptionHidden;
    private RecyclerView mCategoryOptionRecView;
    private Button mCategoryOptionBtn;
    private StaggeredGridLayoutManager mCategoryOptionLayoutManager;
    private SettingCategoryAdapter mCategoryOptionAdapter;
    //字体大小设置
    private RelativeLayout mTextsizeEmerging;
    private ImageView mTextsizeSpread;
    private LinearLayout mTextsizeHidden;
    private Button mTextsizeBtn;
    //清除缓存
    private Button mClearHistoryBtn;
    //夜间模式
    private CheckBox mNightstyleCheckBox;





    public static DatabaseLoader db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        //语音 初始化
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=59b15923");
        db = new DatabaseLoader(this.getBaseContext());
        //用ToolBar代替ActionBar
        initToolBar();
        //侧滑菜单的属性设置
        initDrawerLayout();
        //主页模式里面的东西
        initHomepage();
        //历史模式需要的东西
        initHistory();
        //收藏夹需要的东西
        initFavorite();
        //设置菜单里面的东西
        initSetting();
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
        //侧滑菜单里的按钮的行为
        initNavigationView();
    }

    //侧滑菜单里的按钮的行为
    private void initNavigationView() {
        //夜间模式
        findViewById(R.id.nav_view)
                .setBackgroundColor(getResources().getColor(
                        getSharedPreferences("setting", Activity.MODE_PRIVATE)
                                .getBoolean("NightStyleOn", false)? R.color.night_background: R.color.daytime_background));
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
                        mHomepageInclude.setVisibility(View.VISIBLE);
                        mHistoryInclude.setVisibility(View.GONE);
                        mFavoriteInclude.setVisibility(View.GONE);
                        mSettingInclude.setVisibility(View.GONE);
                        mBackToTopFAB.setVisibility(View.VISIBLE);
                        mDrawerLayout.closeDrawers();
                        break;
                    //查看历史
                    case R.id.nav_history:
                        mToolBarText.setText("：历史");
                        mHomepageInclude.setVisibility(View.GONE);
                        mHistoryInclude.setVisibility(View.VISIBLE);
                        mFavoriteInclude.setVisibility(View.GONE);
                        mSettingInclude.setVisibility(View.GONE);
                        mBackToTopFAB.setVisibility(View.GONE);
                        loadHistory();
                        mDrawerLayout.closeDrawers();
                        break;
                    //进入收藏夹
                    case R.id.nav_favorite:
                        mToolBarText.setText("：收藏");
                        mHomepageInclude.setVisibility(View.GONE);
                        mHistoryInclude.setVisibility(View.GONE);
                        mFavoriteInclude.setVisibility(View.VISIBLE);
                        mSettingInclude.setVisibility(View.GONE);
                        mBackToTopFAB.setVisibility(View.GONE);
                        loadFavorite();
                        mDrawerLayout.closeDrawers();
                        break;
                    //进入设置界面
                    case R.id.nav_setting:
                        mToolBarText.setText("：设置");
                        mHomepageInclude.setVisibility(View.GONE);
                        mHistoryInclude.setVisibility(View.GONE);
                        mFavoriteInclude.setVisibility(View.GONE);
                        mSettingInclude.setVisibility(View.VISIBLE);
                        mBackToTopFAB.setVisibility(View.GONE);
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
        mHomepageInclude = findViewById(R.id.main_include_homepage);
        mHistoryInclude = findViewById(R.id.main_include_history);
        mFavoriteInclude = findViewById(R.id.main_include_favorite);
        mSettingInclude = findViewById(R.id.main_include_setting);
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

    private void initHomepage() {
        //筛选器里按钮的行为
        initNewsFilter();
        //新闻模块的显示，下拉刷新，自动加载的功能
        initShowNews();
        //第一页新闻
        refreshNews();
        //悬浮按钮
        initBackToTopButtom();
    }

    //筛选器里面的按钮的行为
    private void initNewsFilter() {
        //设置分类模块
        initFilterCategory();
        //设置搜索模块
        initFilterSearch();
    }

    //筛选器里的分类模块
    private void initFilterCategory() {
        //“分类”按钮的内容
        SharedPreferences sharedPreferences = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        mCategoryList.clear();
        for(String option: mAllCategoryList) {
            Log.d(TAG, option + " " + sharedPreferences.getBoolean(option, true));
            if(sharedPreferences.getBoolean(option, true)) {
                mCategoryList.add(option);
            }
        }
        String firstOptionName;
        if(mCategoryList.size() > 0) {
            firstOptionName = mCategoryList.get(0);
        } else {
            firstOptionName = "最新";
        }
        int firstOptionId = mAllCategoryList.indexOf(firstOptionName);
        if(nowCategoryId == -1) {
            nowCategoryId = firstOptionId;
        } else if(nowCategoryId != firstOptionId) {
            nowCategoryId = firstOptionId;
            refreshNews();
        }
        Log.d(TAG, "firstOption = " + firstOptionName + " " + firstOptionId);
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
                String chooseName = (String) ((TextView) view).getText();
                int chooseId = mAllCategoryList.indexOf(chooseName);
                if (nowCategoryId != chooseId) {
                    closeInputMethodAnyaway();
                    nowCategoryId = chooseId;
                    mCategoryBtn.setText("分类：" + chooseName);
                    refreshNews();
                }
                mCategoryPopupWindow.dismiss();
            }
        });
        //设置“分类”按钮行为
        mCategoryBtn = (Button) findViewById(R.id.main_filter_category);
        mCategoryBtn.setText("分类：" + firstOptionName);
        mCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeInputMethodAnyaway();
                mCategoryPopupWindow.showAsDropDown(mCategoryBtn);
            }
        });
    }

    //筛选器里面的搜索模块
    private void initFilterSearch() {
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
        //夜间模式
        findViewById(R.id.main_news_list)
                .setBackgroundColor(getResources().getColor(
                        getSharedPreferences("setting", Activity.MODE_PRIVATE)
                                .getBoolean("NightStyleOn", false)? R.color.night_background: R.color.daytime_background));
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
//                    db.addHistory(pageItem);
                    SharedPreferences sharedPreferences = getSharedPreferences("visited", Activity.MODE_PRIVATE);
                    sharedPreferences.edit().putBoolean(pageItem.getTitle(), true).apply();
                    ((TextView) view.findViewById(R.id.news_title)).setTextColor(getResources().getColor(R.color.daytime_title_visited));
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
                //旋转悬浮按钮
                int firstPosList[] = mNewsLayoutManager.findFirstVisibleItemPositions(null);
                int firstPos = Integer.MAX_VALUE;
                for (int i = 0; i < firstPosList.length; i++) {
                    if(firstPos > firstPosList[i]) {
                        firstPos = firstPosList[i];
                    }
                }
                View firstChildView = mNewsLayoutManager.findViewByPosition(firstPos);
                int itemHeight = firstChildView.getHeight() * firstPos - firstChildView.getTop();
                float rotateAngle = 90.0f * itemHeight / mPageItemRecView.getHeight();
                mBackToTopFAB.setRotation(rotateAngle);
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
        nowPageId = 0;
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
                        pageItems = db.queryPage(++nowPageId, sizeOfPage);
                    } else {
                        pageItems = db.queryPage(nowSearchText, ++nowPageId, sizeOfPage);
                    }
                } else {
                    if (nowSearchText.equals("")) {
                        pageItems = db.queryPage(nowCategoryId, ++nowPageId, sizeOfPage);
                    } else {
                        pageItems = db.queryPage(nowSearchText, nowCategoryId, ++nowPageId, sizeOfPage);
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
        if (pageItems != null && pageItems.size() > 0) {
            for (PageItem pageItem : pageItems) {
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
        mBackToTopFAB = (FloatingActionButton) findViewById(R.id.main_homepage_floating);
        mBackToTopFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBackToTopFAB.setRotation(0);
                closeInputMethodAnyaway();
                mPageItemRecView.scrollToPosition(0);
            }
        });
    }

    //历史模式需要的东西
    private void initHistory() {
        //夜间模式
        findViewById(R.id.main_history_list)
                .setBackgroundColor(getResources().getColor(
                        getSharedPreferences("setting", Activity.MODE_PRIVATE)
                                .getBoolean("NightStyleOn", false)? R.color.night_background: R.color.daytime_background));
        //数据库
        db = new DatabaseLoader(this.getBaseContext());
        mHistoryRecView = (RecyclerView) findViewById(R.id.main_history_list);
        mHistoryNewsLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mHistoryRecView.setLayoutManager(mHistoryNewsLayoutManager);
    }

    //每次打开历史模式的时候要重新加载历史内容
    private void loadHistory() {
        db.updateHistory();
        if(mHistoryNewsAdapter == null) {
            mHistoryNewsAdapter = new NewsAdapter(db.history);
        }
        mHistoryNewsAdapter.notifyDataSetChanged();
        mHistoryNewsAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                //点击第postion条新闻
                closeInputMethodAnyaway();
                final String title = db.history.get(postion).getTitle();
                final PageItem[] pageItem = new PageItem[1];
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            PagePlus p = new PagePlus(title, 1, sizeOfPage);
                            pageItem[0] = p.cont[0];
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();
                try {
                    t.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (pageItem[0] != null) {
                    Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                    intent.putExtra("id", pageItem[0].getId());
                    intent.putExtra("pictures", pageItem[0].getImageUrlList().toArray());
                    startActivity(intent);

                }
            }
        });
        mHistoryNewsAdapter.setOnItemLongClickListener(new NewsAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int postion) {
                //长按第postion条新闻
                closeInputMethodAnyaway();
                PageItem pageItem = db.history.get(postion);
                if (pageItem != null) {
                    Toast.makeText(MainActivity.this, "长按：" + pageItem.getTitle(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(mHistoryRecView.getAdapter() == null) {
            mHistoryRecView.setAdapter(mHistoryNewsAdapter);
        }
    }

    //收藏夹的东西
    private void initFavorite() {
        //夜间模式
        findViewById(R.id.main_favorite_list)
                .setBackgroundColor(getResources().getColor(
                        getSharedPreferences("setting", Activity.MODE_PRIVATE)
                                .getBoolean("NightStyleOn", false)? R.color.night_background: R.color.daytime_background));
        //数据库
        db = new DatabaseLoader(this.getBaseContext());
        mFavoriteRecView = (RecyclerView) findViewById(R.id.main_favorite_list);
        mFavoriteNewsLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mFavoriteRecView.setLayoutManager(mFavoriteNewsLayoutManager);
    }

    //每次打开收藏模式的时候要重新加载历史内容
    private void loadFavorite() {
        db.updateFavorite();
        Log.d(TAG, "size = " + db.favorite.size());
        if(mFavoriteNewsAdapter == null) {
            mFavoriteNewsAdapter = new NewsAdapter(db.favorite);
        }
        mFavoriteNewsAdapter.notifyDataSetChanged();
        mFavoriteNewsAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                //点击第postion条新闻
                closeInputMethodAnyaway();
                final String title = db.favorite.get(postion).getTitle();
                final PageItem[] pageItem = new PageItem[1];
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            PagePlus p = new PagePlus(title, 1, sizeOfPage);
                            pageItem[0] = p.cont[0];
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();
                try {
                    t.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (pageItem[0] != null) {
                    Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                    intent.putExtra("id", pageItem[0].getId());
                    intent.putExtra("pictures", pageItem[0].getImageUrlList().toArray());
                    startActivity(intent);

                }
            }
        });
        mFavoriteNewsAdapter.setOnItemLongClickListener(new NewsAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int postion) {
                //长按第postion条新闻
                closeInputMethodAnyaway();
                PageItem pageItem = db.favorite.get(postion);
                if (pageItem != null) {
                    Toast.makeText(MainActivity.this, "长按：" + pageItem.getTitle(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(mFavoriteRecView.getAdapter() == null) {
            mFavoriteRecView.setAdapter(mFavoriteNewsAdapter);
        }
    }

    //设置菜单里面的东西
    private void initSetting() {
        //夜间模式
        findViewById(R.id.main_setting_list)
                .setBackgroundColor(getResources().getColor(
                        getSharedPreferences("setting", Activity.MODE_PRIVATE)
                                .getBoolean("NightStyleOn", false)? R.color.night_background: R.color.daytime_background));
        //自定义分类列表
        initSettingCategory();
        //字体大小设置
        initSettingTextsize();
        //清除缓存按钮
        initSettingClear();
        //夜间模式切换
        initSettingNightstyle();
    }

    //自定义分类菜单
    private void initSettingCategory() {
        //获取当前的分类菜单状态
        mCategoryOptionEmerging = (RelativeLayout) findViewById(R.id.main_setting_category_emerging);
        mCategoryOptionSpread = (ImageView) findViewById(R.id.main_setting_category_spread);
        mCategoryOptionHidden = (LinearLayout) findViewById(R.id.main_setting_category_hidden);
        mCategoryOptionRecView = (RecyclerView) findViewById(R.id.main_setting_category_recview);
        mCategoryOptionBtn = (Button) findViewById(R.id.main_setting_category_gogogo);
        //显示隐藏部分的点击事件
        mCategoryOptionEmerging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spreadSettingCategory();
            }
        });
        //隐藏部分收回去的点击事件
        mCategoryOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "设置已生效", Toast.LENGTH_SHORT).show();
                initFilterCategory();
                hiddenSettingCategory();
            }
        });
        //按钮控制显示或收回事件
        mCategoryOptionSpread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCategoryOptionHidden.getVisibility() == View.GONE) {
                    spreadSettingCategory();
                } else {
                    Toast.makeText(MainActivity.this, "设置尚未生效", Toast.LENGTH_SHORT).show();
                    hiddenSettingCategory();
                }
            }
        });
        mCategoryOptionLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        mCategoryOptionRecView.setLayoutManager(mCategoryOptionLayoutManager);
        mCategoryOptionAdapter = new SettingCategoryAdapter(mAllCategoryList);
        mCategoryOptionRecView.setAdapter(mCategoryOptionAdapter);
    }
    private void spreadSettingCategory() {
        mCategoryOptionHidden.setVisibility(View.VISIBLE);
        mCategoryOptionSpread.setRotation(180.0f);
    }
    private void hiddenSettingCategory() {
        mCategoryOptionHidden.setVisibility(View.GONE);
        mCategoryOptionSpread.setRotation(0.0f);
    }


    //字体大小设置
    private void initSettingTextsize() {
        mTextsizeEmerging = (RelativeLayout) findViewById(R.id.main_setting_textsize_emerging);
        mTextsizeSpread = (ImageView) findViewById(R.id.main_setting_textsize_spread);
        mTextsizeHidden = (LinearLayout) findViewById(R.id.main_setting_textsize_hidden);
        mTextsizeBtn = (Button) findViewById(R.id.main_setting_textsize_gogogo);
        //显示隐藏部分的点击事件
        mTextsizeEmerging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spreadSettingTextsize();
            }
        });
        //按钮控制显示或收回事件
        mTextsizeSpread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mTextsizeHidden.getVisibility() == View.GONE) {
                    spreadSettingTextsize();
                } else {
                    Toast.makeText(MainActivity.this, "设置尚未生效", Toast.LENGTH_SHORT).show();
                    hiddenSettingTextsize();
                }
            }
        });
        //立即生效按钮
        mTextsizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "设置已生效", Toast.LENGTH_SHORT).show();
                hiddenSettingTextsize();
            }
        });
    }
    private void spreadSettingTextsize() {
        mTextsizeHidden.setVisibility(View.VISIBLE);
        mTextsizeSpread.setRotation(180.0f);
    }
    private void hiddenSettingTextsize() {
        mTextsizeHidden.setVisibility(View.GONE);
        mTextsizeSpread.setRotation(0.0f);
    }

    //清除缓存
    private void initSettingClear() {
        mClearHistoryBtn = (Button) findViewById(R.id.main_setting_clear_gogogo);
        mClearHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("visited", Activity.MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();
                String[] filelist = fileList();
                for (String file : filelist) {
                    if (file.contains("history")) {
                        Log.d(TAG, "onClick: " + file);
                        deleteFile(file);
                    }
                }
                refreshNews();
                Toast.makeText(MainActivity.this, "已清除", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initSettingNightstyle() {
        SharedPreferences sharedPreferences = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        mNightstyleCheckBox = (CheckBox) findViewById(R.id.main_setting_nightstyle_checkbox);
        mNightstyleCheckBox.setChecked(sharedPreferences.getBoolean("NightStyleOn", false));
        mNightstyleCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isOn) {
                SharedPreferences sharedPreferences = getSharedPreferences("setting", Activity.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean("NightStyleOn", isOn).apply();
                nightstyleHasBeenChanged();
            }
        });
    }

    private void nightstyleHasBeenChanged() {
        /*
        int backgound;
        int textTitle;
        int textTitleV;
        int textContent;
        if(getSharedPreferences("setting", Activity.MODE_PRIVATE).getBoolean("NightStyleOn", false)) {
            backgound = getResources().getColor(R.color.daytime_background);
            textTitle = getResources().getColor(R.color.daytime_title);
            textTitleV = getResources().getColor(R.color.daytime_title_visited);
            textContent = getResources().getColor(R.color.daytime_content);
        } else {
            backgound = getResources().getColor(R.color.night_background);
            textTitle = getResources().getColor(R.color.night_title);
            textTitleV = getResources().getColor(R.color.night_title_visited);
            textContent = getResources().getColor(R.color.night_content);
        }
        */
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

