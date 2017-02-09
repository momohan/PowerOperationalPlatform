package com.handu.poweroperational.main.activity.operation;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;

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
import com.handu.poweroperational.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AnalyzeChartsMaxActivity extends BaseActivity {

    @Bind(R.id.lineChart)
    LineChart lineChart;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private XAxis xAxis;//x轴
    private YAxis yAxis;//y轴

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_charts_max);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        initToolBar(toolbar, "详情", true, v -> finish());
        initChart();
    }

    @Override
    protected void initData() {
        setData();
    }

    private void setData() {
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
        lineChart.setScaleXEnabled(true);
        lineChart.setScaleYEnabled(true);
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
}
