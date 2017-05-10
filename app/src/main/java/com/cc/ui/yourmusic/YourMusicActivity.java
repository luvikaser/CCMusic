package com.cc.ui.yourmusic;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.service.textservice.SpellCheckerService;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cc.app.R;
import com.cc.ui.base.BaseFragment;
import com.cc.ui.base.BaseMediaActivity;
import com.cc.ui.base.BaseMediaActivityListener;
import com.cc.ui.equalizer.EqualizerFragment;
import com.cc.ui.image.CircleImageView;
import com.cc.ui.karaoke.ui.fragment.record.VMFragmentRecordList;
import com.cc.ui.karaoke.ui.fragment.song.karaoke.VMSongArirangFragment;
import com.cc.ui.karaoke.ui.fragment.song.karaoke.VMSongCaliforniaFragment;
import com.cc.ui.karaoke.ui.fragment.song.karaoke.VMSongFarvoriteFragment;
import com.cc.ui.karaoke.ui.fragment.song.karaoke.VMSongMusicCoreFragment;
import com.cc.ui.karaoke.ui.fragment.song.karaoke.VMSongVietKtvFragment;
import com.cc.ui.song.SongLocalFragment;
import com.cc.utils.CCMusicUtils;
import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import io.fabric.sdk.android.Fabric;

import static android.view.View.GONE;

/**
 * Author: NT
 * Since: 10/30/2016.
 */
public class YourMusicActivity extends BaseMediaActivity implements BaseMediaActivityListener {

    private NavigationView navigationView;
    public static CallbackManager callbackManager;
    private TextView mLoginTextView;
    private TextView mUser;
    private ImageView mAvatar;

    private DrawerLayout mDrawerLayout;
    private ExpandableListAdapter mMenuAdapter;
    private ExpandableListView mExpandableList;
    private List<ExpandedMenuModel> mListDataHeader;
    private HashMap<ExpandedMenuModel, List<String>> mListDataChild;
    @Override
    public BaseFragment getFragmentToHost() {
        return SongLocalFragment.newInstance(new YourMusicFragment(), getIntent().getExtras());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mExpandableList = (ExpandableListView) findViewById(R.id.navigationmenu);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        prepareListData();

        mMenuAdapter = new ExpandableListAdapter(this, mListDataHeader, mListDataChild, mExpandableList);

        // setting list adapter
        mExpandableList.setAdapter(mMenuAdapter);

        mExpandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                onNavigationItemSelected(i, i1);
                return false;
            }
        });
        mExpandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                onNavigationItemSelected(i, -1);
                return false;
            }
        });
        View hView =  navigationView.getHeaderView(0);
        mLoginTextView = (TextView) hView.findViewById(R.id.login_textview);
        mUser = (TextView) hView.findViewById(R.id.user);
        mAvatar = (ImageView) hView.findViewById(R.id.avatar);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");

                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                    }
                });

        if (AccessToken.getCurrentAccessToken() != null){
            getInfo();
            mLoginTextView.setVisibility(GONE);
        }

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken accessToken2) {
                if (accessToken == null) {
                    getInfo();
                    mLoginTextView.setVisibility(GONE);
                } else if (accessToken2 == null) {
                    mAvatar.setImageResource(R.drawable.ic_avatar);
                    mUser.setText(getString(R.string.hello));
                    mLoginTextView.setVisibility(View.VISIBLE);
                    new GraphRequest(accessToken, "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                            .Callback() {
                        @Override
                        public void onCompleted(GraphResponse graphResponse) {


                        }
                    }).executeAsync();
                }
            }
        };
        mLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            LoginManager.getInstance().logInWithReadPermissions(YourMusicActivity.this, Arrays.asList("public_profile", "user_friends"));
            }
        });

        Fabric.with(this, new Crashlytics());
    }

    private void prepareListData() {
        mListDataHeader = new ArrayList<ExpandedMenuModel>();
        mListDataChild = new HashMap<ExpandedMenuModel, List<String>>();

        ExpandedMenuModel item1 = new ExpandedMenuModel();
        item1.setIconName("HOME");
        item1.setIconImg(R.drawable.ic_home);
        // Adding data header
        mListDataHeader.add(item1);

        ExpandedMenuModel item2 = new ExpandedMenuModel();
        item2.setIconName("MY MUSIC");
        item2.setIconImg(R.drawable.ic_my_music);
        mListDataHeader.add(item2);

        ExpandedMenuModel item3 = new ExpandedMenuModel();
        item3.setIconName("MY KARA");
        item3.setIconImg(R.drawable.ic_mic_outline);
        mListDataHeader.add(item3);

        ExpandedMenuModel item8 = new ExpandedMenuModel();
        item8.setIconName("MY FILM");
        item8.setIconImg(R.drawable.ic_my_film);
        mListDataHeader.add(item8);

        ExpandedMenuModel item4 = new ExpandedMenuModel();
        item4.setIconName("NOTIFICATION");
        item4.setIconImg(R.drawable.ic_notify);
        mListDataHeader.add(item4);

        ExpandedMenuModel item5 = new ExpandedMenuModel();
        item5.setIconName("SHARE APP");
        item5.setIconImg(R.drawable.ic_onnect);
        mListDataHeader.add(item5);

        ExpandedMenuModel item6 = new ExpandedMenuModel();
        item6.setIconName("INVITE");
        item6.setIconImg(R.drawable.invite);
        mListDataHeader.add(item6);

        ExpandedMenuModel item7 = new ExpandedMenuModel();
        item7.setIconName("SETTINGS");
        item7.setIconImg(R.drawable.ic_settings);
        mListDataHeader.add(item7);

        // Adding child data
        List<String> heading3 = new ArrayList<String>();
        heading3.add("My record");
        heading3.add("Arirang");
        heading3.add("California");
        heading3.add("Music core");
        heading3.add("Viet KTV");
        heading3.add("Favorite");

        mListDataChild.put(mListDataHeader.get(2), heading3);// Header, Child data
        mListDataChild.put(mListDataHeader.get(0), new ArrayList<String>());
        mListDataChild.put(mListDataHeader.get(1), new ArrayList<String>());
        mListDataChild.put(mListDataHeader.get(3), new ArrayList<String>());
        mListDataChild.put(mListDataHeader.get(4), new ArrayList<String>());
        mListDataChild.put(mListDataHeader.get(5), new ArrayList<String>());
        mListDataChild.put(mListDataHeader.get(6), new ArrayList<String>());
        mListDataChild.put(mListDataHeader.get(7), new ArrayList<String>());

    }

    private void setupDrawerContent(NavigationView navigationView) {
        //revision: this don't works, use setOnChildClickListener() and setOnGroupClickListener() above instead
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public boolean onNavigationItemSelected(int id, int idChild) {
        Fragment fragment = null;
        Class fragmentClass = null;

        if (id == 0) {
            fragmentClass = YourMusicFragment.class;
        } else if (id == 1) {
            fragmentClass = YourMusicFragment.class;
        } else if (id == 2) {
            if (idChild == -1)
                return false;
            switch (idChild){
                case 0:
                    fragmentClass = VMFragmentRecordList.class;
                    break;
                case 1:
                    fragmentClass = VMSongArirangFragment.class;
                    break;
                case 2:
                    fragmentClass = VMSongCaliforniaFragment.class;
                    break;
                case 3:
                    fragmentClass = VMSongMusicCoreFragment.class;
                    break;
                case 4:
                    fragmentClass = VMSongVietKtvFragment.class;
                    break;
                case 5:
                    fragmentClass = VMSongFarvoriteFragment.class;
                    break;
            }
        } else if (id == 3){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_film)));
            startActivity(intent);
        }else if (id == 4) {

        } else if (id == 5) {
            ShareDialog shareDialog = new ShareDialog(this);
            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {

                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {

                }
            });
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(getString(R.string.link_play_store)))
                        .setImageUrl(Uri.parse(getString(R.string.preview_image_url)))
                        .setContentTitle(getString(R.string.app_name))
                        .setContentDescription(getString(R.string.description_share_app))
                        .build();

                shareDialog.show(content);
            }

        } else if (id == 7) {
            fragmentClass = EqualizerFragment.class;
        } else if (id == 6) {
            String appLinkUrl, previewImageUrl;

            appLinkUrl = getString(R.string.app_link_url);
            previewImageUrl = getString(R.string.preview_image_url);

            if(AppInviteDialog.canShow())
            {
                AppInviteContent content = new AppInviteContent.Builder()
                        .setApplinkUrl(appLinkUrl)
                        .setPreviewImageUrl(previewImageUrl)
                        .build();

                AppInviteDialog appInviteDialog = new AppInviteDialog(this);
                appInviteDialog.registerCallback(callbackManager, new FacebookCallback<AppInviteDialog.Result>()
                {
                    @Override
                    public void onSuccess(AppInviteDialog.Result result)
                    {
                        Toast.makeText(getApplication(), getString(R.string.invite_friend_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel()
                    {
                    }

                    @Override
                    public void onError(FacebookException e)
                    {
                        Toast.makeText(getApplication(), getString(R.string.invite_friend_fail), Toast.LENGTH_SHORT).show();
                    }
                });
                appInviteDialog.show(content);
            }
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void setCount(int count) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void getInfo(){
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {

                            String userID = (String) object.get("id");
                            String userName = (String) object.get("name");
                            Target target = new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    mAvatar.setImageBitmap(CCMusicUtils.getRoundedCornerBitmap(bitmap));
                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            };
                            Picasso.with(YourMusicActivity.this).load("https://graph.facebook.com/" + userID+ "/picture?type=large").into(target);
                            //Bitmap b = (Bitmap) object.get("picture");
                            mUser.setText(userName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,birthday,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }
}