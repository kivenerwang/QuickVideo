package com.browser2345.player.factory;

import com.browser2345.player.AbsSimplePlayer;
import com.browser2345.player.IjkVideoPlayer;

/**
 * Created by kiven on 1/29/18.
 */

public class IjkPlayerFactory implements IPlayerFactory {
    @Override
    public AbsSimplePlayer create() {
        return new IjkVideoPlayer();
    }
}
