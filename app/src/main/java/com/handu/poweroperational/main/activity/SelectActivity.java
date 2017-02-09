package com.handu.poweroperational.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseActivity;
import com.handu.poweroperational.request.RequestServer;
import com.handu.poweroperational.request.callback.StringDialogCallback;
import com.handu.poweroperational.ui.AndroidTree.TreeNodeUtils;
import com.handu.poweroperational.ui.AndroidTree.holder.IconTreeItemHolder;
import com.handu.poweroperational.utils.JsonUtils;
import com.handu.poweroperational.utils.Tools;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.request.BaseRequest;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ezy.ui.layout.LoadingLayout;
import okhttp3.Call;
import okhttp3.Response;

import static com.handu.poweroperational.utils.JsonUtils.JSON_TYPE_ARRAY;
import static com.handu.poweroperational.utils.JsonUtils.JSON_TYPE_ERROR;
import static com.handu.poweroperational.utils.JsonUtils.JSON_TYPE_OBJECT;

public class SelectActivity extends BaseActivity implements TreeNode.TreeNodeClickListener {

    public static final int REQUEST_CODE = 1000;
    public static final String RESULT = "result";
    public static final String TEXT = "text";
    public static final String VALUE = "value";
    public static final String CHILD_NAME = "childName";
    public static final String OTHERS = "others";
    private static final String METHOD = "method";
    private static final String TITLE = "title";
    private static final String PARAMS = "params";
    private static final String SINGLE_SELECTION = "singleSelection";
    private static final String ICON = "icon";

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.ll_container)
    LinearLayout llContainer;
    @Bind(R.id.btn_deselectAll)
    Button btnDeselectAll;
    @Bind(R.id.btn_checkSelection)
    Button btnCheckSelection;
    @Bind(R.id.btn_toggleSelection)
    CheckBox btnToggleSelection;
    @Bind(R.id.btn_selectAll)
    Button btnSelectAll;
    @Bind(R.id.loading_view)
    LoadingLayout loadingView;
    private AndroidTreeView tView;
    private boolean selectionModeEnabled = false;
    private List<Map<String, Object>> selectList;
    private String method;
    private String[] others;
    private Map<String, String> params;
    private String value;
    private int icon;
    private String text;
    private String childName;
    private MsgHandler msgHandler = new MsgHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        ButterKnife.bind(this);
        initView();
        initData();
        getTreeJsonData(params);
    }

    @Override
    protected void onDestroy() {
        OkHttpUtils.getInstance().cancelTag(this);
        msgHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    protected void initView() {
        initLoadingView();
    }

    @Override
    protected void initData() {
        Intent dataIntent = this.getIntent();
        String title = dataIntent.getStringExtra(TITLE);
        method = dataIntent.getStringExtra(METHOD);
        icon = dataIntent.getIntExtra(ICON, R.string.ic_people);
        text = dataIntent.getStringExtra(TEXT);
        value = dataIntent.getStringExtra(VALUE);
        childName = dataIntent.getStringExtra(CHILD_NAME);
        params = (Map<String, String>) dataIntent.getSerializableExtra(PARAMS);
        others = dataIntent.getStringArrayExtra(OTHERS);
        initToolBar(toolbar, title, true, v -> finish(), false, null, null);
        boolean singleSelection = dataIntent.getBooleanExtra(SINGLE_SELECTION, true);
        if (singleSelection) {
            btnToggleSelection.setEnabled(false);
            btnCheckSelection.setClickable(false);
        }
        selectList = new ArrayList<>();
        btnToggleSelection.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectionModeEnabled = true;
                btnCheckSelection.setEnabled(true);
                btnDeselectAll.setEnabled(true);
                btnSelectAll.setEnabled(true);
            } else {
                selectionModeEnabled = false;
                btnCheckSelection.setEnabled(false);
                btnDeselectAll.setEnabled(false);
                btnSelectAll.setEnabled(false);
            }
            tView.setSelectionModeEnabled(selectionModeEnabled);
        });
    }

    private void initLoadingView() {
        loadingView.showLoading();
        loadingView.setRetryListener(v -> getTreeJsonData(params));
        loadingView.setEmptyText(getString(R.string.query_data_is_null));
    }

    private void goBack() {
        Bundle bundle = new Bundle();
        String text = "";
        String value = "";
        for (Map<String, Object> map : selectList) {
            if (!text.equals("")) {
                text = text + ",";
            }
            text = text + map.get(SelectActivity.TEXT);
            if (!value.equals("")) {
                value = value + ",";
            }
            value = value + map.get(SelectActivity.VALUE);
        }
        bundle.putString(TEXT, text);
        bundle.putString(VALUE, value);
        bundle.putSerializable(RESULT, (Serializable) selectList);
        gotoActivitySetResult(bundle, REQUEST_CODE);
    }

    @OnClick({R.id.btn_selectAll, R.id.btn_deselectAll, R.id.btn_checkSelection})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_selectAll:
                tView.selectAll(true);
                break;
            case R.id.btn_deselectAll:
                tView.deselectAll();
                break;
            case R.id.btn_checkSelection:
                List<TreeNode> nodes = tView.getSelected();
                selectList.clear();
                for (TreeNode node : nodes) {
                    if (node.size() == 0) {
                        Map<String, Object> map = new HashMap<>();
                        IconTreeItemHolder.IconTreeItem iconTreeItem = (IconTreeItemHolder.IconTreeItem) node.getValue();
                        map.put(TEXT, iconTreeItem.text);
                        map.put(VALUE, iconTreeItem.value);
                        if (iconTreeItem.others != null) {
                            map.put(OTHERS, iconTreeItem.others);
                        }
                        selectList.add(map);
                    }
                }
                if (selectList.size() > 0) {
                    goBack();
                } else {
                    Tools.showToast("您没有选择任何选项");
                }
                break;
        }
    }

    @Override
    public void onClick(TreeNode node, Object value) {
        if (!selectionModeEnabled) {
            if (node.size() == 0) {
                selectList.clear();
                IconTreeItemHolder.IconTreeItem iconTreeItem = (IconTreeItemHolder.IconTreeItem) value;
                Map<String, Object> map = new HashMap<>();
                map.put(TEXT, iconTreeItem.text);
                map.put(VALUE, iconTreeItem.value);
                if (iconTreeItem.others != null) {
                    map.put(OTHERS, iconTreeItem.others);
                }
                selectList.add(map);
                if (selectList.size() > 0) {
                    goBack();
                } else {
                    Tools.showToast("您没有选择任何选项");
                }
            }
        }
    }

    private void getTreeJsonData(Map<String, String> map) {
        if (!Tools.isNetworkAvailable()) {
            loadingView.setErrorText(getString(R.string.network_not_available));
            loadingView.showError();
            return;
        }
        if (map != null) {
            RequestServer.post(this, method, map, new StringDialogCallback(this) {
                @Override
                public void onBefore(BaseRequest request) {

                }

                @Override
                public void onSuccess(String s, Call call, Response response) {
                    Message msg = new Message();
                    msg.obj = s;
                    msg.what = 0;
                    msgHandler.sendMessage(msg);
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    msgHandler.sendEmptyMessage(-1);
                }
            });
        }
    }

    private static class MsgHandler extends Handler {

        private WeakReference<Activity> reference;

        MsgHandler(Activity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SelectActivity activity = (SelectActivity) reference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 0:
                        try {
                            String s = (String) msg.obj;
                            JSONObject o = new JSONObject(s);
                            boolean success = o.getBoolean("success");
                            String msg_ = o.getString("msg");
                            if (success) {
                                String data = o.getString("data");
                                TreeNode root;
                                switch (JsonUtils.getJsonType(data)) {
                                    case JSON_TYPE_OBJECT:
                                        root = TreeNode.root();
                                        JSONObject object = new JSONObject(data);
                                        if (activity.others != null)
                                            root = TreeNodeUtils.getTreeNode(activity.mContext, object, root, activity.icon, activity.text, activity.value, activity.childName, activity.others);
                                        else
                                            root = TreeNodeUtils.getTreeNode(activity.mContext, object, root, activity.icon, activity.text, activity.value, activity.childName);
                                        activity.tView = new AndroidTreeView(activity.mContext, root);
                                        activity.tView.setDefaultAnimation(true);
                                        activity.tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
                                        activity.tView.setUse2dScroll(true);
                                        activity.tView.setDefaultNodeClickListener(activity);
                                        activity.llContainer.addView(activity.tView.getView());
                                        activity.loadingView.showContent();
                                        break;
                                    case JSON_TYPE_ARRAY:
                                        root = TreeNode.root();
                                        JSONArray array = new JSONArray(data);
                                        if (activity.others != null)
                                            root = TreeNodeUtils.getTreeNode(activity.mContext, array, root, activity.icon, activity.text, activity.value, activity.childName, activity.others);
                                        else
                                            root = TreeNodeUtils.getTreeNode(activity.mContext, array, root, activity.icon, activity.text, activity.value, activity.childName);
                                        activity.tView = new AndroidTreeView(activity.mContext, root);
                                        activity.tView.setDefaultAnimation(true);
                                        activity.tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
                                        activity.tView.setUse2dScroll(true);
                                        activity.tView.setDefaultNodeClickListener(activity);
                                        activity.llContainer.addView(activity.tView.getView());
                                        activity.loadingView.showContent();
                                        break;
                                    case JSON_TYPE_ERROR:
                                        activity.loadingView.setErrorText("请求数据错误");
                                        activity.loadingView.showError();
                                        break;
                                }
                            } else {
                                activity.loadingView.setErrorText(msg_);
                                activity.loadingView.showError();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            activity.loadingView.setErrorText("请求数据错误");
                            activity.loadingView.showError();
                        }
                        break;
                    case -1:
                        activity.loadingView.setErrorText("请求数据错误");
                        activity.loadingView.showError();
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }

    /**
     * @param activity
     * @param title           标题
     * @param params          参数
     * @param method          方法名
     * @param singleSelection 是否单选
     * @param value           解析value的键
     * @param text            解析text的值键
     * @param childName       子集的键
     */
    public static void runSelectActivityForResult(
            Activity activity, String title, Map<String, String> params, String method,
            boolean singleSelection, String value, String text, String childName, Integer icon) {
        runSelectActivityForResult(activity, title, params, method, singleSelection, value, text, childName, icon, null);
    }

    /**
     * @param activity
     * @param title           标题
     * @param params          参数
     * @param method          方法名
     * @param singleSelection 是否单选
     * @param value           解析value的键
     * @param text            解析text的值键
     * @param childName       子集的键
     */
    public static void runSelectActivityForResult(
            Activity activity, String title, Map<String, String> params, String method,
            boolean singleSelection, String value, String text, String childName, String others[]) {
        runSelectActivityForResult(activity, title, params, method, singleSelection, value, text, childName, null, others);
    }

    /**
     * @param activity
     * @param title           标题
     * @param params          参数
     * @param method          方法名
     * @param singleSelection 是否单选
     * @param value           解析value的键
     * @param text            解析text的值键
     * @param childName       子集的键
     */
    public static void runSelectActivityForResult(
            Activity activity, String title, Map<String, String> params, String method,
            boolean singleSelection, String value, String text, String childName) {
        runSelectActivityForResult(activity, title, params, method, singleSelection, value, text, childName, null, null);
    }

    /**
     * @param activity
     * @param title           标题
     * @param params          参数
     * @param method          方法名
     * @param singleSelection 是否单选
     * @param value           解析value的键
     * @param text            解析text的值键
     * @param childName       子集的键
     * @param others          其他字段的键
     */
    public static void runSelectActivityForResult(
            Activity activity, String title, Map<String, String> params,
            String method, boolean singleSelection, String value, String text, String childName, Integer icon, String[] others) {
        Bundle bundle = new Bundle();
        if (icon != null)
            bundle.putInt(ICON, icon);
        bundle.putString(TITLE, title);
        bundle.putBoolean(SINGLE_SELECTION, singleSelection);
        if (params != null)
            bundle.putSerializable(PARAMS, (Serializable) params);
        bundle.putString(METHOD, method);
        if (others != null) {
            bundle.putStringArray(OTHERS, others);
        }
        bundle.putString(VALUE, value);
        bundle.putString(TEXT, text);
        bundle.putString(CHILD_NAME, childName);
        Intent intent = new Intent(activity, SelectActivity.class);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }
}
