package com.yang.bletest.utils;

/**
 * Created by Yang_Mstarc on 2017/8/31.
 */

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import mstarc_os_api.mstarc_os_api_msg;

public class ApduUtil {

    final String TAG = "ApduUtil";
    private final int MSG_XX = 2;
    public boolean INITED = false;

    private mstarc_os_api_msg m_api_msg;
    public Handler mhandler;
    public HandlerThread mhandlerthread;

    public ApduUtil(Context context) {
        mhandlerthread = new HandlerThread("APDU");
        mhandlerthread.start();
        mhandler = new Handler(mhandlerthread.getLooper()) {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_XX:
                        init_nfc();
                        break;
                    default:
                        break;
                }
            }
        };
        m_api_msg=new mstarc_os_api_msg(context)
        {
            @Override
            public void onServiceConnected() {
                // TODO 自动生成的方法存根
                super.onServiceConnected();
                Log.i(TAG, "service connected");
                Message msg = mhandler.obtainMessage();
                msg.what=MSG_XX;
                mhandler.sendMessageDelayed(msg, 1000);
            }
        };
    }

    public boolean init_nfc()
    {
        Log.i(TAG, "init nfc");
        if(m_api_msg != null)
        {
            m_api_msg.mstarc_sle97_init();
            //m_api_msg.mstarc_sle97_reset();
            INITED = true;
        }
        else {
            return false;
        }

        return true;
    }

    public int[] reset_nfc()
    {
        Log.i(TAG, "reset nfc");
        if(m_api_msg != null)
        {
            //m_api_msg.mstarc_sle97_init();
            return m_api_msg.mstarc_sle97_reset();
        }

        return null;
    }

    public void power_off()
    {
        m_api_msg.mstarc_sle97_write_buf(0x1B05, new int[]{0,0});	//power off
    }

    public int[] send_apdu(int[] data)
    {
        return m_api_msg.mstarc_sle97_apdu(data);
    }


}
