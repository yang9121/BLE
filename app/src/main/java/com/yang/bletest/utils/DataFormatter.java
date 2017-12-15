package com.yang.bletest.utils;

import android.util.Log;

import com.yang.bletest.Command;

import java.util.ArrayList;

/**
 * Created by Yang_Mstarc on 2017/8/31.
 */

public class DataFormatter {

    //public ArrayList<Byte> rcv_value = new ArrayList<>();
    public String TAG = "yhy";


    public DataFormatter() {
/*
        byte[] test = new byte[]{(byte)0xC0, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                                0x09, (byte)0xdb, (byte)0xdd, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, (byte)0xc0};
        byte[] result;

        result = this.perse(test);

        Log.i(TAG, "perse result = " + utils.bytesToHexString(result));
*/
    }

    public static byte[] perse(byte[] value)
    {
        ArrayList<Byte> rcv_value = new ArrayList<>();
        byte[] data;
        int j = 0;
        int k = 0;

        if(value[0] == Command.END)
        {
            k = 1;
        }
        else
        {
            k = 0;
        }

        if(value[value.length-1] == Command.END)
        {
            j = value.length-1;
        }
        else
        {
            j = value.length;
        }

        for(int i=k; i< j; i++)
        {
            if(value[i] == Command.ESC && value[i+1] == (byte) 0xDC)
            {
                rcv_value.add(Command.END);
                i++;
            }
            else if(value[i] == Command.ESC && value[i+1] == (byte) 0xDD)
            {
                rcv_value.add(Command.ESC);
                i++;
            }
            else
            {
                rcv_value.add(value[i]);
            }
        }

        data = new byte[rcv_value.size()];
        for(int i = 0; i<data.length;i++)
        {
            data[i] = rcv_value.get(i);
        }

        return data;
    }

    public static void format(ArrayList<Byte> array, Command cmd)
    {
        byte[] tmp = new byte[array.size()];

        for(int i=0; i<array.size(); i++)
        {
            tmp[i] = array.get(i);
        }

        format(tmp, cmd);

    }

    public static void format(byte[] data, Command cmd)
    {
        if(data == null || (data.length < 10))
            return;
        cmd.mode = data[Command.mode_offset];
        cmd.reserved = data[Command.res_offset];
        System.arraycopy(data, Command.ssc_offset, cmd.ssc, 0, cmd.ssc.length);
        System.arraycopy(data, Command.cmd_offset, cmd.command, 0, cmd.command.length);
        System.arraycopy(data, Command.len_offset, cmd.len_cmd, 0, cmd.len_cmd.length);

        System.arraycopy(data, Command.apdu_offset, cmd.apdu, 0, (data.length-Command.apdu_offset-1));
        cmd.lrc = data[data.length - 1];
    }

}
