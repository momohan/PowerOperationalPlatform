package com.handu.poweroperational.main.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseActivity;
import com.handu.poweroperational.ui.RecyclerView.adapter.CommonRecyclerViewAdapter;
import com.handu.poweroperational.ui.RecyclerView.click.ItemClickListener;
import com.handu.poweroperational.ui.RecyclerView.holder.BaseRecyclerViewHolder;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ezy.ui.layout.LoadingLayout;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class PushMessageActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.refreshLayout)
    PtrClassicFrameLayout refreshLayout;
    @Bind(R.id.loading_view)
    LoadingLayout loadingView;
    private CommonRecyclerViewAdapter<String> mAdapter;
    private int size = 20;
    private int page = 1;
    private MsgHandler msgHandler = new MsgHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_message);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        msgHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void initRecyclerView() {
        recyclerView.addOnItemTouchListener(new ItemClickListener(recyclerView,
                new ItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                    }
                }));
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(manager);
        mAdapter = new CommonRecyclerViewAdapter<String>(mContext, R.layout.item_message_data_list) {
            @Override
            public void convert(BaseRecyclerViewHolder helper, String item, int position) {
                TextView tv = helper.getView(R.id.tv_message_content);
                tv.setText(item);
                tv = helper.getView(R.id.tv_position);
                tv.setText(position + 1 + "");
            }
        };
        ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(mAdapter);
        alphaAdapter.setInterpolator(new OvershootInterpolator(0.5f));
        alphaAdapter.setDuration(1000);
        alphaAdapter.setFirstOnly(false);
        RecyclerAdapterWithHF adapter = new RecyclerAdapterWithHF(alphaAdapter);
        recyclerView.setAdapter(adapter);
    }

    private void initLoadingView() {
        loadingView.showContent();
        loadingView.setRetryListener(v -> refresh());
    }

    private void initRefreshLayout() {
        refreshLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                page = 1;
                msgHandler.postDelayed(() -> setData(0), 1000);
            }
        });
        refreshLayout.setOnLoadMoreListener(() -> {
            page++;
            msgHandler.postDelayed(() -> setData(1), 1000);
        });
    }

    private void refresh() {
        msgHandler.postDelayed(() -> {
            if (refreshLayout != null) {
                refreshLayout.autoRefresh(true);
            }
        }, 100);
    }

    @Override
    protected void initView() {
        initToolBar(toolbar, getString(R.string.title_message), true, v -> finish());
        initLoadingView();
        initRecyclerView();
        initRefreshLayout();
    }

    @Override
    protected void initData() {
        refresh();
    }

    private void setData(int type) {
        msgHandler.sendEmptyMessage(type);
    }

    static class MsgHandler extends Handler {

        private WeakReference<Activity> reference;

        public MsgHandler(Activity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            PushMessageActivity activity = (PushMessageActivity) reference.get();
            switch (msg.what) {
                case 0:
                    List<String> list = new ArrayList<>();
                    for (int i = 0; i < activity.size; i++) {
                        list.add("你有新的消息" + (i + 1));
                    }
                    activity.mAdapter.fillList(list);
                    if (activity.refreshLayout != null) {
                        if (activity.refreshLayout.isRefreshing())
                            activity.refreshLayout.refreshComplete();
                        activity.refreshLayout.setLoadMoreEnable(true);
                    }
                    break;
                case 1:
                    list = new ArrayList<>();
                    for (int i = (activity.page - 1) * activity.size; i < (activity.page * activity.size); i++) {
                        list.add("你有新的消息" + (i + 1));
                    }
                    activity.mAdapter.appendList(list);
                    if (activity.refreshLayout != null) {
                        if (activity.refreshLayout.isLoadingMore())
                            activity.refreshLayout.loadMoreComplete(true);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    }
}
