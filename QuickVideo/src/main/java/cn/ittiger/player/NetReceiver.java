package cn.ittiger.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.quickplayer.bean.NetMsgEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by kiven on 2/11/18.
 */

public class NetReceiver extends BroadcastReceiver {

    private int mLastNetState = -1;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null)
            return;
        String action = intent.getAction();
        String NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
        Log.i("dongdong", "action = " + action);
        if (NET_CHANGE_ACTION.equals(intent.getAction())) {
            ConnectivityManager ConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = ConnectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isAvailable()){
                int currentType = netInfo.getType();
                if (currentType != mLastNetState) {
                    EventBus.getDefault().post(new NetMsgEvent(currentType));
                    mLastNetState = currentType;
                }
            } else if (mLastNetState != -1) {
                EventBus.getDefault().post(new NetMsgEvent(netInfo.getType()));
                mLastNetState = -1;
            }
        }
    }
}
