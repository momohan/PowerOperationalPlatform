package com.handu.poweroperational.main.fragment.operation;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseEvent;
import com.handu.poweroperational.base.BaseFragment;
import com.handu.poweroperational.main.activity.operation.AnalyzeChartsMaxActivity;
import com.handu.poweroperational.main.activity.operation.OperationActivity;
import com.handu.poweroperational.main.bean.constants.AnalyzeType;
import com.handu.poweroperational.main.bean.constants.EventType;
import com.handu.poweroperational.main.bean.results.DeviceResult;
import com.handu.poweroperational.ui.DateTimePicker.utils.DateTimePicker;
import com.handu.poweroperational.ui.RecyclerView.adapter.CommonRecyclerViewAdapter;
import com.handu.poweroperational.ui.RecyclerView.click.ItemClickListener;
import com.handu.poweroperational.ui.RecyclerView.holder.BaseRecyclerViewHolder;
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
 * Use the {@link AnalyzeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnalyzeFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private XAxis xAxis;//x轴
    private YAxis yAxis;//y轴
    @Bind(R.id.tv_device_name)
    TextView tvDeviceName;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.lineChart)
    LineChart lineChart;
    private Handler mHandler = new Handler();
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.loading_view)
    LoadingLayout loadingView;
    @Bind(R.id.appbar)
    AppBarLayout appbar;
    private CommonRecyclerViewAdapter<AnalyzeItem> adapter;
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
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
        ButterKnife.unbind(this);
    }

    public AnalyzeFragment() {
    }

    public static AnalyzeFragment newInstance(String param1, String param2) {
        AnalyzeFragment fragment = new AnalyzeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int findView() {
        return R.layout.fragment_analyze;
    }

    @Override
    protected void initView() {
        initLoadingView();
        recyclerView.addOnItemTouchListener(new ItemClickListener(recyclerView, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AnalyzeItem item = adapter.getItem(position);
                setData();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        initChart();
    }

    private void initLoadingView() {
        loadingView.showLoading();
        loadingView.setEmptyImage(R.drawable.ic_vector_no_data);
        loadingView.setEmptyText(getString(R.string.please_select_device));
        loadingView.setRetryText(getString(R.string.action_retry));
        loadingView.setRetryListener(v -> initData());
    }

    @Override
    protected void initData() {
        Calendar calendar = Calendar.getInstance();
        tvTime.setText(Tools.ConvertDateToString(calendar.getTime(), "yyyy-MM-dd"));
        AnalyzeType types[] = AnalyzeType.getAllAnalyzeType();
        List<AnalyzeItem> list = new ArrayList<>();
        for (AnalyzeType analyzeType : types) {
            list.add(new AnalyzeItem(analyzeType.getType(), analyzeType.getName()));
        }
        adapter = new CommonRecyclerViewAdapter<AnalyzeItem>(mContext, R.layout.item_analyze_search_type, list) {
            @Override
            public void convert(BaseRecyclerViewHolder holder, AnalyzeItem item, int position) {
                TextView tv = holder.getView(R.id.item_title);
                tv.setText(item.name);
            }
        };
        LinearLayoutManager manager = new LinearLayoutManager(mContext, StaggeredGridLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);
        ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(adapter);
        alphaAdapter.setInterpolator(new OvershootInterpolator(0.5f));
        alphaAdapter.setDuration(1000);
        alphaAdapter.setFirstOnly(false);
        RecyclerAdapterWithHF adapter = new RecyclerAdapterWithHF(alphaAdapter);
        recyclerView.setAdapter(adapter);
        if (OperationActivity.deviceResult != null) {
            tvDeviceName.setText(OperationActivity.deviceResult.getDeviceName());
            setData();
        } else {
            loadingView.showEmpty();
        }
    }

    private void setData() {
        loadingView.showLoading();
        mHandler.postDelayed(() -> {
            loadingView.showContent();
            List<ILineDataSet> iLineDataSets = new ArrayList<>();//线集合
            List<Float> list = new ArrayList<>();//y值
            list.add(15f);
            list.add(19f);
            list.add(6f);
            list.add(5f);
            list.add(7f);
            List<String> xList = new ArrayList<>();//x值
            for (int i = 0; i < 5; i++) {
                xList.add("星期" + (i + 1));
            }
            iLineDataSets.add(addLine(list, "故障数量", ContextCompat.getColor(mContext, R.color.red_light)));
            list = new ArrayList<>();//y值
            list.add(5f);
            list.add(9f);
            list.add(18f);
            list.add(13f);
            list.add(7f);
            xList = new ArrayList<>();//x值
            for (int i = 0; i < 5; i++) {
                xList.add("星期" + (i + 1));
            }
            iLineDataSets.add(addLine(list, "工单数量", ContextCompat.getColor(mContext, R.color.green_dark)));
            list = new ArrayList<>();//y值
            list.add(15f);
            list.add(5f);
            list.add(7f);
            list.add(22f);
            list.add(10f);
            xList = new ArrayList<>();//x值
            for (int i = 0; i < 5; i++) {
                xList.add("星期" + (i + 1));
            }
            iLineDataSets.add(addLine(list, "告警数量", ContextCompat.getColor(mContext, R.color.blue_dark)));
            setLineData(xList, iLineDataSets, "");
        }, 2000);
    }

    /**
     * 初始化LineChart
     */
    private void initChart() {
        /**
         * ====================初始化-自由配置===========================
         */
        lineChart.setNoDataText(getString(R.string.no_charts_data));
        // 是否在折线图上添加边框
        lineChart.setDrawGridBackground(true);
        lineChart.setDrawBorders(true);
        //设置透明度
        lineChart.setAlpha(0.8f);
        //设置网格底下的那条线的颜色
        lineChart.setBorderColor(Color.BLACK);
        //设置高亮显示
        lineChart.setHighlightPerDragEnabled(false);
        lineChart.setHighlightPerTapEnabled(true);
        //设置是否可以触摸，如为false，则不能拖动，缩放等
        lineChart.setTouchEnabled(true);
        //设置是否可以拖拽
        lineChart.setDragEnabled(true);
        //设置x,y是否可以缩放
        lineChart.setScaleXEnabled(false);
        lineChart.setScaleYEnabled(false);
        //设置是否能扩大扩小
        lineChart.setPinchZoom(false);
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int dataSetIndex, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });
        /**
         * ====================x，y动画效果===========================
         */
        //从X轴进入的动画
        lineChart.animateXY(1000, 1000);//从XY轴一起进入的动画
        Legend l = lineChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setForm(Legend.LegendForm.SQUARE);  //设置图最下面显示的类型
        l.setTextSize(10f);
        l.setTextColor(Color.BLACK);
        l.setFormSize(6f);
        /**
         *========= 初始化坐标轴
         */
        //x轴
        xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //y轴
        yAxis = lineChart.getAxisLeft();
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(true);
        lineChart.setOnLongClickListener(v -> {
            gotoActivity(AnalyzeChartsMaxActivity.class, false);
            return false;
        });
    }

    /**
     * @param value 限制值
     * @param title 限制说明
     * @param Axis  显示在那个轴
     */
    private void addLimitLine(float value, String title, AxisBase Axis, int color) {
        LimitLine ll = new LimitLine(value, title);
        ll.setLineColor(color);
        ll.setTextColor(color);
        ll.enableDashedLine(10f, 10f, 0f);
        ll.setLineWidth(2f);
        ll.setTextSize(6f);
        Axis.addLimitLine(ll);
    }

    /**
     * @param yList 数据点集合
     * @param title 线说明
     * @param color 线颜色
     * @return 线集合
     */
    private LineDataSet addLine(List<Float> yList, String title, int color) {

        List<Entry> values = new ArrayList<>();
        for (int i = 0; i < yList.size(); i++) {
            float val = yList.get(i);
            values.add(new Entry(val, i));
        }
        LineDataSet set = new LineDataSet(values, title);
        set.setColor(color);
        set.setCircleColor(color);
        set.setLineWidth(3f);
        set.setCircleRadius(3f);
        set.setDrawCircleHole(true);
        set.setValueTextSize(10f);
        set.setDrawFilled(true);
        set.setLineWidth(2f);
        set.setCubicIntensity(2f);
        set.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        set.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format("%.2f", value));
        return set;
    }

    /**
     * @param iLineDataSets 数据集合
     * @param description   表格描述
     */
    private void setLineData(List<String> xList, List<ILineDataSet> iLineDataSets, String description) {

        LineData data = new LineData(xList, iLineDataSets);
        lineChart.clear();
        lineChart.setData(data);
        // 设置右下角描述
        lineChart.setDescriptionTextSize(12f);
        lineChart.setDescription(description);
        lineChart.animateY(1000, Easing.EasingOption.EaseInOutQuad);
        lineChart.animateX(1000, Easing.EasingOption.EaseInOutQuad);
        lineChart.setDescriptionColor(ContextCompat.getColor(mContext, R.color.red_dark));
        // 刷新图表
        lineChart.invalidate();
    }

    @OnClick({R.id.bt_change_device, R.id.tv_time, R.id.bt_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_change_device:
                EventBus.getDefault().post(new BaseEvent(EventType.requestOperationDevice.getType()));
                break;
            case R.id.tv_time:
                DateTimePicker.show(mContext, getFragmentManager(), DateTimePicker.YEAR_MONTH_DAY, (timePickerView, millSeconds, result) -> {
                    tvTime.setText(result);
                    setData();
                }, tvTime.getText().toString() + " 00:00:00");
                break;
            case R.id.bt_search:
                setData();
                break;
        }
    }

    class AnalyzeItem {
        public int type;
        public String name;

        AnalyzeItem(int type, String name) {
            this.type = type;
            this.name = name;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)//在ui线程执行 操作ui必须在此线程
    public void onEvent(BaseEvent event) {
        if (!isFirstLoad)
            if (event.eventType == EventType.selectOperationDevice.getType()) {
                DeviceResult deviceResult = (DeviceResult) event.data;
                tvDeviceName.setText(deviceResult.getDeviceName());
                setData();
            }
    }
}
