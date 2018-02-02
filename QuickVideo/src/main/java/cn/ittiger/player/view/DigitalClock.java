package cn.ittiger.player.view;

import android.content.Context;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Calendar;

/**
 * 自定义DigitalClock输出格式
 */
public class DigitalClock extends TextView {
    Calendar mCalendar;
    private final static String m12 = "aa h:mm";//h:mm:ss aa
    private final static String m24 = "k:mm";//k:mm:ss

    private Runnable mTicker;
    private Handler mHandler;

    private boolean mTickerStopped = false;
    String mFormat;

    public DigitalClock(Context context) {
        super(context);
        initClock(context);
    }

    public DigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        initClock(context);
    }

    private void initClock(Context context) {
        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }
        setFormat();
        mHandler = new Handler();
        mTicker = new Runnable() {
            public void run() {
                if (mTickerStopped) return;
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                setText(DateFormat.format(mFormat, mCalendar));
                invalidate();
                mHandler.postDelayed(mTicker, 1000);
            }
        };
    }

    @Override
    protected void onAttachedToWindow() {
        mTickerStopped = false;
        super.onAttachedToWindow();
        mHandler.removeCallbacks(mTicker);
        mHandler.post(mTicker);
    }

    @Override
    protected void onDetachedFromWindow() {
        mTickerStopped = true;
        mHandler.removeCallbacks(mTicker);
        super.onDetachedFromWindow();
    }

    /**
     * Pulls 12/24 mode from system settings
     */
    private boolean get24HourMode() {
        return DateFormat.is24HourFormat(getContext());
    }

    private void setFormat() {
        if (get24HourMode()) {
            mFormat = m24;
        } else {
            mFormat = m12;
        }
    }
}
