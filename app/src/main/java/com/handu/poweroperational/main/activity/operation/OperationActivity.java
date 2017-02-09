package com.handu.poweroperational.main.activity.operation;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;

import com.flyco.dialog.widget.NormalListDialog;
import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseActivity;
import com.handu.poweroperational.base.BaseEvent;
import com.handu.poweroperational.main.bean.constants.EventType;
import com.handu.poweroperational.main.bean.results.DeviceResult;
import com.handu.poweroperational.main.fragment.operation.AnalyzeFragment;
import com.handu.poweroperational.main.fragment.operation.CurrentFragment;
import com.handu.poweroperational.main.fragment.operation.DeviceAlarmFragment;
import com.handu.poweroperational.main.fragment.operation.StatisticsFragment;
import com.handu.poweroperational.ui.viewpager.ScrollViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OperationActivity extends BaseActivity {

    @Bind(R.id.tabs)
    TabLayout mTabLayout;
    @Bind(R.id.svg_operation_container)
    ScrollViewPager scrollViewPager;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    public static DeviceResult deviceResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);
        registerEvent();
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        initToolBar(toolbar, getString(R.string.title_operation), true, v -> finish(), true, R.menu.operation, item -> {
            switch (item.getItemId()) {
                case R.id.action_tenement:
//                    gotoActivity(SearchActivity.class, false);
                    break;
                case R.id.action_device:
                    showDeviceDialog();
                    break;
            }
            return false;
        });
    }

    @Override
    protected void initData() {
        FragmentManager fm = getSupportFragmentManager();
        //设置tab模式，当前为系统默认模式
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        //添加tab选项卡
        List<String> titles = new ArrayList<>();
        titles.add("实时");
        titles.add("统计");
        titles.add("分析");
        titles.add("告警");
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(CurrentFragment.newInstance("", ""));
        fragments.add(StatisticsFragment.newInstance("", ""));
        fragments.add(AnalyzeFragment.newInstance("", ""));
        fragments.add(DeviceAlarmFragment.newInstance("", ""));
        MyPagerAdapter adapter = new MyPagerAdapter(fm, fragments, titles);
        scrollViewPager.setAdapter(adapter);
        scrollViewPager.setOffscreenPageLimit(fragments.size());
        mTabLayout.setupWithViewPager(scrollViewPager);
        if (deviceResult == null)
            showDeviceDialog();
    }

    //搜索当前楼宇下的设备
    private void showDeviceDialog() {
        final DeviceResult results[] = {
                new DeviceResult("奥克斯广场总漏保", 1),
                new DeviceResult("环球中心总漏保", 2),
                new DeviceResult("德商国际总漏保", 3),
                new DeviceResult("中国进出口银行总漏保", 4)
        };
        final String item[] = new String[results.length];
        for (int i = 0; i < results.length; i++) {
            item[i] = results[i].getDeviceName();
        }
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
        dialog.setOnOperItemClickL((parent, view1, position, id) -> {
            switch (position) {
                default:
                    dialog.dismiss();
                    deviceResult = results[position];
                    EventBus.getDefault().post(new BaseEvent(EventType.selectOperationDevice.getType(), results[position]));
                    break;
            }
        });
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;
        private List<String> titles;

        MyPagerAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
            super(fm);
            this.fragments = fragments;
            this.titles = titles;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)//在ui线程执行 操作ui必须在此线程
    public void onEvent(BaseEvent event) {
        if (event.eventType == EventType.requestOperationDevice.getType()) {
            showDeviceDialog();
        }
    }
}
