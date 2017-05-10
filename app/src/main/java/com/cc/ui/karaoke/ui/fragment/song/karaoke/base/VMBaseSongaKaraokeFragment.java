package com.cc.ui.karaoke.ui.fragment.song.karaoke.base;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cc.app.R;
import com.futuremind.recyclerviewfastscroll.FastScroller;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
 import com.cc.ui.karaoke.data.database.table.karaoke.VMSongArirangTable;
import com.cc.ui.karaoke.data.model.karaoke.VMLanguageKaraoke;
import com.cc.ui.karaoke.data.model.karaoke.VMSongKaraoke;
import com.cc.ui.karaoke.data.model.karaoke.VMVolsKaraoke;
import com.cc.ui.karaoke.presenter.song.karaoke.base.VMSongKaraokePresenter;

import com.cc.ui.karaoke.ui.activity.song.SongKaraokeDetailActivity;
import com.cc.ui.karaoke.ui.adapter.karaoke.VMSongKaraokeAdapter;
import com.cc.ui.karaoke.ui.adapter.karaoke.language.VMLanguageKaraokeAdapter;
import com.cc.ui.karaoke.ui.adapter.karaoke.vols.VMVolsKaraokeAdapter;
import com.cc.ui.karaoke.ui.fragment.base.VMBaseFragment;
import com.cc.ui.karaoke.ui.widget.DividerDecoration;
import com.cc.ui.karaoke.utils.DebugLog;
import com.cc.ui.karaoke.utils.FontUtils;
import com.cc.ui.karaoke.utils.StringUtils;
import com.cc.ui.karaoke.utils.SystemUtil;


/**
 * Project: Minion
 * Author: NT
 * Since: 6/18/2016.
 * Email: duynguyen.developer@yahoo.com
 */
public abstract class VMBaseSongaKaraokeFragment extends VMBaseFragment implements
        VMSongKaraokeAdapter.OnClickItemSongListener {

    // adapter used to common
    protected VMSongKaraokeAdapter mAdapter;
    protected VMSongKaraokePresenter mPresenter;
    protected int idVols;
    protected String volCurrent = "-1";
    protected String languageCurrent = "vn";

    // init view
    @BindView(R.id.recycler_view)
    protected RecyclerView recyclerView;

    @BindView(R.id.fastscroll)
    protected FastScroller fastScroller;

    @BindView(R.id.tv_font_icon_sort_language)
    protected TextView tvFontIconSortLanguage;

    @BindView(R.id.tv_font_icon_sort_vols)
    protected TextView tvFontIconSortVols;

    @BindView(R.id.tv_filter_vols)
    protected TextView tvFilterVols;

    @BindView(R.id.tv_filter_language)
    protected TextView tvFilterLanguage;

    @BindView(R.id.view_filter_language)
    protected View viewFilterLanguage;

    @BindView(R.id.view_filter_vols)
    protected View viewFilterVols;

    @BindView(R.id.tv_loading)
    TextView tvLoading;


    protected abstract void onCreateInitPresenter();


    private void initRecyclerView(View v) {
        // Set layout manager
        setRecyclerViewLayoutManager(recyclerView);
    }

    protected int getLayoutManagerOrientation(int activityOrientation) {
        if (activityOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            return LinearLayoutManager.VERTICAL;
        } else {
            return LinearLayoutManager.HORIZONTAL;
        }
    }

    /**
     * Set RecyclerView's LayoutManager
     */
    public void setRecyclerViewLayoutManager(RecyclerView recyclerView) {
        recyclerView.setNestedScrollingEnabled(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerDecoration(getActivity()));

    }

    /**
     * set adapter for list song karaoke
     *
     * @param cursor used to get data
     */
    protected void setAdapter(Cursor cursor) {
        if (cursor != null) {
            idVols = cursor.getInt(cursor.getColumnIndex(VMSongArirangTable
                    .SMANUFACTURE_COLUMN));
        }
        mAdapter = new VMSongKaraokeAdapter(getActivity(), R.layout.row_song_karaoke,
                cursor, columns, this);
        recyclerView.setAdapter(mAdapter);
        fastScroller.setRecyclerView(recyclerView);
    }

    /**
     * change text vols after selected
     *
     * @param text used to get vol show ui
     */
    protected void setTextVolsFilter(String text) {
        tvFilterVols.setText(text);
    }

    /**
     * change text language after selected
     *
     * @param text used to get language
     */
    protected void setTextLanguageFilter(String text) {
        tvFilterLanguage.setText(text);
    }

    /**
     * hide view select vols when language  = english
     *
     * @param isShow used to get flag show or hide
     */
    protected void showVolFilter(boolean isShow) {
        if (isShow) {
            viewFilterVols.setVisibility(View.VISIBLE);
        } else {
            viewFilterVols.setVisibility(View.GONE);
        }
    }

    /**
     * show popup select language
     */
    protected void showListLanguage() {
        List<String> list = Arrays.asList(getResources().getStringArray(R.array.language_karaoke_items));
        final ArrayList<VMLanguageKaraoke> languageKaraokes = new ArrayList<>();
        VMLanguageKaraoke languageKaraokeVN = new VMLanguageKaraoke();
        languageKaraokeVN.setName(list.get(0));
        languageKaraokeVN.setId("vn");
        languageKaraokes.add(languageKaraokeVN);

        VMLanguageKaraoke languageKaraokeForeign = new VMLanguageKaraoke();
        languageKaraokeForeign.setName(list.get(1));
        languageKaraokeForeign.setId("en");
        languageKaraokes.add(languageKaraokeForeign);


        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_listview_title);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.tv_title);
        ListView listView = (ListView) dialog.findViewById(R.id.list_item_dialog);

        tvTitle.setText(R.string.title_language_karaoke);
        listView.setAdapter(new VMLanguageKaraokeAdapter(getContext(), languageKaraokes));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setTextLanguageFilter(languageKaraokes.get(position).getName());
                languageCurrent = languageKaraokes.get(position).getId();
                if (languageCurrent.compareTo("en") == 0) {
                    showVolFilter(false);
                    if (idVols == 4) {
                        mPresenter.getSongWithVols(volCurrent, languageCurrent);
                    } else {
                        mPresenter.getSongWithVols("0", languageCurrent);
                    }

                } else {
                    showVolFilter(true);
                    if (volCurrent.compareTo("-1") == 0) {
                        mPresenter.getAllSong();
                    } else {
                        mPresenter.getSongWithVols(volCurrent, languageCurrent);
                    }

                }
//                                mPresenter.getSongWithVols(String.valueOf(lisDataVols.get(which).getId()));
                dialog.dismiss();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * show popup list vol
     *
     * @param lisDataVols used to get list vols
     */
    protected void showListVols(final ArrayList<VMVolsKaraoke> lisDataVols) {
        DebugLog.e(TAG, SystemClock.currentThreadTimeMillis() + "");
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_listview_title);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.tv_title);
        ListView listView = (ListView) dialog.findViewById(R.id.list_item_dialog);

        tvTitle.setText(R.string.title_karaoke_arirang);

        lisDataVols.add(new VMVolsKaraoke(-1, getString(R.string.title_all)));
        listView.setAdapter(new VMVolsKaraokeAdapter(getActivity(), lisDataVols));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "Clicked item " +
                        lisDataVols.get(position).getId(), Toast
                        .LENGTH_SHORT).show();
                setTextVolsFilter(lisDataVols.get(position).getName());
                volCurrent = String.valueOf(lisDataVols.get(position).getId());
                if (volCurrent.compareTo("-1") == 0 && languageCurrent.compareTo("vn") == 0) {
                    mPresenter.getAllSong();
                } else {
                    mPresenter.getSongWithVols(String.valueOf
                            (lisDataVols.get(position).getId()), languageCurrent);
                }

                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @OnClick(R.id.view_filter_language)
    void onClickViewFilterLanguage(View v) {
        showListLanguage();
    }

    @OnClick(R.id.view_filter_vols)
    void onClickViewFilterVols(View v) {
        mPresenter.getListVols(idVols);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        onCreateInitPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_karaoke, container, false);
        return view;
    }

    @Override
    public synchronized void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView(view);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPresenter.getAllSong();
            }
        }, 200);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

/*
    @Override
    public void onSearchOpened() {
        Log.d(TAG, "onSearchOpened");
    }

    @Override
    public void onSearchCleared() {
        Log.d(TAG, "onSearchCleared");
        mPresenter.getAllSong();
    }

    @Override
    public void onSearchClosed() {
        mPresenter.getAllSong();
        mSearchBox.clearSearchable();
        Log.d(TAG, "onSearchClosed");
    }*/



    protected void hideTextLoading() {
        tvLoading.setVisibility(View.GONE);
    }

    @Override
    public void onLikeSong(VMSongKaraoke vmSongKaraoke) {
        mPresenter.addSongToFavorite(vmSongKaraoke);
    }

    @Override
    public void onUnLikeSong(long idSong) {
        mPresenter.removeSongToFavorite(idSong);
    }

    @Override
    public void onViewDetailSong(VMSongKaraoke vmSongKaraoke) {
        SystemUtil.startActivity(getActivity(), SongKaraokeDetailActivity.getIntent
                (getActivity(), vmSongKaraoke), false);
    }
}
