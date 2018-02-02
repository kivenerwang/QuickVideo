package cn.ittiger.player.factory;

import cn.ittiger.player.AbsSimplePlayer;
import cn.ittiger.player.IjkVideoPlayer;

/**
 * Created by kiven on 1/29/18.
 */

public class IjkPlayerFactory implements IPlayerFactory {
    @Override
    public AbsSimplePlayer create() {
        return new IjkVideoPlayer();
    }
}
