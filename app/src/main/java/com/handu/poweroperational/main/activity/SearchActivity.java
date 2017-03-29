package com.handu.poweroperational.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.flyco.dialog.widget.MaterialDialog;
import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseActivity;
import com.handu.poweroperational.db.dao.SearchContentDao;
import com.handu.poweroperational.db.entity.SearchContent;
import com.handu.poweroperational.db.manager.GreenDaoManager;
import com.handu.poweroperational.ui.RecyclerView.adapter.CommonRecyclerViewAdapter;
import com.handu.poweroperational.ui.RecyclerView.click.ItemClickListener;
import com.handu.poweroperational.ui.RecyclerView.divide.DividerItemDecoration;
import com.handu.poweroperational.ui.RecyclerView.holder.BaseRecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends BaseActivity {

    public static final int SEARCH_CODE = 212;
    public static final String SEARCH_CONTENT = "content";
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_search_content)
    TextInputEditText etSearchContent;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    private CommonRecyclerViewAdapter<String> mAdapter;
    private SearchContentDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        dao = GreenDaoManager.getInstance().getNewSession().getSearchContentDao();
        initView();
        initData();
    }

    @Override
    protected void initView() {
        initToolBar(toolbar, getString(R.string.title_search), true, v -> finish());
        initRecyclerView();
    }

    @Override
    protected void initData() {
        String content = getIntent().getStringExtra(SEARCH_CONTENT);
        if (!TextUtils.isEmpty(content)) {
            etSearchContent.setText(content);
        }
        queryHistorySearch();
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(manager);
        recyclerView.addOnItemTouchListener(new ItemClickListener(recyclerView, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String str = mAdapter.getItem(position);
                if (!TextUtils.isEmpty(str)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(SEARCH_CONTENT, str);
                    gotoActivitySetResult(bundle, SEARCH_CODE);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        mAdapter = new CommonRecyclerViewAdapter<String>(mContext, R.layout.item_history_search_content_list) {
            @Override
            public void convert(BaseRecyclerViewHolder helper, String item, int position) {
                TextView tv = helper.getView(R.id.tv_content);
                tv.setText(item);
            }
        };
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(mAdapter);
    }

    private void queryHistorySearch() {
        List<SearchContent> contents = dao.queryBuilder().orderDesc(SearchContentDao.Properties.Id).list();
        List<String> list = new ArrayList<>();
        for (SearchContent searchContent : contents) list.add(searchContent.content);
        mAdapter.fillList(list);
    }

    private void clear() {
        dao.deleteAll();
        mAdapter.clearAll();
    }

    private void search() {
        String content = etSearchContent.getText().toString();
        if (TextUtils.isEmpty(content)) {
            etSearchContent.setError(getString(R.string.search_content_is_null));
            etSearchContent.requestFocus();
            return;
        }
        List<SearchContent> list = dao.queryBuilder().where(SearchContentDao.Properties.Content.eq(content)).build().list();
        if (list.size() == 0) {
            SearchContent sc = new SearchContent(null, content, null);
            dao.insert(sc);
        }
        Bundle bundle = new Bundle();
        bundle.putString(SEARCH_CONTENT, content);
        gotoActivitySetResult(bundle, SEARCH_CODE);
    }

    @OnClick({R.id.tv_search, R.id.ll_delete_history_search, R.id.ibt_speech_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_search:
                search();
                break;
            case R.id.ll_delete_history_search:
                MaterialDialog dialog = new MaterialDialog(mContext);
                dialog.setTitle(R.string.action_clear_history_search);
                dialog.btnNum(2)
                        .content(getString(R.string.confirm_clear_search_content))
                        .btnText(getString(R.string.bt_cancel), getString(R.string.bt_sure))
                        .showAnim(mBasIn)
                        .dismissAnim(mBasOut)
                        .show();
                dialog.setOnBtnClickL(dialog::dismiss, () -> {
                    dialog.dismiss();
                    clear();
                });
                break;
            case R.id.ibt_speech_search:
                SpeechActivity.runSpeechActivityForResult(SearchActivity.this, false);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SpeechActivity.REQUEST_CODE && data != null) {
            String result = data.getStringExtra(SpeechActivity.REQUEST_RESULT);
            if (!TextUtils.isEmpty(result)) {
                etSearchContent.setText(result);
                List<SearchContent> list = dao.queryBuilder().where(SearchContentDao.Properties.Content.eq(result)).build().list();
                if (list.size() == 0) {
                    SearchContent sc = new SearchContent(null, result, null);
                    dao.insert(sc);
                }
                Bundle bundle = new Bundle();
                bundle.putString(SEARCH_CONTENT, result);
                gotoActivitySetResult(bundle, SEARCH_CODE);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * @param activity      对象
     * @param searchContent 搜索内容
     */
    public static void runSearchActivityForResult(Activity activity, String searchContent) {
        Intent intent = new Intent(activity, SearchActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(SEARCH_CONTENT, searchContent);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, SEARCH_CODE);
    }
}
