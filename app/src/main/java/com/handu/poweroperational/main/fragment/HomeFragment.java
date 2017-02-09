package com.handu.poweroperational.main.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseEvent;
import com.handu.poweroperational.base.BaseFragment;
import com.handu.poweroperational.main.bean.constants.EventType;
import com.handu.poweroperational.utils.Tools;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ezy.ui.layout.LoadingLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    @Bind(R.id.pieChart)
    PieChart pieChart;
    @Bind(R.id.lineChart)
    LineChart lineChart;
    @Bind(R.id.loading_view)
    LoadingLayout loadingView;
    @Bind(R.id.refreshLayout)
    PtrClassicFrameLayout refreshLayout;
    private XAxis xAxis;//x轴
    private YAxis yAxis;//y轴
    private boolean isCanRefresh = true;
    private Handler handler = new Handler();

    // TODO: Rename and change types of parameters
    private String mParam1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerEvent();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
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
        handler.removeCallbacksAndMessages(null);
        ButterKnife.unbind(this);
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int findView() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView() {
        initLoadingView();
        initRefreshLayout();
        initPieCharts();
        initChart();
    }

    @Override
    protected void initData() {
        refresh();
    }

    private void initRefreshLayout() {
        refreshLayout.setAutoLoadMoreEnable(false);
        refreshLayout.setLoadMoreEnable(false);
        refreshLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return isCanRefresh;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                handler.postDelayed(() -> setData(), 1000);
            }
        });
    }

    public void refresh() {
        if (!isFirstLoad)
            loadingView.showContent();
        handler.postDelayed(() -> {
            if (refreshLayout != null)
                refreshLayout.autoRefresh(true);
        }, 100);
    }

    //初始化loadingView
    private void initLoadingView() {
        loadingView.showLoading();
        loadingView.setRetryText(getString(R.string.action_retry));
        loadingView.setRetryListener(v -> refresh());
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


        List<String> xPieList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            xPieList.add("故障" + (i + 1));
        }
        List<Float> listPie = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            listPie.add((float) (Math.random() * 10));
        }
        PieDataSet pieDataSet = addPie(listPie, "");
        setPieData(xPieList, pieDataSet, "近一周故障情况");
    }

    /**
     * 初始化LineChart
     */
    private void initChart() {
        /**
         * ====================初始化-自由配置===========================
         */
        lineChart.setNoDataText("");
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

    //初始化饼状图
    private void initPieCharts() {
        pieChart.setDescription("");
        pieChart.setNoDataText("");
        pieChart.setUsePercentValues(true);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setCenterTextSize(10f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawCenterText(true);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });
        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
    }

    private PieDataSet addPie(List<Float> yList, String description) {
        // 每个百分比占区块绘制的不同颜色
        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.MATERIAL_COLORS)
            colors.add(c);
        ArrayList<Entry> yValue = new ArrayList<>();
        for (int i = 0; i < yList.size(); i++) {
            yValue.add(new Entry(yList.get(i), i));
        }
        PieDataSet dataSet = new PieDataSet(yValue, description);
        dataSet.setColors(colors);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        return dataSet;
    }

    private void setPieData(List<String> xList, PieDataSet pieDataSet, String centerText) {

        // 将x轴和y轴设置给PieData作为数据源
        PieData data = new PieData(xList, pieDataSet);
        // 设置成PercentFormatter将追加%号
        data.setValueFormatter(new PercentFormatter());
        // 文字的颜色
        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(10);
        pieChart.setCenterText(centerText);
        pieChart.animateY(1000, Easing.EasingOption.EaseInOutQuad);
        pieChart.animateX(1000, Easing.EasingOption.EaseInOutQuad);
        // 最终将全部完整的数据喂给PieChart
        pieChart.setData(data);
        pieChart.invalidate();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)//在ui线程执行 操作ui必须在此线程
    public void onEvent(BaseEvent event) {
        if (!isFirstLoad)
            if (event.eventType == EventType.homeRefresh.getType()) {
                refresh();
            }
    }
}
