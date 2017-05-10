package com.cc.ui.karaoke.ui.fragment.song.karaoke.detail;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.OnClick;

import com.cc.ui.karaoke.data.database.helper.karaoke.VMSongFavoriteScope;
import com.cc.ui.karaoke.data.database.helperImp.karaoke.VMSongFavoriteScopeImp;
import com.cc.ui.karaoke.data.model.karaoke.VMSongKaraoke;
import com.cc.ui.karaoke.data.model.services.response.VMSong;
import com.cc.ui.karaoke.data.model.services.response.lyric.VMLyricByIdResponse;
import com.cc.ui.karaoke.data.model.services.response.song.VMSearchSongResponse;
import com.cc.ui.karaoke.data.model.services.response.stream.VMMp3StreamResponse;

import com.cc.app.R;
import com.cc.ui.karaoke.presenter.song.karaoke.detail.VMSongKaraokeDetailPresenter;
import com.cc.ui.karaoke.presenter.song.karaoke.detail.VMSongKaraokeDetailPresenterImp;
import com.cc.ui.karaoke.receiver.notify.VMObservableObject;
import com.cc.ui.karaoke.ui.activity.player.ListVideoKaraokeYoutubeActivity;
 import com.cc.ui.karaoke.ui.adapter.karaoke.detail.VMSongLyricAdapter;
import com.cc.ui.karaoke.ui.fragment.base.VMBaseFragment;
import com.cc.ui.karaoke.ui.widget.DividerDecoration;
import com.cc.ui.karaoke.ui.widget.VMUnderlineTextView;
import com.cc.ui.karaoke.utils.SystemUtil;
import com.cc.utils.DialogUtil;

/**
 * Project: Minion
 * Author: NT
 * Since: 6/15/2016.
 * Email: duynguyen.developer@yahoo.com
 */
public class VMSongKaraokeDetailFragment extends VMBaseFragment implements
        VMSongKaraokeDetailView, VMSongLyricAdapter.OnClickItemView, Observer {
    public static final String KEY_BUNDLE_ZSONG = "bundle_zsong";
    public static final String KEY_BUNDLE_VIDEO_YOUTUB = "bundle_video_youtube";
    private ProgressDialog progress;

    public static VMSongKaraokeDetailFragment newInstance(Bundle b) {
        VMSongKaraokeDetailFragment f = new VMSongKaraokeDetailFragment();
        b.putSerializable(KEY_BUNDLE_ZSONG, b.getSerializable(KEY_BUNDLE_ZSONG));
        f.setArguments(b);
        return f;
    }

    private VMSongKaraoke mVMSongKaraoke;
    private VMSongLyricAdapter mAdapter;
    private VMSongKaraokeDetailPresenter presenter;
    private VMSongFavoriteScope mVmSongFavoriteScope;
    private VMSong mSongCurrent;

    @BindView(R.id.tv_code_song)
    TextView tvCodeSong;

    @BindView(R.id.tv_title)
    TextView tvTitleSong;

    @BindView(R.id.tv_lyric_content)
    TextView tvLyricSong;

    @BindView(R.id.tv_author_content)
    TextView tvAuthorSong;

    @BindView(R.id.tv_machine_karaoke_content)
    TextView tvMachineKaraoke;

    @BindView(R.id.tv_search_quickly_content)
    TextView tvSearchQuickly;

    @BindView(R.id.img_favorite)
    ImageView imgFavorite;

    @BindView(R.id.tv_view_more)
    VMUnderlineTextView tvViewMore;

    @BindView(R.id.rcv_song_lyric)
    RecyclerView recyclerView;

    @BindView(R.id.tv_empty)
    TextView tvEmpty;

    @BindView(R.id.bt_player)
    Button btnPlayer;

    @BindView(R.id.progressBar1)
    View viewProgressbar;

    @OnClick(R.id.img_favorite)
    public void OnClickFavorite(View v) {
        if (mVmSongFavoriteScope.isAddedFavorite(mVMSongKaraoke.getPk())) {
            imgFavorite.setImageResource(R.drawable.ic_favorite_grey_600_24dp);
            presenter.removeSongToFavorite(mVMSongKaraoke.getPk());
        } else {
            imgFavorite.setImageResource(R.drawable.ic_favorite_red_600_24dp);
            presenter.addSongToFavorite(mVMSongKaraoke);
        }
    }

    @OnClick(R.id.bt_player)
    public void OnClickPlayer(View v) {
        Intent intent = new Intent(getActivity(), ListVideoKaraokeYoutubeActivity.class);
        intent.putExtra(KEY_BUNDLE_ZSONG, mVMSongKaraoke);
        SystemUtil.startActivity(getActivity(), intent, false);
    }

    @Override
    public void registerReceiverChangeNetwork() {
        VMObservableObject.getInstance().addObserver(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        presenter = new VMSongKaraokeDetailPresenterImp();
        presenter.setView(this);
        mVMSongKaraoke = (VMSongKaraoke) bundle.getSerializable(KEY_BUNDLE_ZSONG);
        mVmSongFavoriteScope = new VMSongFavoriteScopeImp();


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_karaoke_detail, container,
                false);
        return view;
    }

    @Override
    public synchronized void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.detail_label));
        setUpData();
        initRecyclerView();
        loadListLyric();
    }

    private void loadListLyric() {
        if (SystemUtil.isNetworkAvailable(getActivity()))
            presenter.getListSongSearch(mVMSongKaraoke.getsNameClean());
        else {
            showDialogErrorNetwork();
        }
    }


    private void showDialogErrorNetwork() {
        DialogUtil.showDialogExit(getActivity(), getString(R.string.error), getString(R.string.error_network), new DialogUtil
                .OnClickDialogListener() {
            @Override
            public void onClickPositive() {

            }

            @Override
            public void onClickNegative() {

            }
        });
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerDecoration(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * set data show view of song
     */
    private void setUpData() {
        String typeKaraoke = "Arirang - Vol ";
        tvCodeSong.setText(String.valueOf(mVMSongKaraoke.getRowid()));
        tvTitleSong.setText(mVMSongKaraoke.getsName());
        tvLyricSong.setText(mVMSongKaraoke.getsLyric());
        tvAuthorSong.setText(mVMSongKaraoke.getsMeta());
        if (mVmSongFavoriteScope.isAddedFavorite(mVMSongKaraoke.getPk())) {
            imgFavorite.setImageResource(R.drawable.ic_favorite_red_600_24dp);
        }

        switch (Integer.parseInt(mVMSongKaraoke.getsManufacture())) {
            case 1:
                typeKaraoke = "ARIRANG  - Vol ";
                break;

            case 2:
                typeKaraoke = "CALIFORNIAL - Vol ";
                break;

            case 3:
                typeKaraoke = "MUSIC CORE - Vol ";
                break;

            case 4:
                typeKaraoke = "VIET KTV - Vol ";
                break;

        }
        tvMachineKaraoke.setText(typeKaraoke + mVMSongKaraoke.getSvol());
        tvSearchQuickly.setText(mVMSongKaraoke.getSabbr());
    /*    if (Byte.parseByte(mVMSongKaraoke.getsFavorite()) == 1)
            imgFavorite.setBackgroundResource(R.drawable.ic_favorite_grey_600_24dp);*/
    }

    @Override
    public void setDataLyricSong(VMLyricByIdResponse lyricSong, final VMMp3StreamResponse mp3StreamResponse, final VMMp3StreamResponse lrcDataString) {

        final String lyric = lyricSong.getLyric();
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.dialog_show_lyric, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final TextView userInput = (TextView) promptsView
                .findViewById(R.id.tv_lyric_content);
        userInput.setText(Html.fromHtml(lyric));


        // set dialog message
        alertDialogBuilder
                .setTitle(mSongCurrent.getArtist() + " - " + mSongCurrent.getNameSong())
                .setCancelable(false)

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        if (mp3StreamResponse != null && !TextUtils.isEmpty(mp3StreamResponse.data)) {
            alertDialogBuilder.setPositiveButton("Play Now",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                          /*  DebugLog.e(TAG, "mp3StreamResponse.data = " + mp3StreamResponse.data);
                            Intent intent = new Intent(getActivity(), URLMediaPlayerActivity.class);
                            intent.putExtra(URLMediaPlayerActivity.BUNDLE_AUDIO_URL,
                                    "http://org2." + mp3StreamResponse.data);
                            intent.putExtra(URLMediaPlayerActivity.BUNDLE_LYRIC_SONG, lyric);
                            intent.putExtra(URLMediaPlayerActivity.BUNDLE_TITLE_SONG, mSongCurrent.getArtist() + " - " + mSongCurrent.getNameSong());
                            intent.putExtra(URLMediaPlayerActivity.BUNDLE_IMG_URL, "https://dl.dropboxusercontent.com/u/2763264/RSS%20MP3%20Player/img3.jpg");
                            if(lrcDataString != null && lrcDataString.getError() == 0) {
                                intent.putExtra(URLMediaPlayerActivity
                                        .BUNDLE_LRC_STRING, lrcDataString.data);
                            }
                            startActivity(intent);*/
                        }
                    });
        }


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
        progress.dismiss();
    }

    private void hideProgressbar(int view) {
        viewProgressbar.setVisibility(view);
    }

    @Override
    public void setDataListSong(VMSearchSongResponse songSearchResponse) {
        hideProgressbar(View.GONE);
        mAdapter = new VMSongLyricAdapter(getActivity(), songSearchResponse, this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void showError(String message) {

        hideProgressbar(View.GONE);
        tvEmpty.setText(R.string.empty_song);
    }


    @Override
    public void emptyData() {
        hideProgressbar(View.GONE);
        tvEmpty.setText(R.string.empty_song);
    }

    @Override
    public void showMessageGetLyric(String message) {
        if (progress != null) {
            progress.dismiss();
        }
        showToast(message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroyView();
        VMObservableObject.getInstance().deleteObserver(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiverChangeNetwork();
    }

    @Override
    public void onClickArtist(VMSong song) {
        if (!SystemUtil.isNetworkAvailable(getActivity())) {
            showDialogErrorNetwork();
            return;
        }
        mSongCurrent = song;
        String idSong = song.getIdSong();
        presenter.getLyricSong(idSong);
        progress = new ProgressDialog(getActivity());
//        progress.setCancelable(false);
        progress.setMessage("Đang tải lời bài hát..");
        progress.show();
    }

    @Override
    public void update(Observable observable, Object data) {
        if ((boolean) data) {
            if (mAdapter == null)
                presenter.getListSongSearch(mVMSongKaraoke.getsNameClean());
            else if (mAdapter.getItemCount() <= 0)
                presenter.getListSongSearch(mVMSongKaraoke.getsNameClean());
        }
    }
}
