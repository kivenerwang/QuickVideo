package cn.ittiger.player.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

public class BatteryView extends ImageView {
    public float mCurrentX;
    public float mCurrentY;
    private float mWidth;
    private float mHeight;
    private float percentage;
    private float mSecondX;

    private int speedTime = 5000;
    private Paint mPaint = new Paint();
    private Context mContext;
    private Handler mHandler;
    private Bitmap mBitmap;



    public BatteryView(Context context) {
        super(context);
        this.mContext = context;
    }

    public BatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        //initView();
    }



    public BatteryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }

    @Override
    protected void onAttachedToWindow() {
        getContext().registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        getContext().unregisterReceiver(mBatInfoReceiver);
        super.onDetachedFromWindow();
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        mHeight = this.getHeight();
//        mWidth = this.getWidth();
//
//        mPaint.setColor(Color.WHITE);
//        Resources res = mContext.getResources();
//        BitmapDrawable bmpDraw = (BitmapDrawable) res.getDrawable(R.drawable.news_video_battery);
//        mBitmap = bmpDraw.getBitmap();
//        Rect srcRect = new Rect(0,0,mBitmap.getWidth(),mBitmap.getHeight());
//        Rect destRect = new Rect(0,0,mBitmap.getWidth()/20,mBitmap.getHeight());
//        canvas.drawBitmap(mBitmap,srcRect,destRect,mPaint);
//        //canvas.drawBitmap(mBitmap,mSecondX,0,mPaint);
//        Log2345.printfLog("[onDraw]mCurrentX = "+ mCurrentX);
//        Log2345.printfLog("[onDraw]mSecondX = "+ mSecondX);
//    }

//    private void initView() {
//        percentage = 0;
//
//        mHandler = new Handler() {
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//                    case 1:
//                        mCurrentX ++;
//                        mCurrentY ++;
//                        if (mBitmap != null && mCurrentX > mBitmap.getWidth()){
//                            mCurrentX = -mBitmap.getWidth();
//                        }
//                        if (mBitmap != null){
//                            mSecondX = mCurrentY+mBitmap.getWidth();
//                            if (mSecondX >= mBitmap.getWidth()){
//                                mSecondX = mCurrentX-mBitmap.getWidth();
//                            }
//                        }
//
//                        percentage = percentage + 0.003f;
//                        if (percentage > 1){
//                            percentage = 0;
//                        }
//                        // 每次计算后都发送消息进入下一次循环，并刷新界面
//                        mHandler.sendEmptyMessageDelayed(1, speedTime);
//                        postInvalidate();
//                        break;
//                }
//                super.handleMessage(msg);
//                postInvalidate();
//            }
//        };
//
//        // 首次循环刷新界面
//        mHandler.sendEmptyMessageDelayed(1, speedTime);
//    }
//


    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        int intLevel = 0;
        int intScale = 0;
//		String BatteryV = "";
//		String BatteryT = "";
//		String BatteryStatus = "";
//		String BatteryStatus2 = "";
//		String BatteryTemp = "";

        public void onReceive(Context context, Intent intent) {
            if(intent==null || intent.getAction()==null) {
                return;
            }
            String action = intent.getAction();
			/*
			 * 如果捕捉到的action是ACTION_BATTERY_CHANGED， 就运行onBatteryInfoReceiver()
			 */
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                intLevel = intent.getIntExtra("level", 0);
                intScale = intent.getIntExtra("scale", 100);
//				if (mBatteryView != null) {
                BatteryView.this.getDrawable().setLevel((intLevel * 100) / intScale);
//				// 电池伏数
//				Log.d("Battery V", "" + intent.getIntExtra("voltage", 0));
//				// 电池温度
//				Log.d("Battery T", "" + intent.getIntExtra("temperature", 0));
//				BatteryV = "当前电压为：" + intent.getIntExtra("voltage", 0);
//				BatteryT = "当前温度为：" + intent.getIntExtra("temperature", 0);
//				switch (intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN)) {
//				case BatteryManager.BATTERY_STATUS_CHARGING:
//					BatteryStatus = "充电状态";
//					break;
//				case BatteryManager.BATTERY_STATUS_DISCHARGING:
//					BatteryStatus = "放电状态";
//					break;
//				case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
//					BatteryStatus = "未充电";
//					break;
//				case BatteryManager.BATTERY_STATUS_FULL:
//					BatteryStatus = "充满电";
//					break;
//				case BatteryManager.BATTERY_STATUS_UNKNOWN:
//					BatteryStatus = "未知道状态";
//					break;
//				}
//				switch (intent.getIntExtra("plugged", BatteryManager.BATTERY_PLUGGED_AC)) {
//				case BatteryManager.BATTERY_PLUGGED_AC:
//					BatteryStatus2 = "AC充电";
//					break;
//				case BatteryManager.BATTERY_PLUGGED_USB:
//					BatteryStatus2 = "USB充电";
//					break;
//				}
//				switch (intent.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN)) {
//				case BatteryManager.BATTERY_HEALTH_UNKNOWN:
//					BatteryTemp = "未知错误";
//					break;
//				case BatteryManager.BATTERY_HEALTH_GOOD:
//					BatteryTemp = "状态良好";
//					break;
//				case BatteryManager.BATTERY_HEALTH_DEAD:
//					BatteryTemp = "电池没有电";
//					break;
//				case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
//					BatteryTemp = "电池电压过高";
//					break;
//				case BatteryManager.BATTERY_HEALTH_OVERHEAT:
//					BatteryTemp = "电池过热";
//					break;
//				}
            }
        }
    };
}
