package cn.ittiger.video.app;

import android.app.Application;

import butterknife.ButterKnife;
import cn.ittiger.database.SQLiteDBConfig;
import com.browser2345.player.Config;
import com.browser2345.player.PlayerManager;
import com.browser2345.player.factory.IjkPlayerFactory;
import cn.ittiger.video.R;
import cn.ittiger.video.util.ApplicationHelper;
import cn.ittiger.video.util.CrashHandler;

/**
 * @author laohu
 */
public class TigerApplication extends Application implements IDbApplication {
    /**
     * 本地数据库配置
     */
    private SQLiteDBConfig mDBConfig;

    @Override
    public void onCreate() {

        super.onCreate();
        PlayerApplication.onCreate(this);
        CrashHandler.getInstance().init(getApplicationContext());
        ApplicationHelper.getInstance().init(this);
        ButterKnife.setDebug(true);
        PlayerManager.loadConfig(
            new Config.Builder(this)
            .enableSmallWindowPlay()
            .cache(false)
            .buildPlayerFactory(new IjkPlayerFactory())
            .build()
        );
    }

    @Override
    public SQLiteDBConfig getGlobalDbConfig() {

        if(mDBConfig == null) {
            synchronized (TigerApplication.class) {
                if(mDBConfig == null) {
                    mDBConfig = new SQLiteDBConfig(getApplicationContext());
                    mDBConfig.setDbName(getResources().getString(R.string.app_name) + ".db");
                    //本地数据库文件保存在应用文件目录
                    mDBConfig.setDbDirectoryPath(getApplicationContext().getExternalCacheDir().getAbsolutePath());
                }
            }
        }
        return mDBConfig;
    }

    public void onDestroy() {

        ApplicationHelper.getInstance().onDestory();
        this.mDBConfig = null;
    }
}
