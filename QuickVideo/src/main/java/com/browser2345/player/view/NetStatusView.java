package com.browser2345.player.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.AttributeSet;
import android.widget.ImageView;


import com.browser2345.player.R;

public class NetStatusView extends ImageView {
    private ConnectivityManager mNetMananger;
    private WifiManager mWifiManager;
    private static final int TYPE_NO = -1;

    public NetStatusView(Context context) {
        super(context);
        mNetMananger = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        refreshStatus();
    }

    public NetStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mNetMananger = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        refreshStatus();
    }

    public NetStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mNetMananger = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        refreshStatus();
    }

    @Override
    protected void onAttachedToWindow() {
        getContext().registerReceiver(mNetWorkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        getContext().unregisterReceiver(mNetWorkReceiver);
        super.onDetachedFromWindow();
    }

    BroadcastReceiver mNetWorkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshStatus();
        }
    };

    private void refreshStatus(){
        try {
            NetworkInfo info = mNetMananger.getActiveNetworkInfo();
            mWifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();

            int wifi = mWifiInfo.getRssi();//获取wifi信号强度

            if(info==null){
                //setImageResource(R.drawable.data_error);
                return;
            }else if(ConnectivityManager.TYPE_WIFI==info.getType()){

                if (wifi > -50 && wifi < 0) {//最强
                    setImageResource(R.drawable.news_video_wifi_3);
                } else if (wifi > -70 && wifi < -50) {//较强
                    setImageResource(R.drawable.news_video_wifi_2);
                } else if (wifi > -80 && wifi < -70) {//较弱
                    setImageResource(R.drawable.news_video_wifi_1);
                }
            }else{
                //setImageResource(R.drawable.phone);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
