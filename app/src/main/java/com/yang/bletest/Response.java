package com.yang.bletest;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.yang.bletest.utils.utils;

import java.awt.font.TextAttribute;
import java.util.ArrayList;

/**
 * Created by Yang_Mstarc on 2017/9/1.
 */

public class Response {

    protected byte mode = 0;
    protected byte reserved = 0;
    protected byte[] ssc = new byte[2];
    protected byte[] status = new byte[2];
    protected byte[] len_data = new byte[2];
    protected byte[] data;
    protected byte lrc = 0;

    public int[] response;
    public int[] atr;

    static final byte[] BTC_IO_OK = new byte[] {0x00, 0x00};
    static final byte[] BTC_ILLEGAL_CMD = new byte[] {0x00, 0x01};
    static final byte[] BTC_IO_TIMEOUT = new byte[] {0x00, 0x02};
    static final byte[] BTC_IO_ERROR = new byte[] {0x00, 0x03};
    static final byte[] BTC_IO_BUSY = new byte[] {0x00, 0x04};
    static final byte[] BTC_ILLEGAL_STATUS = new byte[] {0x00, 0x05};
    static final byte[] BTC_SSC_ERROR = new byte[] {0x00, 0x06};
    static final byte[] BTC_MODE_ERROR = new byte[] {0x00, 0x07};

    private HandlerThread mthread;
    private Handler mhandler;

    final String TAG = "yhy";

    public Response() {
        mthread = new HandlerThread("response");
        mthread.start();

        mhandler = new Handler(mthread.getLooper());
    }

    public byte[] make_response()
    {
        ArrayList<Byte> array = new ArrayList<>();

        generate_lrc();

        array.add(Command.END);     //start indication
        array.add(this.mode);       //mode
        array.add(this.reserved);   //reserved
        //ssc
        for(int i=0;i< ssc.length;i++)
        {
            if(ssc[i] == Command.END)
            {
                array.add(Command.ESC);
                array.add((byte)0xdc);
            }
            else if (ssc[i] == Command.ESC)
            {
                array.add(Command.ESC);
                array.add((byte)0xdd);
            }
            else
            {
                array.add(ssc[i]);
            }
        }
        //status
        for(int i=0;i< status.length;i++)
        {
            if(status[i] == Command.END)
            {
                array.add(Command.ESC);
                array.add((byte)0xdc);
            }
            else if (status[i] == Command.ESC)
            {
                array.add(Command.ESC);
                array.add((byte)0xdd);
            }
            else
            {
                array.add(status[i]);
            }
        }
        //data length
        for(int i=0;i< len_data.length;i++)
        {
            if(len_data[i] == Command.END)
            {
                array.add(Command.ESC);
                array.add((byte)0xdc);
            }
            else if (len_data[i] == Command.ESC)
            {
                array.add(Command.ESC);
                array.add((byte)0xdd);
            }
            else
            {
                array.add(len_data[i]);
            }
        }
        //data
        if(utils.byteToShort(len_data) > 0) {
            for (int i = 0; i < data.length; i++) {
                if (data[i] == Command.END) {
                    array.add(Command.ESC);
                    array.add((byte) 0xdc);
                } else if (data[i] == Command.ESC) {
                    array.add(Command.ESC);
                    array.add((byte) 0xdd);
                } else {
                    array.add(data[i]);
                }
            }
        }
        //lrc
        if(lrc == Command.END)
        {
            array.add(Command.ESC);
            array.add((byte)0xdc);
        }
        else if (lrc == Command.ESC)
        {
            array.add(Command.ESC);
            array.add((byte)0xdd);
        }
        else
        {
            array.add(lrc);
        }

        array.add(Command.END);

        byte[] ret = new byte[array.size()];
        for(int i=0;i< ret.length;i++)
        {
            ret[i] = array.get(i);
        }

        return ret;
    }

    public BluetoothGattServer mgatt;
    public BluetoothGattCharacteristic mcharacter;
    public BluetoothDevice mdevice;

    public boolean send_response(BluetoothGattServer gatt, BluetoothGattCharacteristic characteristic, BluetoothDevice device, byte[] value)
    {
        int len = value.length;
        int i=0;
        int j=0;
        byte[] tmp;

        if(gatt == null || characteristic == null || device == null)
            return false;
        mgatt = gatt;
        mcharacter = characteristic;
        mdevice = device;

        if(len <= 20)
        {
            mcharacter.setValue(value);
            mgatt.notifyCharacteristicChanged(mdevice, mcharacter, false);
        }
        else
        {
            for(i = 0; i< len/20; i++)
            {
                for (j = 0; j < 20; j++) {
                    tmp = new byte[20];
                    tmp[j] = value[20*i + j];

                    mcharacter.setValue(tmp);
                    mhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mgatt.notifyCharacteristicChanged(mdevice, mcharacter, false);
                        }
                    }, 100);
                }
            }
            for(j=0; j< len%20; j++)
            {
                tmp = new byte[len%20];
                tmp[j] = value[20*(len/20) + j];

                mcharacter.setValue(tmp);
                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mgatt.notifyCharacteristicChanged(mdevice, mcharacter, false);
                    }
                }, 100);

            }
        }

        return true;
    }

    public void set_mode(byte mode)
    {
        this.mode = mode;
    }

    public byte get_mode()
    {
        return this.mode;
    }

    public void set_ssc(short ssc)
    {
        byte[] tmp = utils.shortToByte(ssc);
        System.arraycopy(tmp, 0, this.ssc, 0, 2);
    }

    public void set_ssc(byte[] data)
    {
        System.arraycopy(data, 0, this.ssc, 0, 2);
    }

    public byte[] get_ssc_byte()
    {
        return this.ssc;
    }

    public short get_ssc_short()
    {
        return utils.byteToShort(this.ssc);
    }

    public void set_status(byte[] status)
    {
        System.arraycopy(status, 0, this.ssc, 0, 2);
    }

    public byte[] get_status()
    {
        return this.status;
    }

    public void set_data_length(short length)
    {
        byte[] tmp = utils.shortToByte(length);
        System.arraycopy(tmp, 0, this.len_data, 0, 2);
    }

    public void set_data_length(byte[] length)
    {
        System.arraycopy(length, 0, this.len_data, 0, 2);
    }

    public byte[] get_data_length_byte()
    {
        return this.len_data;
    }

    public short get_data_length_short()
    {
        return utils.byteToShort(this.len_data);
    }

    public void set_data(byte[] value)
    {
        this.data = value;
    }

    public void generate_lrc()
    {
        this.lrc = (byte)(this.status[1]^this.status[0]^this.ssc[1]^this.ssc[0]^this.reserved^this.mode);
    }

    public byte[] get_atr()
    {
        byte[] tmp = new byte[atr.length];

        for(int i=0;i< tmp.length; i++)
        {
            tmp[i] = (byte)atr[i];
        }

        return tmp;
    }



}
