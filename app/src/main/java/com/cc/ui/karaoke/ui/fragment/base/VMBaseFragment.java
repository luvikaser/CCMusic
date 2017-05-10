package com.cc.ui.karaoke.ui.fragment.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;
import com.cc.ui.karaoke.data.database.table.karaoke.VMSongArirangTable;
 import com.cc.ui.karaoke.utils.DebugLog;


/**
 * Author: NT
 * Email: duynguyen.developer@yahoo.com
 */
public abstract class VMBaseFragment extends Fragment implements VMIBaseFragment {

    public final String TAG = this.getClass().getSimpleName();

    public boolean onBackPressed(@NonNull Boolean isBackButtonDisplayed) {
        return false;
    }

    public abstract void registerReceiverChangeNetwork();

    public String getTagFromClassName() {
        return TAG;
    }

    //the desired columns to be bound
    protected String[] columns = new String[]{
            VMSongArirangTable.ROWID_COLUMN,
            VMSongArirangTable.SNAME_COLUMN,
            VMSongArirangTable.SLYRIC_COLUMN
    };


    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DebugLog.d(TAG, "onCreate" + savedInstanceState);
        setHasOptionsMenu(true);
        registerReceiverChangeNetwork();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DebugLog.d(TAG, "onCreateView" + savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public synchronized void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DebugLog.d(TAG, "onViewCreated");
        ButterKnife.bind(this, view);
    }

    @Override
    public void onStop() {
        super.onStop();
     }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
     }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
     }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void hideProgressDialog() {

    }

    @Override
    public void showNetworkError() {

    }

    @Override
    public void showGeneralErrorDialog() {

    }

    @Override
    public void showErrorDialog(String message, String statusDesc) {

    }

    @Override
    public void showDialog(String title, String description) {

    }

}
