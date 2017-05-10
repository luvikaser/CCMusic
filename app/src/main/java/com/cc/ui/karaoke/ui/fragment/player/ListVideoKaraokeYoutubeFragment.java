package com.cc.ui.karaoke.ui.fragment.player;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cc.app.R;
import com.cc.ui.karaoke.app.BaseConstants;
import com.cc.ui.karaoke.data.model.VideoItem;
import com.cc.ui.karaoke.data.model.karaoke.VMSongKaraoke;
import com.cc.ui.karaoke.receiver.notify.VMObservableObject;
import com.cc.ui.karaoke.ui.activity.player.PlayerYoutubeActivity;
import com.cc.ui.karaoke.ui.fragment.base.VMBaseFragment;
import com.cc.ui.karaoke.ui.fragment.song.karaoke.detail.VMSongKaraokeDetailFragment;
import com.cc.ui.karaoke.utils.DebugLog;
import com.cc.ui.karaoke.utils.SystemUtil;
import com.cc.utils.DialogUtil;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;

/**
 * Author  : duyng
 * since   : 8/9/2016
 */
public class ListVideoKaraokeYoutubeFragment extends VMBaseFragment implements Observer {
    private ArrayAdapter<VideoItem> adapter;
    private static final int RECORD_AUDIO_PERMISSION = 108;
    private static final int OVERLAY_PERMISSION = 201;
    private int positionItemCurrent = -1;

    public static ListVideoKaraokeYoutubeFragment newInstance(Bundle b) {
        ListVideoKaraokeYoutubeFragment f = new ListVideoKaraokeYoutubeFragment();
        f.setArguments(b);
        return f;
    }

    private List<VideoItem> searchResults;
    private VMSongKaraoke mVMSongKaraoke;

    @BindView(R.id.list_view_video)
    ListView lvVideo;

    @BindView(R.id.progressBar1)
    View viewProgressbar;

    private void setVisibleProgressbar(int visible) {
        viewProgressbar.setVisibility(visible);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_karaoke_video_youtube, container, false);
    }

    @Override
    public void registerReceiverChangeNetwork() {
        VMObservableObject.getInstance().addObserver(this);
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set data bundle
        Bundle bundle = getArguments();
        mVMSongKaraoke = (VMSongKaraoke) bundle.getSerializable(VMSongKaraokeDetailFragment.KEY_BUNDLE_ZSONG);
    }

    @Override
    public synchronized void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (SystemUtil.isNetworkAvailable(getActivity()))
            loadVideoSearch();
        else {
            showDialogErrorNetwork();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiverChangeNetwork();
    }

    @Override
    public void onPause() {
        super.onPause();
        VMObservableObject.getInstance().deleteObserver(this);
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

    // search video
    private void loadVideoSearch() {
        //search data video
        final YoutubeConnector youtubeConnector = new YoutubeConnector(getActivity());
        new Thread() {
            public void run() {
                searchResults = youtubeConnector.search(mVMSongKaraoke.getsName() + " karaoke");
                updateVideosFound();
            }
        }.start();

        lvVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DebugLog.d(TAG, "onItemClick");
                positionItemCurrent = position;
                requestRecordAudioPermission(position);
            }
        });
    }

    private void requestRecordAudioPermission(int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission
                    .RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission
                        .RECORD_AUDIO, Manifest.permission
                        .RECORD_AUDIO}, RECORD_AUDIO_PERMISSION);
            } else {
                openRecordAudioActivity(position);
            }
        } else {
            openRecordAudioActivity(position);
        }
    }

    private void openRecordAudioActivity(int position) {
        Intent intent = new Intent(getActivity(), PlayerYoutubeActivity.class);
        intent.putExtra(VMSongKaraokeDetailFragment.KEY_BUNDLE_VIDEO_YOUTUB, searchResults.get(position));
        SystemUtil.startActivity(getActivity(), intent, false);
    }

    private void updateVideosFound() {
        if (getActivity() == null) {
            DebugLog.e(TAG, "error: getActivity() == null");
            return;
        }
        adapter = new ArrayAdapter<VideoItem>(getActivity(), R.layout.row_video_youtube, searchResults) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.row_video_youtube, parent, false);
                }
                ImageView thumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
                TextView title = (TextView) convertView.findViewById(R.id.video_title);
                TextView description = (TextView) convertView.findViewById(R.id.video_description);

                VideoItem searchResult = searchResults.get(position);

                Picasso.with(getActivity()).load(searchResult.getThumbnailURL()).into(thumbnail);
                title.setText(searchResult.getTitle());
                description.setText(searchResult.getDescription());
                return convertView;
            }
        };

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (SystemUtil.isNetworkAvailable(getActivity()))
                    if (lvVideo == null || adapter.isEmpty()) return;
                lvVideo.setAdapter(adapter);
                setVisibleProgressbar(View.GONE);
            }
        });
    }

    @Override
    public void update(Observable observable, Object data) {

        if ((boolean) data) {
            loadVideoSearch();
        }
    }

    public class YoutubeConnector {
        private YouTube youtube;
        private YouTube.Search.List query;

        /**
         * Define a global instance of the HTTP transport.
         */
        public final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

        /**
         * Define a global instance of the JSON factory.
         */
        public final JsonFactory JSON_FACTORY = new JacksonFactory();

        public YoutubeConnector(Context context) {
            youtube = new YouTube.Builder(new NetHttpTransport(),
                    new JacksonFactory(), new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest hr) throws IOException {
                }
            }).setApplicationName(getString(R.string.app_name)).build();


        /*    youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName(getString(R.string.app_name)).build();*/

            try {
                query = youtube.search().list("id,snippet");
                query.setKey(BaseConstants.YOUTUBE_API_KEY);
                query.setType("video");
                query.setFields("items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url)");
            } catch (IOException e) {
                Log.d("YC", "Could not initialize: " + e);
            }
        }

        public List<VideoItem> search(String keywords) {
            query.setQ(keywords);
            try {
                SearchListResponse response = query.execute();
                List<SearchResult> results = response.getItems();

                List<VideoItem> items = new ArrayList<>();
                for (SearchResult result : results) {
                    VideoItem item = new VideoItem();
                    item.setTitleSong(mVMSongKaraoke.getsName());
                    item.setTitle(result.getSnippet().getTitle());
                    item.setDescription(result.getSnippet().getDescription());
                    item.setThumbnailURL(result.getSnippet().getThumbnails().getDefault().getUrl());
                    item.setId(result.getId().getVideoId());
                    items.add(item);
                    DebugLog.e(TAG, "result.getId().getVideoId() =  " + result.getId().getVideoId());
                }
                return items;
            } catch (IOException e) {
                Log.d("YC", "Could not search: " + e);
                return null;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_AUDIO_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestRecordAudioPermission(positionItemCurrent);
            }
        }
    }
}
