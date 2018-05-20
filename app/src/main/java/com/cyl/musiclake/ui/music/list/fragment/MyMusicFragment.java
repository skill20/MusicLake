package com.cyl.musiclake.ui.music.list.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;

import com.cyl.musiclake.R;
import com.cyl.musiclake.base.BaseFragment;
import com.cyl.musiclake.bean.Music;
import com.cyl.musiclake.bean.Playlist;
import com.cyl.musiclake.common.NavigationHelper;
import com.cyl.musiclake.ui.music.list.adapter.LocalAdapter;
import com.cyl.musiclake.ui.music.list.adapter.PlaylistAdapter;
import com.cyl.musiclake.ui.music.list.contract.MyMusicContract;
import com.cyl.musiclake.ui.music.list.dialog.CreatePlaylistDialog;
import com.cyl.musiclake.ui.music.list.presenter.MyMusicPresenter;
import com.cyl.musiclake.ui.my.user.UserStatus;
import com.cyl.musiclake.utils.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Monkey on 2015/6/29.
 */
public class MyMusicFragment extends BaseFragment<MyMusicPresenter> implements MyMusicContract.View {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.hor_recyclerView)
    RecyclerView mHorRecyclerView;
    @BindView(R.id.scroll_view)
    NestedScrollView mNestedScrollView;

    @OnClick(R.id.iv_playlist_add)
    void addPlaylist() {
        if (UserStatus.getstatus(getContext())) {
            CreatePlaylistDialog dialog = CreatePlaylistDialog.newInstance();
            dialog.show(getChildFragmentManager(), TAG_CREATE);
        } else {
            ToastUtils.show("请登录");
        }
    }

    private static final String TAG_CREATE = "create_playlist";
    private List<Playlist> mData;
    private PlaylistAdapter mAdapter;
    private LocalAdapter mLocalAdapter;

    public static MyMusicFragment newInstance() {
        Bundle args = new Bundle();
        MyMusicFragment fragment = new MyMusicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.frag_local;
    }

    @Override
    public void initViews() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setSmoothScrollbarEnabled(false);

        mHorRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mHorRecyclerView.setNestedScrollingEnabled(false);

        mLocalAdapter = new LocalAdapter();
        mHorRecyclerView.setAdapter(mLocalAdapter);
        mLocalAdapter.bindToRecyclerView(mHorRecyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setNestedScrollingEnabled(false);

        mAdapter = new PlaylistAdapter(mData);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(mRecyclerView);

    }

    @Override
    protected void initInjector() {
        mFragmentComponent.inject(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void listener() {
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
                    Pair<View, String> transitionViews = new Pair<View, String>(view.findViewById(R.id.iv_album), "transition_album_art" + position);
                    NavigationHelper.navigateToPlaylist(getActivity(), (Playlist) adapter.getItem(position), transitionViews);
                }
        );
        mLocalAdapter.setOnItemClickListener((adapter, view, position) -> {
                    if (position == 0) {
                        NavigationHelper.navigateToLocalMusic(getActivity(), null);
                    } else if (position == 1) {
                        NavigationHelper.navigateRecentlyMusic(getActivity());
                    } else if (position == 2) {
                        NavigationHelper.navigateToLoveMusic(getActivity(), null);
                    } else if (position == 3) {
                        NavigationHelper.navigateToDownload(getActivity(), null);
                    }
                }
        );
    }

    @Override
    protected void loadData() {
        mPresenter.loadSongs();
        mPresenter.loadPlaylist();
    }


    @Override
    public void showSongs(List<Music> songList) {
        mLocalAdapter.setSongsNum(0, songList.size());
    }

    @Override
    public void showEmptyView() {
        mAdapter.setEmptyView(R.layout.view_playlist_empty);
    }

    @Override
    public void showPlaylist(List<Playlist> playlists) {
        mAdapter.setNewData(playlists);
    }

    @Override
    public void showHistory(List<Music> musicList) {
        mLocalAdapter.setSongsNum(1, musicList.size());
    }

    @Override
    public void showLoveList(List<Music> musicList) {
        mLocalAdapter.setSongsNum(2, musicList.size());
    }

    @Override
    public void showDownloadList(List<Music> musicList) {
        mLocalAdapter.setSongsNum(3, musicList.size());
    }

}
