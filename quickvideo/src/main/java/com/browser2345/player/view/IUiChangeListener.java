package com.browser2345.player.view;

/**
 * Created by kiven on 2/2/18.
 */

public interface IUiChangeListener {

    // status - STATE_NORMAL
    void onChangeUINormalState();

    // statue -STATE_LOADING
    void onChangeUILoadingState();

    // status - STATE_PLAYING
    void onChangeUIPlayingState();

    // status - STATE_PAUSE
    void onChangeUIPauseState();

    // status - STATE_PAUSE
    void onChangeUISeekBufferingState();

    // status - STATE_AUTO_COMPLETE
    void onChangeUICompleteState();

    // status - STATE_ERROR
    void onChangeUIErrorState();
}
