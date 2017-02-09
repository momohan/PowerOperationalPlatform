package com.handu.poweroperational.main.fragment.operation;


import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseEvent;
import com.handu.poweroperational.base.BaseFragment;
import com.handu.poweroperational.main.activity.operation.OperationActivity;
import com.handu.poweroperational.main.bean.constants.EventType;
import com.handu.poweroperational.main.bean.results.DeviceResult;
import com.handu.poweroperational.ui.DateTimePicker.utils.DateTimePicker;
import com.handu.poweroperational.ui.RecyclerView.adapter.CommonRecyclerViewAdapter;
import com.handu.poweroperational.ui.RecyclerView.holder.BaseRecyclerViewHolder;
import com.handu.poweroperational.ui.RecyclerView.utils.SwipyAppBarScrollListener;
import com.handu.poweroperational.utils.Tools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ezy.ui.layout.LoadingLayout;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticsFragment extends BaseFragment {

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
    @Bind(R.id.tv_start_time)
    TextView tvStartTime;
    @Bind(R.id.tv_end_time)
    TextView tvEndTime;
    private StaggeredGridLayoutManager manager;
    private CommonRecyclerViewAdapter<String[]> mAdapter;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerEvent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
        ButterKnife.unbind(this);
    }

    public StatisticsFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticsFragment newInstance(String param1, String param2) {
        StatisticsFragment fragment = new StatisticsFragment();
        return fragment;
    }

    @Override
    protected int findView() {
        return R.layout.fragment_statistics;
    }

    @Override
    protected void initView() {
        initLoadingView();
        initRefreshLayout();
        recyclerView.addOnScrollListener(new SwipyAppBarScrollListener(appbar, refreshLayout, recyclerView));
        manager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        mAdapter = new CommonRecyclerViewAdapter<String[]>(mContext, R.layout.item_operation_statistics_data_list) {
            @Override
            public void convert(BaseRecyclerViewHolder helper, String[] item, int position) {
                TextView tv = helper.getView(R.id.tv_name);
                tv.setText(item[0]);
                tv = helper.getView(R.id.tv_current_value);
                tv.setText(item[1]);
                tv = helper.getView(R.id.tv_current_time);
                tv.setText(item[2]);
                tv = helper.getView(R.id.tv_max_value);
                tv.setText(item[3]);
                tv = helper.getView(R.id.tv_max_time);
                tv.setText(item[4]);
                tv = helper.getView(R.id.tv_min_value);
                tv.setText(item[5]);
                tv = helper.getView(R.id.tv_min_time);
                tv.setText(item[6]);
                tv = helper.getView(R.id.tv_average_value);
                tv.setText(item[7]);

            }
        };
        ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(mAdapter);
        alphaAdapter.setInterpolator(new OvershootInterpolator(0.5f));
        alphaAdapter.setDuration(1000);
        alphaAdapter.setFirstOnly(false);
        RecyclerAdapterWithHF adapter = new RecyclerAdapterWithHF(alphaAdapter);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        Calendar calendar = Calendar.getInstance();
        tvEndTime.setText(Tools.ConvertDateToString(calendar.getTime(), "yyyy-MM-dd"));
        calendar.add(Calendar.DATE, -6);
        tvStartTime.setText(Tools.ConvertDateToString(calendar.getTime(), "yyyy-MM-dd"));
        if (OperationActivity.deviceResult != null) {
            tvDeviceName.setText(OperationActivity.deviceResult.getDeviceName());
            mHandler.postDelayed(() -> {
                if (refreshLayout != null)
                    refreshLayout.autoRefresh(true);
            }, 100);
        } else {
            loadingView.showEmpty();
        }
    }

    private void initLoadingView() {
        loadingView.showLoading();
        loadingView.setEmptyImage(R.drawable.ic_vector_no_data);
        loadingView.setEmptyText(getString(R.string.please_select_device));
        loadingView.setRetryText(getString(R.string.action_retry));
        loadingView.setRetryListener(v -> refresh());
    }

    private void initRefreshLayout() {
        refreshLayout.setAutoLoadMoreEnable(false);
        refreshLayout.setLoadMoreEnable(false);
        refreshLayout.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mHandler.postDelayed(() -> setData(), 1000);
            }
        });
    }

    public void refresh() {
        loadingView.showLoading();
        mHandler.postDelayed(() -> {
            manager.scrollToPosition(0);
            setData();
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
        list.add(new String[]{"A相电流", "23", "2016-12-26 12:56:33", "44", "2016-12-26 12:56:33", "12", "2016-12-26 12:56:33", "33"});
        list.add(new String[]{"B相电流", "26", "2016-12-26 12:56:33", "89", "2016-12-26 12:56:33", "21", "2016-12-26 12:56:33", "23"});
        list.add(new String[]{"C相电流", "67", "2016-12-26 12:56:33", "67", "2016-12-26 12:56:33", "21", "2016-12-26 12:56:33", "43"});
        list.add(new String[]{"A相电压", "223", "2016-12-26 12:56:33", "299", "2016-12-26 12:56:33", "220", "2016-12-26 12:56:33", "223"});
        list.add(new String[]{"B相电压", "233", "2016-12-26 12:56:33", "234", "2016-12-26 12:56:33", "210", "2016-12-26 12:56:33", "233"});
        list.add(new String[]{"C相电压", "238", "2016-12-26 12:56:33", "223", "2016-12-26 12:56:33", "219", "2016-12-26 12:56:33", "212"});
        list.add(new String[]{"剩余电流", "28", "2016-12-26 12:56:33", "97", "2016-12-26 12:56:33", "20", "2016-12-26 12:56:33", "43"});
        mAdapter.fillList(list);
    }

    @OnClick({R.id.bt_change_device, R.id.tv_start_time, R.id.tv_end_time, R.id.bt_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_change_device:
                EventBus.getDefault().post(new BaseEvent(EventType.requestOperationDevice.getType()));
                break;
            case R.id.tv_start_time:
                DateTimePicker.show(mContext, getFragmentManager(), DateTimePicker.YEAR_MONTH_DAY, (timePickerView, millSeconds, result) -> tvStartTime.setText(result), tvStartTime.getText().toString() + " 00:00:00");
                break;
            case R.id.tv_end_time:
                DateTimePicker.show(mContext, getFragmentManager(), DateTimePicker.YEAR_MONTH_DAY, (timePickerView, millSeconds, result) -> tvEndTime.setText(result), tvEndTime.getText().toString() + " 23:59:59");
                break;
            case R.id.bt_search:
                refresh();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)//在ui线程执行 操作ui必须在此线程
    public void onEvent(BaseEvent event) {
        if (!isFirstLoad)
            if (event.eventType == EventType.selectOperationDevice.getType()) {
                DeviceResult deviceResult = (DeviceResult) event.data;
                tvDeviceName.setText(deviceResult.getDeviceName());
                refresh();
            }
    }
}
