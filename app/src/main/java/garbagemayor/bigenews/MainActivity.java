package garbagemayor.bigenews;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    public static PageProvider pageProvider;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private Toolbar toolbar;
    private List<NewsListFragment> l1;
    private List<String> l2;
    private Map<Integer, NewsListFragment> mFragmentList;
    private Map<Integer, String> mFragmentTitleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=59b15923");
        pageProvider = new PageProvider(this);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        mFragmentList = new HashMap<>();
        mFragmentTitleList = new HashMap<>();

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        // bundle
        NewsListFragment fragment = new NewsListFragment();
        Bundle category = new Bundle();
        category.putInt("category", NewsAPI.TECHNOLOGY);
        fragment.setArguments(category);
        adapter.addFrag(fragment, "TECHNOLOGY");

//        NewsListFragment fragment2 = new NewsListFragment();
//        Bundle category2 = new Bundle();
//        category2.putInt("category", NewsAPI.EDUCATION);
//        fragment2.setArguments(category2);
//        adapter.addFrag(fragment2, "EDUCATION");
        // on this list
//        mFragmentList.put(NewsAPI.EDUCATION, fragment);
//        mFragmentTitleList.put(NewsAPI.EDUCATION, "EDUCATION");

//        l1 = new ArrayList<>();
//        l1.add(new NewsListFragment());
//        l1.add(new NewsListFragment());
//        l1.add(new NewsListFragment());
//        l2 = new ArrayList<>();
//        l2.add("aaa");
//        l2.add("aaa");
//        l2.add("aaa");
//
//        adapter = new ViewPagerAdapter(getSupportFragmentManager(), l1, l2);
        viewPager.setAdapter(adapter);
//        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<NewsListFragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        public ViewPagerAdapter(FragmentManager manager, List<NewsListFragment> f, List<String> l) {
            super(manager);
            fragmentList.addAll(f);
            fragmentTitleList.addAll(l);
        }

        public void addFrag(NewsListFragment f, String s) {
            fragmentList.add(f);
            fragmentTitleList.add(s);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_clean_cache:
                clean_cache();
        }
        return true;
    }

    private void clean_cache() {
        String[] filelist = this.fileList();
        for (String file : filelist) {
            if (file.contains("json")) {
                this.deleteFile(file);
            }
        }
        Toast.makeText(this, "已清除", Toast.LENGTH_SHORT).show();
    }
}
