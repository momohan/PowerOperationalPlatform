package com.handu.poweroperational.main.fragment.workorder;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.google.gson.reflect.TypeToken;
import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseFragment;
import com.handu.poweroperational.request.callback.JsonDialogCallback;
import com.handu.poweroperational.main.activity.workorder.AddWorkOrderActivity;
import com.handu.poweroperational.main.activity.workorder.HistoryWorkOrderDetailActivity;
import com.handu.poweroperational.main.bean.constants.WorkOrderPriority;
import com.handu.poweroperational.main.bean.constants.WorkOrderState;
import com.handu.poweroperational.main.bean.constants.WorkOrderType;
import com.handu.poweroperational.main.bean.results.WorkOrderResult;
import com.handu.poweroperational.request.RequestServer;
import com.handu.poweroperational.ui.RecyclerView.ItemClickListener;
import com.handu.poweroperational.ui.RecyclerView.adapter.BaseRecyclerViewHolder;
import com.handu.poweroperational.ui.RecyclerView.adapter.CommonRecyclerViewAdapter;
import com.handu.poweroperational.utils.AnimationUtil;
import com.handu.poweroperational.utils.AppConstant;
import com.handu.poweroperational.utils.PreferencesUtils;
import com.handu.poweroperational.utils.ServiceUrl;
import com.handu.poweroperational.utils.Tools;
import com.haozhang.lib.SlantedTextView;
import com.jayfang.dropdownmenu.DropDownMenu;
import com.jayfang.dropdownmenu.OnMenuSelectedListener;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.request.BaseRequest;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ezy.ui.layout.LoadingLayout;
import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;
import okhttp3.Call;
import okhttp3.Response;

/**
 * A simple {@link BaseFragment} subclass.
 * Use the {@link HistoryWorkOrderListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryWorkOrderListFragment extends BaseFragment {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.refreshLayout)
    PtrClassicFrameLayout refreshLayout;
    @Bind(R.id.fab_go_top)
    FloatingActionButton fabGoTop;
    @Bind(R.id.fab_add)
    FloatingActionButton fabAdd;
    @Bind(R.id.drop_down_menu)
    DropDownMenu dropDownMenu;
    @Bind(R.id.loading_view)
    LoadingLayout loadingView;
    private CommonRecyclerViewAdapter<WorkOrderResult> mAdapter;
    private LinearLayoutManager manager;
    private int size = 20;
    private int page = 1;
    private String workType;//类型
    private String workState;//状态
    private String workPriority;//优先级
    private String[] titles = new String[]{"工单类型", "工单状态", "优先级"};
    private String[] typeValues;
    private String[] stateValues;
    private String[] priorityValues;
    private MsgHandler handler = new MsgHandler(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        ButterKnife.unbind(this);
        OkHttpUtils.getInstance().cancelTag(this);
        handler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }

    public HistoryWorkOrderListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewWorkOrderListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryWorkOrderListFragment newInstance() {
        HistoryWorkOrderListFragment fragment = new HistoryWorkOrderListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int findView() {
        return R.layout.fragment_history_work_order;
    }

    @Override
    protected void initView() {
        initLoadingView();
        initDropDownMenu();
        initRefreshLayout();
        fabAdd.setVisibility(View.VISIBLE);
        recyclerView.addOnItemTouchListener(new ItemClickListener(recyclerView,
                new ItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        WorkOrderResult workOrderResult = mAdapter.getItem(position);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("workOrderResult", workOrderResult);
                        bundle.putInt("position", position);
                        gotoActivity(HistoryWorkOrderDetailActivity.class, bundle, false);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                    }
                }));
        manager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(manager);
        mAdapter = new CommonRecyclerViewAdapter<WorkOrderResult>(mContext, R.layout.item_new_work_order_list) {
            @Override
            public void convert(BaseRecyclerViewHolder helper, WorkOrderResult item, int position) {
                TextView tv = helper.getView(R.id.tv_work_priority);
                WorkOrderPriority priority = WorkOrderPriority.getPriority(item.getPriority());
                if (priority != null)
                    tv.setText(priority.getColorName());
                tv = helper.getView(R.id.tv_work_client_name);
                tv.setText(item.getClientName() == null ? "" : item.getClientName());
                tv = helper.getView(R.id.tv_work_num);
                tv.setText(item.getNumber());
                tv = helper.getView(R.id.tv_work_fzr);
                tv.setText(item.getResponseUserName() == null ? "无" : item.getResponseUserName());
                int state = item.getCurrentState();
                WorkOrderState orderState = WorkOrderState.getState(state);
                SlantedTextView slantedTextView = helper.getView(R.id.tv_position);
                if (orderState != null) {
                    slantedTextView.setSlantedBackgroundColor(orderState.getTextColor());
                    slantedTextView.setText(position + 1 + "：" + orderState.getDefaultName());
                }
                tv = helper.getView(R.id.tv_work_type);
                int type = item.getType();
                WorkOrderType workOrderType = WorkOrderType.getType(type);
                if (workOrderType != null)
                    tv.setText(workOrderType.getColorName());

            }
        };
        SlideInLeftAnimationAdapter alphaAdapter = new SlideInLeftAnimationAdapter(mAdapter);
        alphaAdapter.setInterpolator(new OvershootInterpolator(0.5f));
        alphaAdapter.setDuration(1000);
        alphaAdapter.setFirstOnly(false);
        RecyclerAdapterWithHF adapter = new RecyclerAdapterWithHF(alphaAdapter);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        refresh();
    }

    private void initLoadingView() {
        loadingView.showLoading();
        loadingView.setRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingView.showContent();
                refresh();
            }
        });
    }

    private void initRefreshLayout() {
        refreshLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                hideFABTop();
                return super.checkCanDoRefresh(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                page = 1;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setData(0);
                    }
                }, 1000);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                page++;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setData(1);
                    }
                }, 1000);
            }
        });
    }

    private void initDropDownMenu() {
        WorkOrderState[] state = WorkOrderState.getAllState();
        String[] stateTitles = new String[state.length + 1];
        stateValues = new String[state.length + 1];
        stateTitles[0] = "全部状态";
        stateValues[0] = "";
        for (int i = 0; i < state.length; i++) {
            stateTitles[i + 1] = state[i].getDefaultName();
            stateValues[i + 1] = state[i].getState() + "";
        }
        WorkOrderType[] type = WorkOrderType.getAllType();
        String[] typeTitles = new String[type.length + 1];
        typeValues = new String[type.length + 1];
        typeTitles[0] = "全部类型";
        typeValues[0] = "";
        for (int i = 0; i < type.length; i++) {
            typeTitles[i + 1] = type[i].getDefaultName();
            typeValues[i + 1] = type[i].getType() + "";
        }
        WorkOrderPriority[] priority = WorkOrderPriority.getAllPriority();
        String[] priorityTitles = new String[priority.length + 1];
        priorityValues = new String[priority.length + 1];
        priorityTitles[0] = "所有优先级";
        priorityValues[0] = "";
        for (int i = 0; i < priority.length; i++) {
            priorityTitles[i + 1] = priority[i].getDefaultName();
            priorityValues[i + 1] = priority[i].getPriority() + "";
        }
        dropDownMenu.setmMenuCount(3);
        dropDownMenu.setmShowCount(10);
        dropDownMenu.setShowCheck(true);
        dropDownMenu.setmMenuTitleTextSize(16);
        dropDownMenu.setmMenuTitleTextColor(ContextCompat.getColor(mContext, R.color.black));
        dropDownMenu.setmMenuListTextSize(16);
        dropDownMenu.setmMenuListTextColor(ContextCompat.getColor(mContext, R.color.black));
        dropDownMenu.setmMenuBackColor(ContextCompat.getColor(mContext, R.color.white));
        dropDownMenu.setmMenuPressedBackColor(ContextCompat.getColor(mContext, R.color.gray_light));
        dropDownMenu.setmMenuPressedTitleTextColor(ContextCompat.getColor(mContext, R.color.white));
        dropDownMenu.setmCheckIcon(R.drawable.ico_make);
        dropDownMenu.setmUpArrow(R.drawable.ic_vector_drop_down_selected);
        dropDownMenu.setmDownArrow(R.drawable.ic_vector_drop_down_unselected);
        dropDownMenu.setDefaultMenuTitle(titles);
        dropDownMenu.setShowDivider(true);
        dropDownMenu.setmMenuListBackColor(ContextCompat.getColor(mContext, R.color.white));
        dropDownMenu.setmMenuListSelectorRes(R.color.white);
        dropDownMenu.setmArrowMarginTitle(20);
        dropDownMenu.setMenuSelectedListener(new OnMenuSelectedListener() {
            @Override
            public void onSelected(View view, int RowIndex, int ColumnIndex) {
                switch (ColumnIndex) {
                    case 0://类型
                        workType = typeValues[RowIndex];
                        break;
                    case 1://状态
                        workState = stateValues[RowIndex];
                        break;
                    case 2://优先级
                        workPriority = priorityValues[RowIndex];
                        break;
                }
                refresh();
            }
        });
        List<String[]> items = new ArrayList<>();
        items.add(typeTitles);
        items.add(stateTitles);
        items.add(priorityTitles);
        dropDownMenu.setmMenuItems(items);
        dropDownMenu.setIsDebug(false);
    }

    private void hideFABTop() {
        AnimationUtil.scaleHide(fabGoTop, new ViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {
            }

            @Override
            public void onAnimationEnd(View view) {
                fabGoTop.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(View view) {
            }
        });
    }

    private void refresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (refreshLayout != null) {
                    refreshLayout.autoRefresh(true);
                }
            }
        }, 1);
    }

    /**
     * 滚动到顶部
     */
    private void scrollToTop() {
        hideFABTop();
        manager.scrollToPosition(0);
    }

    /**
     * @param type 0 刷新 1加载更多
     */
    private void setData(int type) {
        if (!Tools.isNetworkAvailable()) {
            if (refreshLayout != null) {
                if (refreshLayout.isRefreshing())
                    refreshLayout.refreshComplete();
                if (refreshLayout.isLoadingMore())
                    refreshLayout.loadMoreComplete(false);
            }
            mAdapter.clearAll();
            loadingView.setErrorText(getString(R.string.network_not_available));
            loadingView.showError();
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("page", page + "");
        map.put("rows", size + "");
        if (!TextUtils.isEmpty(workState))
            map.put("CurrentState", workState);
        if (!TextUtils.isEmpty(workType))
            map.put("Type", workType);
        if (!TextUtils.isEmpty(workPriority))
            map.put("Priority", workPriority);
        map.put("userId", PreferencesUtils.get(mContext, AppConstant.userId, "") + "");
        switch (type) {
            case 0:
                RequestServer.post(getActivity(), ServiceUrl.GetHistoryList, map,
                        new JsonDialogCallback<List<WorkOrderResult>>(getActivity(), new TypeToken<List<WorkOrderResult>>() {
                        }.getType()) {

                            @Override
                            public void onBefore(BaseRequest request) {
                            }

                            @Override
                            public void onSuccess(List<WorkOrderResult> workOrderResults, Call call, Response response) {
                                Message message = new Message();
                                message.what = 1;
                                message.obj = workOrderResults;
                                handler.sendMessage(message);
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                Message message = new Message();
                                message.what = 2;
                                message.obj = e;
                                handler.sendMessage(message);
                            }
                        });
                break;
            case 1:
                RequestServer.post(getActivity(), ServiceUrl.GetHistoryList, map,
                        new JsonDialogCallback<List<WorkOrderResult>>(getActivity(), new TypeToken<List<WorkOrderResult>>() {
                        }.getType()) {

                            @Override
                            public void onBefore(BaseRequest request) {
                            }

                            @Override
                            public void onSuccess(List<WorkOrderResult> workOrderResults, Call call, Response response) {
                                Message message = new Message();
                                message.what = 3;
                                message.obj = workOrderResults;
                                handler.sendMessage(message);
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                Message message = new Message();
                                message.what = 4;
                                message.obj = e;
                                handler.sendMessage(message);
                            }
                        });
                break;
        }
    }

    @OnClick({R.id.fab_go_top, R.id.fab_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_go_top:
                scrollToTop();
                break;
            case R.id.fab_add:
                gotoActivity(AddWorkOrderActivity.class, false);
                break;
        }
    }

    //弱引用避免内存泄漏
    private static class MsgHandler extends Handler {

        private WeakReference<Fragment> reference;

        MsgHandler(Fragment context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            HistoryWorkOrderListFragment fragment = (HistoryWorkOrderListFragment) reference.get();
            if (fragment != null) {
                if (fragment.isAdded()) {
                    List<WorkOrderResult> workOrderResults;
                    Exception e;
                    switch (msg.what) {
                        case 1:
                            workOrderResults = (List<WorkOrderResult>) msg.obj;
                            if (workOrderResults != null && workOrderResults.size() != 0) {
                                fragment.mAdapter.fillList(workOrderResults);
                                if (fragment.refreshLayout != null) {
                                    fragment.refreshLayout.refreshComplete();
                                    if (workOrderResults.size() < fragment.size) {
                                        fragment.refreshLayout.setLoadMoreEnable(false);
                                    } else {
                                        fragment.refreshLayout.setLoadMoreEnable(true);
                                    }
                                }
                                fragment.loadingView.showContent();
                            } else {
                                fragment.mAdapter.clearAll();
                                if (fragment.refreshLayout != null) {
                                    fragment.refreshLayout.refreshComplete();
                                    fragment.refreshLayout.setLoadMoreEnable(false);
                                }
                                fragment.loadingView.setErrorText(fragment.getString(R.string.query_data_is_null));
                                fragment.loadingView.setRetryText(fragment.getString(R.string.action_retry));
                                fragment.loadingView.showError();
                            }
                            break;
                        case 2:
                            e = (Exception) msg.obj;
                            fragment.mAdapter.clearAll();
                            if (fragment.refreshLayout != null) {
                                fragment.refreshLayout.refreshComplete();
                                fragment.refreshLayout.setLoadMoreEnable(false);
                            }
                            fragment.loadingView.setErrorText(e.getMessage());
                            fragment.loadingView.showError();
                            break;
                        case 3:
                            workOrderResults = (List<WorkOrderResult>) msg.obj;
                            if (workOrderResults != null && workOrderResults.size() != 0) {
                                fragment.mAdapter.appendList(workOrderResults);
                                if (fragment.refreshLayout != null) {
                                    fragment.refreshLayout.loadMoreComplete(true);
                                    if (workOrderResults.size() < fragment.size) {
                                        fragment.refreshLayout.setLoadMoreEnable(false);
                                    } else {
                                        fragment.refreshLayout.setLoadMoreEnable(true);
                                    }
                                }
                            } else {
                                Tools.showToast(fragment.getString(R.string.query_no_more_data));
                                if (fragment.refreshLayout != null) {
                                    fragment.refreshLayout.loadMoreComplete(true);
                                    fragment.refreshLayout.setLoadMoreEnable(false);
                                }
                            }
                            break;
                        case 4:
                            e = (Exception) msg.obj;
                            if (fragment.refreshLayout != null) {
                                fragment.refreshLayout.loadMoreComplete(true);
                            }
                            Tools.showToast(e.getMessage());
                            break;
                    }
                }
            }
            super.handleMessage(msg);
        }
    }
}
