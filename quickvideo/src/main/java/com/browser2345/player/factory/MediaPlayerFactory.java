package com.browser2345.player.factory;

import com.browser2345.player.AbsSimplePlayer;
import com.browser2345.player.util.Utils;
import com.browser2345.player.VideoMediaPlayer;

/**
 * 创建基于MediaPlayer实现的播放器
 * @author: ylhu
 * @time: 17-9-13
 */
public class MediaPlayerFactory implements IPlayerFactory {

    @Override
    public AbsSimplePlayer create() {

        Utils.log("create MediaPlayer");
        return new VideoMediaPlayer();
    }
}
