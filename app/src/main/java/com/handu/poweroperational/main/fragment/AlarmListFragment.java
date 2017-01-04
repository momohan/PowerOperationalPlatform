package com.handu.poweroperational.main.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseEvent;
import com.handu.poweroperational.base.BaseFragment;
import com.handu.poweroperational.main.bean.constants.EventType;
import com.handu.poweroperational.ui.RecyclerView.ItemClickListener;
import com.handu.poweroperational.ui.RecyclerView.adapter.BaseRecyclerViewHolder;
import com.handu.poweroperational.ui.RecyclerView.adapter.CommonRecyclerViewAdapter;
import com.haozhang.lib.SlantedTextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlarmListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlarmListFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.refreshLayout)
    PtrClassicFrameLayout refreshLayout;
    private CommonRecyclerViewAdapter<String> mAdapter;
    private RecyclerAdapterWithHF adapter;
    private int size = 20;
    private int page = 1;
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

    public AlarmListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment AlarmListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlarmListFragment newInstance(String param1) {
        AlarmListFragment fragment = new AlarmListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)//在ui线程执行 操作ui必须在此线程
    public void onEvent(BaseEvent event) {
        if (event.eventType == EventType.alarmNumUpdate.getType()) {
            showSnackbar(recyclerView, getString(R.string.has_new_alarm_do_you_refresh), getString(R.string.action_refresh), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refresh();
                }
            }, false);
        }
    }

    @Override
    protected int findView() {
        return R.layout.fragment_alarm_list;
    }

    @Override
    protected void initView() {
        recyclerView.addOnItemTouchListener(new ItemClickListener(recyclerView,
                new ItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                    }
                }));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new CommonRecyclerViewAdapter<String>(mContext, R.layout.item_home_list) {
            @Override
            public void convert(BaseRecyclerViewHolder helper, String item, int position) {
                TextView tv = helper.getView(R.id.tv_device_name);
                tv.setText(item);
                SlantedTextView slantedTextView = helper.getView(R.id.tv_position);
                slantedTextView.setText(position + 1 + "");
            }
        };
        ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(mAdapter);
        alphaAdapter.setInterpolator(new OvershootInterpolator(0.5f));
        alphaAdapter.setDuration(1000);
        alphaAdapter.setFirstOnly(false);
        adapter = new RecyclerAdapterWithHF(alphaAdapter);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        initRefreshLayout();
        refresh();
    }

    private void initRefreshLayout() {
        refreshLayout.setAutoLoadMoreEnable(true);
        refreshLayout.setPtrHandler(new PtrDefaultHandler() {
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

    public void refresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (refreshLayout != null)
                    refreshLayout.autoRefresh(true);
            }
        }, 100);
    }

    private void setData(int type) {
        List<String> list = new ArrayList<>();
        switch (type) {
            case 0:
                for (int i = 0; i < size; i++) {
                    list.add("新告警" + (i + 1));
                }
                mAdapter.fillList(list);
                if (refreshLayout != null) {
                    refreshLayout.refreshComplete();
                    refreshLayout.setLoadMoreEnable(true);
                }
                break;
            case 1:
                for (int i = (page - 1) * size; i < page * size; i++) {
                    list.add("新告警" + (i + 1));
                }
                mAdapter.appendList(list);
                if (refreshLayout != null) {
                    refreshLayout.loadMoreComplete(true);
                }
                break;
        }
    }

}
