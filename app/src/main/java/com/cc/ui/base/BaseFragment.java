package com.cc.ui.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cc.MusicApplication;
import com.cc.app.R;
import com.cc.di.components.ApplicationComponent;
import com.cc.di.components.UserComponent;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author: NT
 * Since: 27/9/2016.
 */

public abstract class BaseFragment extends Fragment {

    public synchronized static <F> BaseFragment newInstance(F fragment, Bundle b) {
        BaseFragment fragmentInstance = (BaseFragment) fragment;
        fragmentInstance.setArguments(b);
        return fragmentInstance;
    }

    protected abstract void setupFragmentComponent();

    protected abstract int getResLayoutId();

    public final String TAG = getClass().getSimpleName();

    private Snackbar mSnackBar;
    private ProgressDialog mProgressDialog;
    private Unbinder unbinder;
/*
    protected final Navigator navigator = AndroidApplication.instance().getAppComponent().navigator();
    protected final UserConfig userConfig = AndroidApplication.instance().getAppComponent().userConfig();*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getResLayoutId(), container, false);
        unbinder = ButterKnife.bind(this, view);
        setupFragmentComponent();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideLoading();
        unbinder.unbind();
    }

    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
     }

    public void showSnackbar(int message, View.OnClickListener listener) {
        showSnackbar(getActivity().findViewById(android.R.id.content), message, listener);
    }

    public void showSnackbar(View view, int message, View.OnClickListener listener) {
        hideSnackbar();
        mSnackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        if (listener != null) {
            mSnackBar.setAction(R.string.retry, listener);
        }
        mSnackBar.show();
    }

    public void showNetworkError() {
        showSnackbar(R.string.exception_no_connection, null);
    }

    public void hideSnackbar() {
        if (mSnackBar != null) mSnackBar.dismiss();
    }

    public void showLoading() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    public void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    public void showError(String message) {
        showToast(message);
    }

    public void showMessage(String message) {
        showToast(message);
    }

    public ApplicationComponent getAppComponent() {
        return MusicApplication.getInstance().getAppComponent();
    }

    public UserComponent getUserComponent() {
        return MusicApplication.getInstance().getUserComponent();
    }

    protected void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(int message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }


    public void addNewFragment(BaseFragment fragment, int containerViewId) {
        if (fragment != null && getFragmentManager().findFragmentById(fragment.getId()) == null) {
            FragmentTransaction fragmentTransaction
                    = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(containerViewId, fragment, fragment.TAG);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }
}
