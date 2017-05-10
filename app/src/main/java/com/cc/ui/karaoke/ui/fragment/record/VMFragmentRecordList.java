package com.cc.ui.karaoke.ui.fragment.record;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import mmobile.com.karaoke.R;
import com.cc.ui.karaoke.data.database.helper.karaoke.VMRecordFileScope;
import com.cc.ui.karaoke.data.database.helperImp.record.VMRecordFileScopeImpl;
import com.cc.ui.karaoke.ui.adapter.record.VMRecordListAdapter;
import com.cc.ui.karaoke.ui.fragment.base.VMBaseFragment;
import com.cc.ui.karaoke.ui.widget.DividerDecoration;

/**
 * Author: NT
 * Since: 9/8/2016.
 */
public class VMFragmentRecordList extends VMBaseFragment {
    public static VMFragmentRecordList newInstance() {
        VMFragmentRecordList f = new VMFragmentRecordList();
        return f;
    }

    private VMRecordFileScope mRecordFileScope;

    @BindView(R.id.list_record)
    RecyclerView mRecyclerView;

    @Override
    public void registerReceiverChangeNetwork() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record_list, container, false);
    }

    @Override
    public synchronized void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecordFileScope = new VMRecordFileScopeImpl();
        initRecyclerView();
        setData();
    }

    private void initRecyclerView() {
        mRecyclerView.setNestedScrollingEnabled(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerDecoration(getActivity()));
    }

    private void setData() {
        VMRecordListAdapter adapter = new VMRecordListAdapter(mRecordFileScope.getCursorRecordFileTable(), getActivity(), R.layout.recording);
        mRecyclerView.setAdapter(adapter);
    }
}