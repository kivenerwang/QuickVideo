package cn.ittiger.video.presenter;

import android.util.Log;

import cn.ittiger.video.bean.VideoData;
import cn.ittiger.video.factory.ResultParseFactory;
import cn.ittiger.video.mvpview.VideoMvpView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.List;

/**
 * @author laohu
 * @site http://ittiger.cn
 */
public abstract class VideoPresenter extends MvpBasePresenter<VideoMvpView>
        implements TypePresenter {

    private int mCurPage = 1;

    public void refreshData(boolean pullToRefresh) {

        mCurPage = 1;
        request(false, pullToRefresh);
    }

    public void loadMoreData() {

        request(true, false);
    }

    @Override
    public void detachView(boolean retainInstance) {

        super.detachView(retainInstance);
        mCurPage = 1;
    }

    public abstract Observable<String> getHttpCallObservable(int curPage);

    void request(final boolean loadMore, final boolean pullToRefresh) {

        getHttpCallObservable(mCurPage)
                .subscribeOn(Schedulers.io())
                .map(new Function<String, List<VideoData>>() {

                    @Override
                    public List<VideoData> apply(String s) {
                        return ResultParseFactory.parse(s, getType());
                    }
                })
                .flatMap(new Function<List<VideoData>, Observable<List<VideoData>>>() {
                    @Override
                    public Observable<List<VideoData>> apply(List<VideoData> videos) {

                        if(videos == null || videos.size() == 0) {
                            return Observable.error(new NullPointerException("not load video data"));
                        }
                        for (VideoData data : videos) {
                            Log.i("dongdong", "video data = " + data.toString());
                        }
                        return Observable.just(videos);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<VideoData>>() {

                    @Override
                    public void onError(Throwable e) {

                        if(loadMore == false) {
                            getView().showError(e, pullToRefresh);
                        } else {
                            getView().showLoadMoreErrorView();
                        }
                    }

                    @Override
                    public void onComplete() {
                        getView().showContent();
                        mCurPage ++;
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<VideoData> videos) {

                        if(isViewAttached()) {
                            if(loadMore == false) {
                                getView().setData(videos);
                            } else {
                                getView().setLoadMoreData(videos);
                            }
                        }
                    }
                });
    }
}
