package com.handu.poweroperational.main.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseActivity;
import com.handu.poweroperational.ui.RecyclerView.adapter.CommonRecyclerViewAdapter;
import com.handu.poweroperational.ui.RecyclerView.holder.BaseRecyclerViewHolder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LuckyExtractActivity extends BaseActivity {

    @Bind(R.id.tv_lucky_name)
    TextView tvLuckyName;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.et_number)
    EditText etNumber;
    private Timer timer;
    private CommonRecyclerViewAdapter<String> adapter;
    private List<String> nameList;
    private Set<String> luckyList;
    private boolean isStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucky_extract);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        luckyList = new HashSet<>();
        nameList = Arrays.asList("冯豪", "刘柳", "杜正伟", "刘坤路", "王杰", "张海涛", "张智灵", "刘永强");
        adapter = new CommonRecyclerViewAdapter<String>(mContext, R.layout.layout_item_type_1, nameList) {
            @Override
            public void convert(BaseRecyclerViewHolder holder, String item, int position) {
                TextView tv = holder.getView(R.id.tv_type1_title);
                tv.setText(item);
            }
        };
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    @OnClick({R.id.bt_start, R.id.bt_stop})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_start:
                if (isStart) {
                    return;
                }
                isStart = true;
                luckyList.clear();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        final String name = nameList.get((int) (Math.random() * nameList.size()));
                        tvLuckyName.post(() -> tvLuckyName.setText(name));
                    }
                }, 0, 50);
                break;
            case R.id.bt_stop:
                if (!isStart) {
                    return;
                }
                isStart = false;
                int number;
                if (timer != null)
                    timer.cancel();
                String num = etNumber.getText().toString();
                if (TextUtils.isEmpty(num)) {
                    num = "1";
                    etNumber.setText(num);
                }
                number = Integer.parseInt(num);
                if (number > nameList.size()) {
                    number = nameList.size();
                    etNumber.setText(String.valueOf(nameList.size()));
                }
                if (number <= 0) {
                    number = 1;
                    etNumber.setText("1");
                }
                while (luckyList.size() < number) {
                    String name = nameList.get((int) (Math.random() * nameList.size()));
                    luckyList.add(name);
                }
                tvLuckyName.setText(luckyList.toString().replace("[", "").replace("]", ""));
                break;
        }
    }
}
