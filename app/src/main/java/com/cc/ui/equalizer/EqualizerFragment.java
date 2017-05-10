package com.cc.ui.equalizer;


import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Virtualizer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.cc.app.R;
import com.cc.service.MusicService;
import com.cc.ui.album.AlbumActivity;
import com.cc.ui.base.BaseFragment;
import com.cc.ui.base.BaseMediaActivityListener;
import com.cc.ui.base.BaseMediaFragment;
import com.cc.utils.VerticalSeekBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * A simple {@link Fragment} subclass.
 */
public class EqualizerFragment extends BaseMediaFragment {


    public EqualizerFragment() {
        // Required empty public constructor
    }


    @Override
    protected void setupFragmentComponent() {
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_equalizer;
    }

    @BindView(R.id.seekBar60)
    VerticalSeekBar mSeekBar60;

    @BindView(R.id.seekBar230)
    VerticalSeekBar mSeekBar230;

    @BindView(R.id.seekBar910)
    VerticalSeekBar mSeekBar910;

    @BindView(R.id.seekBar3600)
    VerticalSeekBar mSeekBar3600;

    @BindView(R.id.seekBar14000)
    VerticalSeekBar mSeekBar14000;

    @BindView(R.id.preset)
    Spinner mPreset;

    @BindView(R.id.bassBoost)
    SeekBar mBassBoost;

    @BindView(R.id.virtualizer)
    SeekBar mVirtualizer;

    @BindView(R.id.reverb)
    Spinner mReverb;

    Equalizer mEqualizer;
    private BaseMediaActivityListener mListener;
    @Override
    protected void onMediaBrowserChildrenLoaded(@NonNull String parentId, List<MediaBrowserCompat.MediaItem> children) {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() instanceof EqualizerActivity) {
            mListener = (EqualizerActivity) getActivity();
            mListener.setTitle(getString(R.string.setting));
        }
        mEqualizer = MusicService.getEqualizer();
        mSeekBar60.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mEqualizer != null)
                        mEqualizer.setBandLevel((short) 0, (short) (i + 1500));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSeekBar230.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mEqualizer != null)
                    mEqualizer.setBandLevel((short) 1, (short) (i + 1500));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSeekBar910.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mEqualizer != null)
                    mEqualizer.setBandLevel((short) 2, (short) (i + 1500));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSeekBar3600.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mEqualizer != null)

                    mEqualizer.setBandLevel((short) 3, (short) (i + 1500));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSeekBar14000.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mEqualizer != null)

                    mEqualizer.setBandLevel((short) 4, (short) (i + 1500));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ArrayList<String> equalizerPresetNames = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_item, equalizerPresetNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        equalizerPresetNames.add("Normal");
        equalizerPresetNames.add("Classical");
        equalizerPresetNames.add("Dance");
        equalizerPresetNames.add("Flat");
        equalizerPresetNames.add("Folk");
        equalizerPresetNames.add("Heavy Metal");
        equalizerPresetNames.add("Hip Hop");
        equalizerPresetNames.add("Jazz");
        equalizerPresetNames.add("Pop");
        equalizerPresetNames.add("Rock");

        final short[][] listBandLevel = new short[10][5];
        listBandLevel[0][0] = 1800; listBandLevel[0][1] = 1500; listBandLevel[0][2] = 1500; listBandLevel[0][3] = 1500; listBandLevel[0][4] = 1800;
        listBandLevel[1][0] = 2000; listBandLevel[1][1] = 1800; listBandLevel[1][2] = 1300; listBandLevel[1][3] = 1900; listBandLevel[1][4] = 1900;
        listBandLevel[2][0] = 2100; listBandLevel[2][1] = 1500; listBandLevel[2][2] = 1700; listBandLevel[2][3] = 1300; listBandLevel[2][4] = 1600;
        listBandLevel[3][0] = 1500; listBandLevel[3][1] = 1500; listBandLevel[3][2] = 1500; listBandLevel[3][3] = 1500; listBandLevel[3][4] = 1500;
        listBandLevel[4][0] = 1700; listBandLevel[4][1] = 1500; listBandLevel[4][2] = 1500; listBandLevel[4][3] = 1700; listBandLevel[4][4] = 1400;
        listBandLevel[5][0] = 1900; listBandLevel[5][1] = 1600; listBandLevel[5][2] = 2400; listBandLevel[5][3] = 1800; listBandLevel[5][4] = 1500;
        listBandLevel[6][0] = 2000; listBandLevel[6][1] = 1800; listBandLevel[6][2] = 1500; listBandLevel[6][3] = 1600; listBandLevel[6][4] = 1800;
        listBandLevel[7][0] = 1900; listBandLevel[7][1] = 1700; listBandLevel[7][2] = 1300; listBandLevel[7][3] = 1700; listBandLevel[7][4] = 2000;
        listBandLevel[8][0] = 1400; listBandLevel[8][1] = 1700; listBandLevel[8][2] = 2000; listBandLevel[8][3] = 1600; listBandLevel[8][4] = 1300;
        listBandLevel[9][0] = 2000; listBandLevel[9][1] = 1800; listBandLevel[9][2] = 1400; listBandLevel[9][3] = 1800; listBandLevel[9][4] = 2000;
        mPreset.setAdapter(adapter);
        mPreset.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mEqualizer != null)
                    mEqualizer.usePreset((short) i);
                mSeekBar60.setProgress(listBandLevel[i][0]);
                mSeekBar60.updateThumb();
                mSeekBar230.setProgress(listBandLevel[i][1]);
                mSeekBar230.updateThumb();
                mSeekBar910.setProgress(listBandLevel[i][2]);
                mSeekBar910.updateThumb();
                mSeekBar3600.setProgress(listBandLevel[i][3]);
                mSeekBar3600.updateThumb();
                mSeekBar14000.setProgress(listBandLevel[i][4]);
                mSeekBar14000.updateThumb();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mBassBoost.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (MusicService.getBassBoost() != null)
                    MusicService.getBassBoost().setStrength((short) i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mVirtualizer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (MusicService.getVirtualizer() != null)
                    MusicService.getVirtualizer().setStrength((short) i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ArrayList<String> reverbPresets = new ArrayList<String>();
        reverbPresets.add("None");
        reverbPresets.add("Large Hall");
        reverbPresets.add("Large Room");
        reverbPresets.add("Medium Hall");
        reverbPresets.add("Medium Room");
        reverbPresets.add("Small Room");
        reverbPresets.add("Plate");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, reverbPresets);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mReverb.setAdapter(dataAdapter);

        mReverb.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int index, long arg3) {

                if (MusicService.getReverb() != null)
                    switch (index) {
                        case 0:
                           MusicService.getReverb().setPreset(PresetReverb.PRESET_NONE);
                           break;
                        case 1:
                            MusicService.getReverb().setPreset(PresetReverb.PRESET_LARGEHALL);
                            break;
                        case 2:
                            MusicService.getReverb().setPreset(PresetReverb.PRESET_LARGEROOM);
                            break;
                        case 3:
                            MusicService.getReverb().setPreset(PresetReverb.PRESET_MEDIUMHALL);
                            break;
                        case 4:
                            MusicService.getReverb().setPreset(PresetReverb.PRESET_MEDIUMROOM);
                            break;
                        case 5:
                            MusicService.getReverb().setPreset(PresetReverb.PRESET_SMALLROOM);
                            break;
                        case 6:
                            MusicService.getReverb().setPreset(PresetReverb.PRESET_PLATE);
                            break;
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void permissionGranted(int permissionRequestCode) {

    }
}
