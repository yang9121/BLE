package com.yang.bletest;

import java.util.Arrays;

/**
 * Created by Yang_Mstarc on 2017/8/31.
 */

public class Command {

    public static final int mode_offset = 0;
    public static final int res_offset = 1;
    public static final int ssc_offset = 2;
    public static final int cmd_offset = 4;
    public static final int len_offset = 6;
    public static final int apdu_offset = 8;
    public int lrc_offset = 0;

    public static final byte END = (byte) 0xC0;
    public static final byte ESC = (byte) 0xDB;

    public byte mode = 0;
    public byte reserved = 0;
    public byte[] ssc = new byte[2];
    public byte[] command = new byte[2];
    public byte[] len_cmd = new byte[2];
    public byte[] apdu = new byte[300];
    public byte lrc = 0;

    //mode
    public static byte BTC_CONTACT = 0x01;
    //cmd
    public static byte[] BTC_INFO = new byte[]{0x00, 0x01};
    public static byte[] BTC_UNIT = new byte[]{0x00, 0x03};
    public static byte[] BTC_DATA = new byte[]{0x00, 0x04};
    public static byte[] BTC_AUTH = new byte[]{0x00, 0x05};
    public static byte[] BTC_DISCONNECT = new byte[]{0x01, 0x01};
    public static byte[] BTC_CONNECT = new byte[]{0x01, 0x02};
    public static byte[] BTC_ATR = new byte[]{0x01, 0x03};
    public static byte[] BTC_APDU = new byte[]{0x01, 0x04};
    public static byte[] BTC_PPS = new byte[]{0x01, 0x05};


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Command command = (Command) o;

        if (BTC_CONTACT != command.BTC_CONTACT) return false;
        if (!Arrays.equals(BTC_INFO, command.BTC_INFO)) return false;
        if (!Arrays.equals(BTC_UNIT, command.BTC_UNIT)) return false;
        if (!Arrays.equals(BTC_DATA, command.BTC_DATA)) return false;
        if (!Arrays.equals(BTC_AUTH, command.BTC_AUTH)) return false;
        if (!Arrays.equals(BTC_DISCONNECT, command.BTC_DISCONNECT)) return false;
        if (!Arrays.equals(BTC_CONNECT, command.BTC_CONNECT)) return false;
        if (!Arrays.equals(BTC_ATR, command.BTC_ATR)) return false;
        if (!Arrays.equals(BTC_APDU, command.BTC_APDU)) return false;
        return Arrays.equals(BTC_PPS, command.BTC_PPS);

    }

    @Override
    public int hashCode() {
        int result = (int) BTC_CONTACT;
        result = 31 * result + Arrays.hashCode(BTC_INFO);
        result = 31 * result + Arrays.hashCode(BTC_UNIT);
        result = 31 * result + Arrays.hashCode(BTC_DATA);
        result = 31 * result + Arrays.hashCode(BTC_AUTH);
        result = 31 * result + Arrays.hashCode(BTC_DISCONNECT);
        result = 31 * result + Arrays.hashCode(BTC_CONNECT);
        result = 31 * result + Arrays.hashCode(BTC_ATR);
        result = 31 * result + Arrays.hashCode(BTC_APDU);
        result = 31 * result + Arrays.hashCode(BTC_PPS);
        return result;
    }
}
