package com.cc.ui.karaoke.ui.activity.player;

import com.cc.ui.karaoke.ui.activity.base.VMBaseFragmentHostActivity;
import com.cc.ui.karaoke.ui.fragment.base.VMBaseFragment;
import com.cc.ui.karaoke.ui.fragment.player.ListVideoKaraokeYoutubeFragment;

/**
 * Author  : duyng
 * since   : 8/11/2016
 */
public class ListVideoKaraokeYoutubeActivity extends VMBaseFragmentHostActivity {
    @Override
    public VMBaseFragment getFragmentToHost() {
        return ListVideoKaraokeYoutubeFragment.newInstance(getIntent().getExtras());
    }


}
