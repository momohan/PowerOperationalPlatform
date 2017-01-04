package com.handu.poweroperational.main.activity.workorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseActivity;
import com.handu.poweroperational.main.bean.constants.WorkOrderType;
import com.handu.poweroperational.main.fragment.workorder.AddTaskWorkOrderFragment;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddWorkOrderActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.sp_work_type)
    MaterialSpinner spWorkType;
    private String currentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_work_order);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        initToolBar(toolbar, "新建工单", true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        WorkOrderType workOrderTypes[] = WorkOrderType.getAllType();
        List<String> list = new ArrayList<>();
        final List<Integer> types = new ArrayList<>();
        for (WorkOrderType workOrderType : workOrderTypes) {
            list.add(workOrderType.getDefaultName());
            types.add(workOrderType.getType());
        }
        if (list.size() > 0) {
            spWorkType.setItems(list);
            spWorkType.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<Object>() {
                @Override
                public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                    switchFragment(types, position);
                }
            });
            switchFragment(types, 0);
        }
    }

    private void switchFragment(List<Integer> types, int position) {

        int type = types.get(position);
        WorkOrderType workOrderType = WorkOrderType.getType(type);
        if (workOrderType != null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            switch (workOrderType) {
                case taskWorkOrder:
                    currentTag = "AddTaskWorkOrderFragment";
                    ft.replace(R.id.fl_container, AddTaskWorkOrderFragment.newInstance(type), currentTag);
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
