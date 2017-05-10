package com.cc.ui.artist;

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
import com.cc.ui.album.AlbumsAdapter;
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
public class ArtistFragment extends BaseMediaFragment implements SearchView.OnQueryTextListener {
    public static final String BUNDLE_ALBUM_NAME = "BUNDLE_ARTIST_NAME";

    @BindView(R.id.rc_grid_artist)
    RecyclerView mRecyclerView;

    private ArtistAdapter mArtistAdapter;
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
        return R.layout.fragment_artist;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListener = (ArtistActivity)getActivity();
        mMediaItemList = new ArrayList<>();
        mArtistAdapter = new ArtistAdapter(getActivity(), ALPHABETICAL_COMPARATOR);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerGridView();

        mRecyclerView.setAdapter(mArtistAdapter);
        mArtistAdapter.edit().add(mMediaItemList).commit();

    }


    @Override
    protected void onMediaBrowserChildrenLoaded(@NonNull String parentId, List<MediaBrowserCompat.MediaItem> children) {
        try {
            Log.d(TAG, "fragment onChildrenLoaded, parentId=" + parentId +
                    "  count=" + children.size());
            mListener.setTitle(getString(R.string.artist));
            mListener.setCount(children.size());
            mMediaItemList.clear();
            mMediaItemList = children;
            mArtistAdapter.edit().removeAll();
            mArtistAdapter.edit().replaceAll(mMediaItemList).commit();
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
        mArtistAdapter.edit()
                .replaceAll(filteredModelList)
                .commit();
        mRecyclerView.scrollToPosition(0);
        return true;
    }



    @Override
    protected void permissionGranted(int permissionRequestCode) {
        // not used
    }
}