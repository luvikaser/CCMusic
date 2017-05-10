package com.cc.ui.karaoke.ui.fragment.song.karaoke;

import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.FastScroller;

import butterknife.BindView;
import mmobile.com.karaoke.R;
import com.cc.ui.karaoke.presenter.song.karaoke.VMSongFavoritePresenter;
import com.cc.ui.karaoke.presenter.song.karaoke.VMSongFavoritePresenterImp;
import com.cc.ui.karaoke.ui.adapter.karaoke.VMSongKaraokeAdapter;
import com.cc.ui.karaoke.ui.fragment.base.VMBaseFragment;
import com.cc.ui.karaoke.ui.widget.DividerDecoration;


/**
 * Project: Minion
 * Author: NT
 * Since: 6/18/2016.
 * Email: duynguyen.developer@yahoo.com
 */
public class VMSongFarvoriteFragment extends VMBaseFragment implements
        VMSongFarvoriteView {

    public static VMSongFarvoriteFragment newInstance(Bundle b) {
        VMSongFarvoriteFragment f = new VMSongFarvoriteFragment();
        f.setArguments(b);
        return f;
    }

    // adapter used to common
    protected VMSongKaraokeAdapter mAdapter;
    protected VMSongFavoritePresenter mPresenter;

    // init view
    @BindView(R.id.recycler_view)
    protected RecyclerView recyclerView;

    @BindView(R.id.fastscroll)
    protected FastScroller fastScroller;

    @BindView(R.id.tv_show_text)
    TextView tvShowText;

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
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition =
                    ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
        fastScroller.setRecyclerView(recyclerView);
    }

    @Override
    public void registerReceiverChangeNetwork() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new VMSongFavoritePresenterImp();
        mPresenter.setView(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_favorite, container, false);
        return view;
    }

    @Override
    public synchronized void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView(view);
        mPresenter.getAllSong();
    }

    @Override
    public void setData(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0)
            tvShowText.setText(R.string.no_song_favorite);
        mAdapter = new VMSongKaraokeAdapter(getActivity(), R.layout.row_song_karaoke,
                cursor, columns);
        recyclerView.setAdapter(mAdapter);

        int orientation = getLayoutManagerOrientation(getResources().getConfiguration().orientation);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), orientation, false);
        recyclerView.setLayoutManager(layoutManager);

        // Add the sticky headers decoration
/*        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        recyclerView.addItemDecoration(headersDecor);*/

        // Add decoration for dividers between list items
        recyclerView.addItemDecoration(new DividerDecoration(getActivity()));

        // Add touch listeners
      /*  StickyRecyclerHeadersTouchListener touchListener =
                new StickyRecyclerHeadersTouchListener(recyclerView, headersDecor);
        touchListener.setOnHeaderClickListener(
                new StickyRecyclerHeadersTouchListener.OnHeaderClickListener() {
                    @Override
                    public void onHeaderClick(View header, int position, long headerId) {

                    }
                });
        recyclerView.addOnItemTouchListener(touchListener);*/


    }
}
