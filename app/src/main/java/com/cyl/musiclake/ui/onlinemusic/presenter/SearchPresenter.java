package com.cyl.musiclake.ui.onlinemusic.presenter;

import android.content.Context;

import com.cyl.musiclake.api.qq.QQApiServiceImpl;
import com.cyl.musiclake.api.xiami.XiamiServiceImpl;
import com.cyl.musiclake.data.model.Music;
import com.cyl.musiclake.service.PlayManager;
import com.cyl.musiclake.ui.onlinemusic.contract.SearchContract;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yonglong on 2018/1/6.
 */

public class SearchPresenter implements SearchContract.Presenter {
    SearchContract.View mView;
    Context mContext;

    @Override
    public void attachView(SearchContract.View view) {
        mView = view;
        mContext = (Context) view;
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void search(String key, int limit, int page) {
        mView.showLoading();
        Observable.merge(QQApiServiceImpl.search(key, limit, page), XiamiServiceImpl.search(key, limit, page))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Music>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Music> results) {
                        mView.showSearchResult(results);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showEmptyView();
                        mView.hideLoading();
                    }

                    @Override
                    public void onComplete() {
                        mView.hideLoading();
                    }
                });
    }

    @Override
    public void play(Music music) {
        if (music.getType() == Music.Type.QQ) {
            QQApiServiceImpl.getMusicInfo(music)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Music>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Music music) {
                            PlayManager.playOnline(music);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            PlayManager.playOnline(music);
        }
    }


}