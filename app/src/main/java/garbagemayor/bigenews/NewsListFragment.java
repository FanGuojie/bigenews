package garbagemayor.bigenews;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

public class NewsListFragment extends Fragment implements PullLoadMoreRecyclerView.PullLoadMoreListener {

    protected View rootView;
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private int category = 1;
    private int mCount = 4;
    private NewsListViewAdapter mNewsListViewAdapter;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        category = getArguments().getInt("category");
        return inflater.inflate(R.layout.fragment_newslist, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPullLoadMoreRecyclerView = view.findViewById(R.id.pullLoadMoreRecyclerView);
        //获取mRecyclerView对象
        mRecyclerView = mPullLoadMoreRecyclerView.getRecyclerView();
        //代码设置scrollbar无效？未解决！
        mRecyclerView.setVerticalScrollBarEnabled(true);
        //设置下拉刷新是否可见
        //mPullLoadMoreRecyclerView.setRefreshing(true);
        //设置是否可以下拉刷新
        mPullLoadMoreRecyclerView.setPullRefreshEnable(true);
        //设置是否可以上拉刷新
        //mPullLoadMoreRecyclerView.setPushRefreshEnable(false);
        //显示下拉刷新
        mPullLoadMoreRecyclerView.setRefreshing(true);
        //设置上拉刷新文字
        mPullLoadMoreRecyclerView.setFooterViewText("loading");
        //设置上拉刷新文字颜色
        //mPullLoadMoreRecyclerView.setFooterViewTextColor(R.color.white);
        //设置加载更多背景色
        //mPullLoadMoreRecyclerView.setFooterViewBackgroundColor(R.color.colorBackground);
        mPullLoadMoreRecyclerView.setLinearLayout();

        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(this);
        mNewsListViewAdapter = new NewsListViewAdapter(getActivity());
        mPullLoadMoreRecyclerView.setAdapter(mNewsListViewAdapter);
        mNewsListViewAdapter.addData(this.category);
    }

    @Override
    public void onRefresh() {
        Log.e("rv", "onRefresh");
        mCount = 4;
        mNewsListViewAdapter.clearData();
        mNewsListViewAdapter.addData(this.category);
    }

    @Override
    public void onLoadMore() {
        Log.e("rv", "onLoadMore");
        mNewsListViewAdapter.addData(this.category);
    }

}
