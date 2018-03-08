package cn.ittiger.video.fragment;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceFragment;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.ittiger.video.R;
import cn.ittiger.video.ui.LoadingView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

import java.util.concurrent.TimeUnit;

/**
 * @author laohu
 * @site http://ittiger.cn
 */
public abstract class BaseFragment<CV extends View, M, V extends MvpLceView<M>, P extends MvpPresenter<V>>
        extends MvpLceFragment<CV, M, V, P> implements NameFragment {

    protected Context mContext;
    private boolean mIsInited = false;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.base_fragment_layout, container, false);

        view.addView(getContentView(inflater, savedInstanceState));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        showLoading(false);
        if(isInitRefreshEnable() && isDelayRefreshEnable() == false) {
            loadData(false);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && isInitRefreshEnable() == false && isDelayRefreshEnable() && mIsInited == false) {
            mIsInited = true;
            refreshData(false);
        }
    }

    private void refreshData(final boolean pullToRefresh) {

        if(presenter != null) {
            loadData(pullToRefresh);
        } else {
            Observable.timer(50, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) {
                            refreshData(pullToRefresh);
                        }
                    });
        }
    }

    /**
     * Fragment数据视图
     * @param inflater
     * @param savedInstanceState
     * @return
     */
    public abstract View getContentView(LayoutInflater inflater, @Nullable Bundle savedInstanceState);

    public boolean isInitRefreshEnable() {

        return true;
    }

    public boolean isDelayRefreshEnable() {

        return false;
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {

        return getActivity().getString(R.string.load_failed);
    }

    @Override
    public void showLoading(boolean pullToRefresh) {

        super.showLoading(pullToRefresh);
        if(!pullToRefresh) {
            ((LoadingView)loadingView).start();
        }
    }

    @Override
    public void showContent() {

        super.showContent();
        ((LoadingView)loadingView).stop();
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {

        super.showError(e, pullToRefresh);
        if(!pullToRefresh) {
            ((LoadingView)loadingView).stop();
        }
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        presenter = null;
        mIsInited = false;
    }
}
