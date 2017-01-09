package com.handu.poweroperational.main.fragment.workorder;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseFragment;
import com.handu.poweroperational.request.callback.JsonDialogCallback;
import com.handu.poweroperational.main.activity.SelectActivity;
import com.handu.poweroperational.main.bean.constants.WorkOrderPriority;
import com.handu.poweroperational.request.RequestServer;
import com.handu.poweroperational.utils.AppConstant;
import com.handu.poweroperational.utils.PreferencesUtils;
import com.handu.poweroperational.utils.ServiceUrl;
import com.handu.poweroperational.utils.Tools;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 工作任务工单添加
 * A simple {@link Fragment} subclass.
 * Use the {@link AddTaskWorkOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddTaskWorkOrderFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private String kh_id;
    @Bind(R.id.et_fzr)
    TextInputEditText etFzr;
    @Bind(R.id.et_address)
    TextInputEditText etAddress;
    @Bind(R.id.et_content)
    TextInputEditText etContent;
    @Bind(R.id.et_kh_mc)
    TextInputEditText etKhMc;
    @Bind(R.id.et_kh_phone)
    TextInputEditText etKhPhone;
    @Bind(R.id.prioritySelector)
    SwipeSelector prioritySelector;
    private int workOrderType;
    private MsgHandler msgHandler = new MsgHandler(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            workOrderType = getArguments().getInt(ARG_PARAM1);
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        msgHandler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }

    public AddTaskWorkOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddTaskWorkOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddTaskWorkOrderFragment newInstance(int workOrderType) {
        AddTaskWorkOrderFragment fragment = new AddTaskWorkOrderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, workOrderType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int findView() {
        return R.layout.fragment_add_task_work_order;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        etFzr.setText(String.valueOf(PreferencesUtils.get(mContext, AppConstant.realName, "")));
        WorkOrderPriority[] priority = WorkOrderPriority.getAllPriority();
        SwipeItem[] items = new SwipeItem[priority.length];
        for (int i = 0; i < priority.length; i++) {
            WorkOrderPriority p = priority[i];
            items[i] = new SwipeItem(p.getPriority(), "工单优先级", p.getDefaultName());
        }
        prioritySelector.setItems(items);
        prioritySelector.selectItemWithValue(WorkOrderPriority.MIDDLE.getPriority(), true);
    }

    @OnClick({R.id.et_kh_mc, R.id.bt_add})
    public void onClick(View view) {
        Map<String, String> map = new HashMap<>();
        map.put("OrganizeId", PreferencesUtils.get(mContext, AppConstant.organizeId, "") + "");
        switch (view.getId()) {
            case R.id.et_kh_mc:
                SelectActivity.runSelectActivityForResult(getActivity(),
                        "客户名称", map, ServiceUrl.GetBuildingList, true, "id", "text", "ChildNodes", R.string.ic_person, new String[]{"ContactPhone1", "BuildingAddress"});
                break;
            case R.id.bt_add:
                addTaskWorkOrder();
                break;
        }
    }

    private void addTaskWorkOrder() {

        String kh_mc = etKhMc.getText().toString();
        String kh_phone = etKhPhone.getText().toString();
        String kh_address = etAddress.getText().toString();
        String content = etContent.getText().toString();
        String fzrId = PreferencesUtils.get(mContext, AppConstant.userId, "") + "";
        String fzrName = PreferencesUtils.get(mContext, AppConstant.realName, "") + "";
        if (TextUtils.isEmpty(kh_mc) || TextUtils.isEmpty(kh_id)) {
            showAlertDialog("请先选择客户");
            return;
        }
        if (TextUtils.isEmpty(content)) {
            showAlertDialog("请填写故障内容");
            return;
        }

        SwipeItem item = prioritySelector.getSelectedItem();
        Map<String, String> map = new HashMap<>();
        map.put("Type", workOrderType + "");
        map.put("Priority", item.value + "");
        map.put("ClientId", kh_id);
        map.put("ClientName", kh_mc);
        map.put("ClientPhone", kh_phone);
        map.put("ClientAddress", kh_address);
        map.put("Content", content);
        map.put("ResponseUserId", fzrId);
        map.put("ResponseUserName", fzrName);
        map.put("CreateUserId", fzrId);
        map.put("CreateUserName", fzrName);
        RequestServer.post(getActivity(), ServiceUrl.SaveWorkOrder, map, new JsonDialogCallback<String>(getActivity(), String.class) {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                msgHandler.sendEmptyMessage(1);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Message message = new Message();
                message.obj = e;
                message.what = -1;
                msgHandler.sendMessage(message);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SelectActivity.REQUEST_CODE) {
            if (data != null) {
                String text = data.getStringExtra(SelectActivity.TEXT);
                String value = data.getStringExtra(SelectActivity.VALUE);
                if (!TextUtils.isEmpty(text) && !TextUtils.isEmpty(value)) {
                    etKhMc.setText(text);
                    kh_id = value;
                }
                List<Map<String, Object>> selectList = (List<Map<String, Object>>) data.getSerializableExtra(SelectActivity.RESULT);
                if (selectList.size() > 0) {
                    Map map = (Map) selectList.get(0).get(SelectActivity.OTHERS);
                    if (map.get("ContactPhone1") != null) {
                        etKhPhone.setText(String.valueOf(map.get("ContactPhone1")));
                    }
                    if (map.get("BuildingAddress") != null) {
                        etAddress.setText(String.valueOf(map.get("BuildingAddress")));
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //弱引用避免内存泄漏
    private static class MsgHandler extends Handler {

        private WeakReference<Fragment> reference;

        public MsgHandler(Fragment fragment) {
            reference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            AddTaskWorkOrderFragment fragment = (AddTaskWorkOrderFragment) reference.get();
            if (fragment != null) {
                switch (msg.what) {
                    case 1:
                        Tools.showToast(fragment.getString(R.string.work_order_add_success));
                        fragment.etKhMc.setText("");
                        fragment.etKhPhone.setText("");
                        fragment.etAddress.setText("");
                        fragment.etContent.setText("");
                        break;
                    case -1:
                        Tools.showToast(fragment.getString(R.string.work_order_add_failure));
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }
}
