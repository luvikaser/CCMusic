package com.cc.ui.karaoke.ui.activity.record;

import com.cc.ui.karaoke.ui.activity.base.VMBaseFragmentHostActivity;
import com.cc.ui.karaoke.ui.fragment.base.VMBaseFragment;
import com.cc.ui.karaoke.ui.fragment.record.VMFragmentRecordList;

/**
 * Author: NT
 * Since: 9/8/2016.
 */
public class VMRecordListActivity extends VMBaseFragmentHostActivity {
    @Override
    public VMBaseFragment getFragmentToHost() {
        return VMFragmentRecordList.newInstance();
    }
}