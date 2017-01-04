package com.handu.poweroperational.main.activity;import android.os.Bundle;import android.os.Handler;import android.support.design.widget.AppBarLayout;import android.support.v4.content.ContextCompat;import android.support.v7.widget.RecyclerView;import android.support.v7.widget.StaggeredGridLayoutManager;import android.support.v7.widget.Toolbar;import android.text.TextUtils;import android.view.View;import android.view.ViewGroup;import android.view.animation.OvershootInterpolator;import android.widget.SearchView;import android.widget.TextView;import com.chanven.lib.cptr.PtrClassicFrameLayout;import com.chanven.lib.cptr.PtrDefaultHandler;import com.chanven.lib.cptr.PtrFrameLayout;import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;import com.handu.poweroperational.R;import com.handu.poweroperational.base.BaseActivity;import com.handu.poweroperational.main.bean.results.DeviceResult;import com.handu.poweroperational.ui.RecyclerView.ItemClickListener;import com.handu.poweroperational.ui.RecyclerView.adapter.BaseRecyclerViewHolder;import com.handu.poweroperational.ui.RecyclerView.adapter.CommonRecyclerViewAdapter;import com.handu.poweroperational.ui.RecyclerView.utils.SwipyAppBarScrollListener;import com.handu.poweroperational.utils.Tools;import java.util.ArrayList;import java.util.List;import butterknife.Bind;import butterknife.ButterKnife;import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;public class SearchActivity extends BaseActivity implements SearchView.OnQueryTextListener {    public static final int REQUEST_CODE = 100;    @Bind(R.id.toolbar)    Toolbar toolbar;    @Bind(R.id.recyclerView)    RecyclerView recyclerView;    @Bind(R.id.refreshLayout)    PtrClassicFrameLayout refreshLayout;    @Bind(R.id.searchView)    SearchView searchView;    @Bind(R.id.appbar)    AppBarLayout appbar;    private ArrayList<DeviceResult> items;    private ArrayList<DeviceResult> newItems;    private CommonRecyclerViewAdapter<DeviceResult> mAdapter;    private int size = 80;    private int page = 1;    private Handler handler = new Handler();    private String searchText = "";    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_search);        ButterKnife.bind(this);        initView();        initData();    }    @Override    protected void onDestroy() {        handler.removeCallbacksAndMessages(null);        super.onDestroy();    }    @Override    protected void initView() {        initToolBar(toolbar, getString(R.string.device_search), true, new View.OnClickListener() {            @Override            public void onClick(View v) {                finish();            }        });        searchView.setOnQueryTextListener(this);        searchView.setSubmitButtonEnabled(true);        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);        TextView tv = (TextView) searchView.findViewById(id);        tv.setHintTextColor(ContextCompat.getColor(mContext, R.color.gray_light));        tv.setTextSize(14);        recyclerView.addOnItemTouchListener(new ItemClickListener(recyclerView,                new ItemClickListener.OnItemClickListener() {                    @Override                    public void onItemClick(View view, int position) {                        gotoActivity(OperationActivity.class, true);                    }                    @Override                    public void onItemLongClick(View view, int position) {                    }                }));        recyclerView.addOnScrollListener(new SwipyAppBarScrollListener(appbar, refreshLayout, recyclerView));        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));        mAdapter = new CommonRecyclerViewAdapter<DeviceResult>(mContext, R.layout.item_device_search_list) {            @Override            public void convert(BaseRecyclerViewHolder helper, DeviceResult item, int position) {                TextView tv = helper.getView(R.id.tv_device_name);                tv.setText(item.getDeviceName());                // 随机高度, 模拟瀑布效果.                ViewGroup.LayoutParams lp = tv.getLayoutParams();                lp.height = Tools.dp2px((int) (100 + Math.random() * 50));                tv.setLayoutParams(lp);            }        };        ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(mAdapter);        alphaAdapter.setInterpolator(new OvershootInterpolator(0.5f));        alphaAdapter.setDuration(1000);        alphaAdapter.setFirstOnly(false);        RecyclerAdapterWithHF adapter = new RecyclerAdapterWithHF(alphaAdapter);        recyclerView.setAdapter(adapter);    }    private void initRefreshLayout() {        refreshLayout.setLoadMoreEnable(false);        refreshLayout.setPtrHandler(new PtrDefaultHandler() {            @Override            public void onRefreshBegin(PtrFrameLayout frame) {                page = 1;                handler.postDelayed(new Runnable() {                    @Override                    public void run() {                        setData(0);                    }                }, 1000);            }        });        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {            @Override            public void loadMore() {                page++;                handler.postDelayed(new Runnable() {                    @Override                    public void run() {                        setData(1);                    }                }, 1000);            }        });    }    @Override    protected void initData() {        items = new ArrayList<>();        newItems = new ArrayList<>();        searchView.setQuery("奥克斯广场总漏保", false);        initRefreshLayout();    }    private void refresh() {        handler.postDelayed(new Runnable() {            @Override            public void run() {                if (refreshLayout != null)                    refreshLayout.autoRefresh(true);            }        }, 100);    }    private void setData(int type) {        if (TextUtils.isEmpty(searchText)) {            refreshLayout.refreshComplete();            Tools.showToast(getString(R.string.search_content_is_null));            return;        }        switch (type) {            case 0:                items.clear();                for (int i = 0; i < size; i++) {                    items.add(new DeviceResult(searchText + (i + 1)));                }                mAdapter.fillList(items);                if (refreshLayout != null) {                    refreshLayout.refreshComplete();                    refreshLayout.setLoadMoreEnable(true);                }                break;            case 1:                List<DeviceResult> list = new ArrayList<>();                for (int i = (page - 1) * size; i < page * size; i++) {                    list.add(new DeviceResult("设备" + (i + 1)));                }                items.addAll(list);                mAdapter.fillList(items);                if (refreshLayout != null) {                    refreshLayout.loadMoreComplete(true);                }                break;        }    }    @Override    public boolean onQueryTextSubmit(String query) {        if (!TextUtils.isEmpty(query)) {            searchText = query;            refresh();        }        return false;    }    @Override    public boolean onQueryTextChange(String newText) {        searchText = newText;        if (refreshLayout != null) refreshLayout.setLoadMoreEnable(false);        if (!TextUtils.isEmpty(newText)) {            mAdapter.fillList(getFilterData(newText));        } else {            mAdapter.fillList(items);        }        return false;    }    private List<DeviceResult> getFilterData(String info) {        newItems.clear();        if (items.size() > 0) {            //遍历list            for (int i = 0; i < items.size(); i++) {                DeviceResult domain = items.get(i);                //如果遍历到的名字包含所输入字符串                if (domain.getDeviceName().contains(info)) {                    //将遍历到的元素重新组成一个list                    newItems.add(domain);                }            }        }        return newItems;    }}