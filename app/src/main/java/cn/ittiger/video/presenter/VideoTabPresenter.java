package cn.ittiger.video.presenter;

import cn.ittiger.video.bean.VideoTabData;
import cn.ittiger.video.factory.ResultParseFactory;
import cn.ittiger.video.mvpview.VideoTabMvpView;
import cn.ittiger.video.util.DBManager;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.List;

/**
 * @author: laohu on 2016/10/8
 * @site: http://ittiger.cn
 */
public abstract class VideoTabPresenter extends MvpBasePresenter<VideoTabMvpView> implements TypePresenter {

    public void queryVideoTab(final boolean pullToRefresh) {

        Observable.just(getType().value())
                .subscribeOn(Schedulers.io())
                .map(new Function<Integer, Boolean>() {
                    @Override
                        public Boolean apply(Integer integer) {

                        String[] whereArgs = {String.valueOf(integer.intValue())};
                        return DBManager.getInstance().getSQLiteDB().queryIfExist(VideoTabData.class, "type=?", whereArgs);
                    }
                })
                .flatMap(new Function<Boolean, Observable<List<VideoTabData>>>() {
                    @Override
                    public Observable<List<VideoTabData>> apply(Boolean aBoolean) {

                        if(aBoolean.booleanValue()) {
                            String[] whereArgs = {String.valueOf(getType().value())};
                            List<VideoTabData> tabs = DBManager.getInstance().getSQLiteDB().query(VideoTabData.class, "type=?", whereArgs);
                            return Observable.just(tabs);
                        }
                        return getHttpCallObservable()
                                .flatMap(new Function<String, Observable<List<VideoTabData>>>() {
                                    @Override
                                    public Observable<List<VideoTabData>> apply(String s) {
                                        List<VideoTabData> tabs = ResultParseFactory.parseTab(s, getType());
                                        if(tabs == null || tabs.size() == 0) {
                                            return Observable.error(new NullPointerException("not load video tab data"));
                                        }
                                        DBManager.getInstance().getSQLiteDB().save(tabs);
                                        return Observable.just(tabs);
                                    }
                                });
                    }
                })

                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<VideoTabData>>() {

                    @Override
                    public void onError(Throwable e) {

                        getView().showError(e, pullToRefresh);
                    }

                    @Override
                    public void onComplete() {
                        getView().showContent();
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<VideoTabData> tabs) {

                        if(isViewAttached()) {
                            getView().setData(tabs);
                        }
                    }
                });




    }

    public abstract Observable<String> getHttpCallObservable();
}
