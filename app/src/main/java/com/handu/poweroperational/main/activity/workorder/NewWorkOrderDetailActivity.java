package com.handu.poweroperational.main.activity.workorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseActivity;
import com.handu.poweroperational.main.bean.constants.WorkOrderType;
import com.handu.poweroperational.main.bean.results.WorkOrderResult;
import com.handu.poweroperational.main.fragment.workorder.TaskNewWorkOrderDetailFragment;

import butterknife.Bind;
import butterknife.ButterKnife;


public class NewWorkOrderDetailActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private WorkOrderResult workOrderResult;
    int position;
    private String currentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_work_order_detail);
        ButterKnife.bind(this);
        initView();
        initData();

    }

    @Override
    protected void initView() {
        initToolBar(toolbar, getString(R.string.title_work_order_handle_detail), true, v -> finish());
        initImagePicker(9);
    }

    @Override

    protected void initData() {
        workOrderResult = getIntent().getParcelableExtra("workOrderResult");
        position = getIntent().getIntExtra("position", 0);
        int type = workOrderResult.getType();
        WorkOrderType workOrderType = WorkOrderType.getType(type);
        if (workOrderType != null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            switch (workOrderType) {
                case taskWorkOrder:
                    currentTag = "TaskNewWorkOrderDetailFragment";
                    ft.replace(R.id.fl_container, TaskNewWorkOrderDetailFragment.newInstance(workOrderResult, position), currentTag);
                    break;
            }
            ft.commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment f = getSupportFragmentManager().findFragmentByTag(currentTag);
            /*然后在碎片中调用重写的onActivityResult方法*/
        f.onActivityResult(requestCode, resultCode, data);
    }
}
