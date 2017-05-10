package com.cc.ui.search;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import com.cc.app.R;
import com.cc.ui.base.BaseMediaFragment;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Author: NT
 * Since: 11/27/2016.
 */
public abstract class BaseFragmentSearch extends BaseMediaFragment implements ISearchOnlineView {
    @BindView(R.id.rc_list_music)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.view_search_more)
    protected View viewSearchMore;

    @BindView(R.id.view_not_found)
    protected View viewSearchNotFound;

    @BindView(R.id.tv_search_more)
    protected TextView tvSearchMore;
    protected String searchText;


    @Inject
    SearOnlinePresenter mPresenter;

    protected void showSearchMore(String textSearch) {
        searchText = textSearch;
        String htmlStr = "Tìm thêm kết quả cho <b>\"" + textSearch + "\"<b/>";
        Spanned spHtmls;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            spHtmls = Html.fromHtml(htmlStr, Html.FROM_HTML_MODE_LEGACY);
        } else {
            spHtmls = Html.fromHtml(htmlStr);
        }

        tvSearchMore.setText(spHtmls);

        viewSearchMore.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (BaseFragmentSearch.this instanceof SearchSongFragment)
                    viewSearchMore.setVisibility(View.VISIBLE);
                else
                    viewSearchNotFound.setVisibility(View.VISIBLE);
            }
        }, 200);

    }

    protected void hideSearchMore() {
        viewSearchNotFound.setVisibility(View.GONE);
        viewSearchMore.setVisibility(View.GONE);
    }


    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_search_song;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.destroyView();
    }


    @Override
    public void songSearchNotFound() {
        viewSearchNotFound.setVisibility(View.VISIBLE);
    }

    @Override
    public void songSearchError() {
        viewSearchNotFound.setVisibility(View.VISIBLE);
    }
}