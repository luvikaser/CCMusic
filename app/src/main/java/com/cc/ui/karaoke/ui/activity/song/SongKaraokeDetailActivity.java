package com.cc.ui.karaoke.ui.activity.song;

import android.content.Context;
import android.content.Intent;

import com.cc.ui.karaoke.data.model.karaoke.VMSongKaraoke;
import com.cc.ui.karaoke.ui.activity.base.VMBaseFragmentHostActivity;
import com.cc.ui.karaoke.ui.fragment.base.VMBaseFragment;
import com.cc.ui.karaoke.ui.fragment.song.karaoke.detail.VMSongKaraokeDetailFragment;

/**
 * Project: Minion
 * Author: NT
 * Since: 6/15/2016.
 * Email: duynguyen.developer@yahoo.com
 */
public class SongKaraokeDetailActivity extends VMBaseFragmentHostActivity {
    public static Intent getIntent(Context context, VMSongKaraoke mVMSongKaraoke) {
        Intent intent = new Intent(context, SongKaraokeDetailActivity.class);
        intent.putExtra(VMSongKaraokeDetailFragment.KEY_BUNDLE_ZSONG, mVMSongKaraoke);
        return intent;
    }
    @Override
    public VMBaseFragment getFragmentToHost() {
        return VMSongKaraokeDetailFragment.newInstance(getIntent().getExtras());
    }


}
