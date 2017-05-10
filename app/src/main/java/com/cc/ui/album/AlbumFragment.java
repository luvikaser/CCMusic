package com.cc.ui.album;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cc.app.R;
import com.cc.data.MusicEnumApp;
import com.cc.domain.utils.LogHelper;
import com.cc.ui.base.BaseMediaActivityListener;
import com.cc.ui.base.BaseMediaFragment;
import com.cc.utils.HidingScrollListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;

/**
 * Author: NT
 * Since: 10/31/2016.
 */
public class AlbumFragment extends BaseMediaFragment implements SearchView.OnQueryTextListener {
    @BindView(R.id.rc_grid_album)
    RecyclerView mRecyclerView;

    private AlbumsAdapter mAlbumsAdapter;
    private List<MediaBrowserCompat.MediaItem> mMediaItemList;
    private BaseMediaActivityListener mListener;

    private void initRecyclerGridView() {
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(layoutManager);

        if (mRecyclerView != null)
            mRecyclerView.addOnScrollListener(new HidingScrollListener() {
                @Override
                public void onHide() {
                    hideViewsPlaybackBottom();
                }

                @Override
                public void onShow() {
                    showViewsPlaybackBottom();
                }
            });
    }


    @Override
    protected void setupFragmentComponent() {

    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_album;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListener = (AlbumActivity)getActivity();
        mMediaItemList = new ArrayList<>();
        mAlbumsAdapter = new AlbumsAdapter(getActivity(), ALPHABETICAL_COMPARATOR);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerGridView();

        mRecyclerView.setAdapter(mAlbumsAdapter);
        mAlbumsAdapter.edit().add(mMediaItemList).commit();



    }


    @Override
    protected void onMediaBrowserChildrenLoaded(@NonNull String parentId, List<MediaBrowserCompat.MediaItem> children) {
        try {
            Log.d(TAG, "fragment onChildrenLoaded, parentId=" + parentId +
                    "  count=" + children.size());
            mListener.setTitle(getString(R.string.albums));
            mListener.setCount(children.size());
            mMediaItemList.clear();
            mMediaItemList = children;
            mAlbumsAdapter.edit().removeAll();
            mAlbumsAdapter.edit().replaceAll(mMediaItemList).commit();
            onQueryTextChange("");
        } catch (Throwable t) {
            LogHelper.e(TAG, "Error on childrenloaded", t);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final List<MediaBrowserCompat.MediaItem> filteredModelList = filter(mMediaItemList, query);
        mAlbumsAdapter.edit()
                .replaceAll(filteredModelList)
                .commit();
        mRecyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    protected void permissionGranted(int permissionRequestCode) {

    }
}