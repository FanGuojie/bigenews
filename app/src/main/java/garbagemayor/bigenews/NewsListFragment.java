package garbagemayor.bigenews;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static android.content.ContentValues.TAG;

public class NewsListFragment extends Fragment {

    private int category;
    private NewsListAdpter newsListAdpter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        category = getArguments().getInt("category");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_newslist, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        Log.d(TAG, "onViewCreated: item");
        newsListAdpter = new NewsListAdpter(getContext());
        recyclerView.setAdapter(newsListAdpter);
        Log.d(TAG, "onViewCreated: item");
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.main_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        newsListAdpter.addData(this.category);
    }

    private void refresh() {
        swipeRefreshLayout.setRefreshing(true);
        Log.d(TAG, "refresh: at refresh");
        newsListAdpter.clearData();
        newsListAdpter.addData(this.category);
        swipeRefreshLayout.setRefreshing(false);
    }

}
