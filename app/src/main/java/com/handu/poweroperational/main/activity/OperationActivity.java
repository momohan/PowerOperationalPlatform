package com.handu.poweroperational.main.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseActivity;
import com.handu.poweroperational.main.fragment.operaton.AnalyzeFragment;
import com.handu.poweroperational.main.fragment.operaton.CurrentFragment;
import com.handu.poweroperational.main.fragment.operaton.DeviceAlarmFragment;
import com.handu.poweroperational.main.fragment.operaton.StatisticsFragment;
import com.handu.poweroperational.ui.viewpager.ScrollViewPager;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        initToolBar(toolbar, "运维", true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, true, R.menu.main, new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_tenement:
                        gotoActivity(SearchActivity.class, false);
                        break;
                }
                return false;
            }
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
}
