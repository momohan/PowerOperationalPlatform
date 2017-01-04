package com.handu.poweroperational.main.fragment.operaton;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnalyzeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnalyzeFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public AnalyzeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AnalyzeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AnalyzeFragment newInstance(String param1, String param2) {
        AnalyzeFragment fragment = new AnalyzeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int findView() {
        return R.layout.fragment_analyze;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

}
