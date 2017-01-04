package com.handu.poweroperational.main.fragment.operaton;


import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.TextView;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseEvent;
import com.handu.poweroperational.base.BaseFragment;
import com.handu.poweroperational.main.bean.constants.EventType;
import com.handu.poweroperational.ui.RecyclerView.adapter.BaseRecyclerViewHolder;
import com.handu.poweroperational.ui.RecyclerView.adapter.CommonRecyclerViewAdapter;
import com.handu.poweroperational.ui.RecyclerView.utils.SwipyAppBarScrollListener;
import com.handu.poweroperational.utils.Tools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ezy.ui.layout.LoadingLayout;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CurrentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrentFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.loading_view)
    LoadingLayout loadingView;
    @Bind(R.id.refreshLayout)
    PtrClassicFrameLayout refreshLayout;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.appbar)
    AppBarLayout appbar;
    @Bind(R.id.tv_device_name)
    TextView tvDeviceName;
    private CommonRecyclerViewAdapter<String[]> mAdapter;
    private StaggeredGridLayoutManager manager;
    private Handler mHandler = new Handler();
    private String mParam1;
    private String mParam2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        registerEvent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }

    public CurrentFragment() {
    }

    public static CurrentFragment newInstance(String param1, String param2) {
        CurrentFragment fragment = new CurrentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int findView() {
        return R.layout.fragment_current;
    }

    @Override
    protected void initView() {
        initLoadingView();
        initRefreshLayout();
        recyclerView.addOnScrollListener(new SwipyAppBarScrollListener(appbar, refreshLayout, recyclerView));
        manager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        mAdapter = new CommonRecyclerViewAdapter<String[]>(mContext, R.layout.item_operation_current_data_list) {
            @Override
            public void convert(BaseRecyclerViewHolder helper, String[] item, int position) {
                TextView tv = helper.getView(R.id.tv_name);
                tv.setText(item[0]);
                tv = helper.getView(R.id.tv_value);
                if (position == 0 || position == 1) {
                    if (item[1].contains("在线") || item[1].contains("合闸")) {
                        tv.setTextColor(ContextCompat.getColor(mContext, R.color.green_dark));
                    } else {
                        tv.setTextColor(ContextCompat.getColor(mContext, R.color.red_light));
                    }
                }
                tv.setText(item[1]);
                tv = helper.getView(R.id.tv_time);
                tv.setText(item[2]);

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
        loadingView.showLoading();
        loadingView.setRetryText(getString(R.string.action_retry));
        loadingView.setRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
    }

    @Override
    protected void initData() {
        tvDeviceName.setText("奥克斯广场总漏保");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (refreshLayout != null)
                    refreshLayout.autoRefresh(true);
            }
        }, 100);
    }

    private void initRefreshLayout() {
        refreshLayout.setAutoLoadMoreEnable(false);
        refreshLayout.setLoadMoreEnable(false);
        refreshLayout.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setData();
                    }
                }, 1000);
            }
        });
    }

    public void refresh() {
        loadingView.showLoading();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                manager.scrollToPosition(0);
                setData();
            }
        }, 1000);
    }

    private void setData() {
        if (!Tools.isNetworkAvailable()) {
            if (refreshLayout != null) {
                if (refreshLayout.isRefreshing())
                    refreshLayout.refreshComplete();
            }
            loadingView.setErrorText(getString(R.string.network_not_available));
            loadingView.showError();
            return;
        }
        if (refreshLayout != null) {
            if (refreshLayout.isRefreshing())
                refreshLayout.refreshComplete();
        }
        loadingView.showContent();
        List<String[]> list = new ArrayList<>();
        list.add(new String[]{"采集器状态", "离线", "2016-12-26 12:56:33"});
        list.add(new String[]{"漏保状态", "合闸", "2016-12-26 12:56:33"});
        list.add(new String[]{"A相电流", "23", "2016-12-26 12:56:33"});
        list.add(new String[]{"B相电流", "26", "2016-12-26 12:56:33"});
        list.add(new String[]{"C相电流", "67", "2016-12-26 12:56:33"});
        list.add(new String[]{"A相电压", "223", "2016-12-26 12:56:33"});
        list.add(new String[]{"B相电压", "233", "2016-12-26 12:56:33"});
        list.add(new String[]{"C相电压", "238", "2016-12-26 12:56:33"});
        list.add(new String[]{"剩余电流", "28", "2016-12-26 12:56:33"});
        mAdapter.fillList(list);
    }

    @OnClick(R.id.bt_change_device)
    public void onClick() {
        final String item[] = new String[]{"奥克斯广场总漏保", "环球中心总漏保", "德商国际总漏保"};
        final NormalListDialog dialog = new NormalListDialog(mContext, item);
        dialog.title(getString(R.string.please_select_device))
                .titleTextColor(ContextCompat.getColor(mContext, R.color.white))
                .titleBgColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .itemPressColor(ContextCompat.getColor(mContext, R.color.gray_light))
                .itemTextColor(ContextCompat.getColor(mContext, R.color.black))
                .cornerRadius(1f)
                .widthScale(0.8f)
                .isTitleShow(true)
                .setCanceledOnTouchOutside(true);
        dialog.show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    default:
                        dialog.dismiss();
                        EventBus.getDefault().post(new BaseEvent(EventType.selectOperationDevice.getType(), item[position]));
                        break;
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)//在ui线程执行 操作ui必须在此线程
    public void onEvent(BaseEvent event) {
        if (event.eventType == EventType.selectOperationDevice.getType()) {
            tvDeviceName.setText((String) event.data);
            refresh();
        }
    }
}
