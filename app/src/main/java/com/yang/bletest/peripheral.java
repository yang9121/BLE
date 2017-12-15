package com.yang.bletest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.ParcelUuid;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.yang.bletest.utils.ApduUtil;
import com.yang.bletest.utils.DataFormatter;
import com.yang.bletest.utils.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.LogRecord;

import static com.yang.bletest.Command.BTC_INFO;

/**
 * Created by Yang_Mstarc on 2017/8/22.
 */

public class peripheral extends Activity {

    final String TAG = "yhy";

    public Context mContext;
    public BluetoothManager mBluetoothManager;
    public BluetoothAdapter mBluetoothAdapter;
    public BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    public PeriServerCallBack mGattServerCallback;
    public BluetoothGattServer mGattServer;
    public BluetoothGattCharacteristic mWriteCharacter;
    public BluetoothGattCharacteristic mIndicateCharacter;
    public BluetoothGattService mGattService;
    public BluetoothDevice mClientDevice;
    public BluetoothGatt mgatt;

    static final String SERVICE_UUID = "48EB9001-F352-5FA0-9B06-8FCAA22602CF";
    static final String WRITE_UUID = "48EB9002-F352-5FA0-9B06-8FCAA22602CF";
    static final String INDICATE_UUID = "8EB9003-F352-5FA0-9B06-8FCAA22602CF";

    TextView receive_data;

    public StringBuilder mstr = new StringBuilder();
    public ArrayList<Byte> mReceived = new ArrayList<>();
    public boolean is_receiving = false;
    public boolean receive_done = false;
    public Command mcmd = new Command();
    public Handler mHandler;
    public ApduUtil mnfc;
    public Response mResponse;
    public boolean in_conversition = false;
    public byte crc;

    public final int MSG_CMD_AVAILABLE = 10;
    public final int MSG_BTC_INFO = 11;
    public final int MSG_BTC_UNIT = 12;
    public final int MSG_BTC_DATA = 13;
    public final int MSG_BTC_AUTH = 14;
    public final int MSG_BTC_DISCONNECT = 15;
    public final int MSG_BTC_CONNECT = 16;
    public final int MSG_BTC_ATR = 17;
    public final int MSG_BTC_APDU = 18;
    public final int MSG_BTC_PPS = 19;
    Message cmd_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = peripheral.this;
        setContentView(R.layout.peripheral);

        receive_data = (TextView)findViewById(R.id.receive);
        receive_data.setMovementMethod(new ScrollingMovementMethod());

        mnfc = new ApduUtil(peripheral.this);
        mResponse = new Response();

        HandlerThread mthread = new HandlerThread("yhy_thread");
        mthread.start();

        mHandler = new Handler(mthread.getLooper())
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what)
                {
                    case MSG_CMD_AVAILABLE:
                    {
                        crc = (byte)(mcmd.command[1]^mcmd.command[0]^mcmd.ssc[1]^mcmd.ssc[0]^mcmd.reserved^mcmd.mode);
                        if(mcmd.lrc != crc)
                        {
                            Log.e(TAG, "crc error: crc = " + crc + "mcmd.lrc = " + mcmd.lrc);
                            return;
                        }
                        if (Command.BTC_CONTACT != mcmd.mode)
                        {
                            Log.e(TAG, "mode error: " + mcmd.mode);
                            return;
                        }

                        cmd_msg = mHandler.obtainMessage();

                        if (Arrays.equals(Command.BTC_INFO, mcmd.command))
                        {
                            cmd_msg.what = MSG_BTC_INFO;
                        }
                        else if (Arrays.equals(Command.BTC_UNIT, mcmd.command))
                        {
                            cmd_msg.what = MSG_BTC_UNIT;
                        }
                        else if (Arrays.equals(Command.BTC_DATA, mcmd.command))
                        {
                            cmd_msg.what = MSG_BTC_DATA;
                        }
                        else if (Arrays.equals(Command.BTC_AUTH, mcmd.command))
                        {
                            cmd_msg.what = MSG_BTC_AUTH;
                        }
                        else if (Arrays.equals(Command.BTC_DISCONNECT, mcmd.command))
                        {
                            cmd_msg.what = MSG_BTC_DISCONNECT;
                        }
                        else if (Arrays.equals(Command.BTC_CONNECT, mcmd.command))
                        {
                            cmd_msg.what = MSG_BTC_CONNECT;
                        }
                        else if (Arrays.equals(Command.BTC_ATR, mcmd.command))
                        {
                            cmd_msg.what = MSG_BTC_ATR;
                        }
                        else if (Arrays.equals(Command.BTC_APDU, mcmd.command))
                        {
                            cmd_msg.what = MSG_BTC_APDU;
                        }
                        else if (Arrays.equals(Command.BTC_PPS, mcmd.command))
                        {
                            cmd_msg.what = MSG_BTC_PPS;
                        }

                        mHandler.sendMessage(cmd_msg);

                        break;
                    }
                    case MSG_BTC_INFO:
                        break;
                    case MSG_BTC_UNIT:
                        break;
                    case MSG_BTC_DATA:
                        break;
                    case MSG_BTC_AUTH: {
                        mResponse.set_ssc((short)0);
                        mResponse.set_status(Response.BTC_IO_OK);
                        mResponse.set_data_length((short)0);
                        mResponse.send_response(mGattServer, mIndicateCharacter, mClientDevice, mResponse.make_response());
                        break;
                    }
                    case MSG_BTC_DISCONNECT: {
                        in_conversition = false;
                        mnfc.power_off();
                        break;
                    }
                    case MSG_BTC_CONNECT: {
                        in_conversition = true;
                        mnfc.init_nfc();
                        mResponse.atr = mnfc.reset_nfc();
                        mResponse.set_mode(Command.BTC_CONTACT);
                        mResponse.set_ssc((short)1);
                        mResponse.set_status(Response.BTC_IO_OK);
                        mResponse.set_data_length((short)2);
                        mResponse.set_data(mResponse.ssc);
                        mResponse.send_response(mGattServer, mIndicateCharacter, mClientDevice, mResponse.make_response());
                        break;
                    }
                    case MSG_BTC_ATR: {
                        short ssc = utils.byteToShort(mcmd.ssc);
                        if(ssc == mResponse.get_ssc_short() + 1) {
                            mResponse.set_ssc(ssc);
                            mResponse.set_status(Response.BTC_IO_OK);
                        }
                        else {
                            mResponse.set_status(Response.BTC_SSC_ERROR);
                        }
                        mResponse.set_data_length((short)mResponse.atr.length);
                        mResponse.set_data(mResponse.get_atr());
                        mResponse.send_response(mGattServer, mIndicateCharacter, mClientDevice, mResponse.make_response());
                        break;
                    }
                    case MSG_BTC_APDU:
                        break;
                    case MSG_BTC_PPS:
                        break;
                    default:
                        break;
                }
            }
        };

        if(init_ble())
        {
            create_service();
            startAdvertising();
        }
    }

    private boolean init_ble()
    {
        // 是否支持ble
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(mContext, "hasSystemFeature == false", Toast.LENGTH_SHORT).show();
            return false;
        }
        // 是否能获取到蓝牙服务
        mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBluetoothManager == null) {
            Toast.makeText(mContext, "mBluetoothManager == null", Toast.LENGTH_LONG).show();
            return false;
        }
        // 获取蓝牙适配器
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(mContext, "BluetoothAdapter == null", Toast.LENGTH_LONG).show();
            return false;
        }
        // 蓝牙是否打开
        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(mContext, "BluetoothAdapter.isEnabled == false", Toast.LENGTH_LONG).show();
            return false;
        }
        // 获取广播者
        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        if (mBluetoothLeAdvertiser == null) {
            Toast.makeText(mContext, "BluetoothLeAdvertiser == null ", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;

    }

    private void create_service()
    {
        // 给个风骚的广播名称，默认是手机设置里蓝牙的名称
        mBluetoothAdapter.setName("UPWEAR666");
        // 这个Callback 是设备广播成功后所有状态的回调，包括读写等
        mGattServerCallback = new PeriServerCallBack();
        // 打开GattServer
        mGattServer = mBluetoothManager.openGattServer(mContext, mGattServerCallback);
        // 创建一个特征通道用来写
        mWriteCharacter = new BluetoothGattCharacteristic(
                UUID.fromString(WRITE_UUID),
                BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_WRITE | BluetoothGattCharacteristic.PERMISSION_READ);
        // 创建一个特征通道用来读
        mIndicateCharacter = new BluetoothGattCharacteristic(
                UUID.fromString(INDICATE_UUID),
                BluetoothGattCharacteristic.PROPERTY_INDICATE | BluetoothGattCharacteristic.PROPERTY_NOTIFY | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_WRITE | BluetoothGattCharacteristic.PERMISSION_READ);
        // 创建一个Gatt服务
        mGattService = new BluetoothGattService(
                UUID.fromString(SERVICE_UUID),
                BluetoothGattService.SERVICE_TYPE_PRIMARY);
        // 添加读写通道
        mGattService.addCharacteristic(mWriteCharacter);
        mGattService.addCharacteristic(mIndicateCharacter);
        // 添加服务
        if (mGattServer != null && mGattService != null)
            mGattServer.addService(mGattService);



    }

    public void startAdvertising() {
        // 这里的Callback是是否开启成功的回调
        mBluetoothLeAdvertiser.startAdvertising(createAdvSettings(true, 0), createAdvertiseData(), mAdvCallback);

    }
    private AdvertiseSettings createAdvSettings(boolean connectable, int timeoutMillis) {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        //设置广播的模式,跟功耗相关
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        builder.setConnectable(connectable);
        builder.setTimeout(timeoutMillis);
        return builder.build();
    }

    //设置广播数据（可以携带广播数据，这里没有携带）
    private AdvertiseData createAdvertiseData() {
        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        builder.setIncludeDeviceName(true);
        //builder.addServiceUuid(ParcelUuid.fromString(SERVICE_UUID));
        //builder.addManufacturerData(0x1122, new byte[]{0x00, 0x01, 0x02, 0x03});
        return builder.build();
    }

    //发送广播的回调
    private AdvertiseCallback mAdvCallback = new AdvertiseCallback() {
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            //mOnCallBackListener.advertisingStatus(true);
            if (settingsInEffect != null) {
                Log.e(TAG, "onStartSuccess TxPowerLv=" + settingsInEffect.getTxPowerLevel()
                        + " mode=" + settingsInEffect.getMode() + " timeout=" + settingsInEffect.getTimeout());
            } else {
                Log.e(TAG, "onStartSuccess, settingInEffect is null");
            }
        }

        public void onStartFailure(int errorCode) {
            //mOnCallBackListener.advertisingStatus(false);
            Log.e(TAG, "onStartFailure errorCode=" + errorCode);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBluetoothLeAdvertiser.stopAdvertising(mAdvCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class PeriServerCallBack extends BluetoothGattServerCallback {

        //当添加一个GattService成功后会回调改接口。
        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "onServiceAdded status=GATT_SUCCESS service=" + service.getUuid().toString());
            } else {
                Log.e(TAG, "onServiceAdded status!=GATT_SUCCESS");
            }
        }

        //BLE连接状态改变后回调的接口
        @Override
        public void onConnectionStateChange(android.bluetooth.BluetoothDevice device, int status, int newState) {
            mClientDevice = device;
            Log.d(TAG, "BLE连接状态改变 status=" + status + "->" + newState + " ==== Address: " + device.getAddress());

            //device.setPin(new byte[] {0x01, 0x02, 0x03, 0x04, 0x05, 0x06});

        }

        //当有客户端来读数据时回调的接口
        @Override
        public void onCharacteristicReadRequest(android.bluetooth.BluetoothDevice device,
                                                int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            mClientDevice = device;
            Log.e(TAG, "客户端读数据 requestId=" + requestId + " offset=" + offset);

            byte[] response = new byte[] {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
                    0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19,
                    0x29,0x30,0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38,0x39,0x40};

/**************************************************************************/
            mIndicateCharacter.setValue(response);
            mGattServer.notifyCharacteristicChanged(device, mIndicateCharacter, false);
/**************************************************************************/
            mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, response);
        }

        //当有客户端来写数据时回调的接口
        @Override
        public void onCharacteristicWriteRequest(android.bluetooth.BluetoothDevice device, int requestId,
                                                 BluetoothGattCharacteristic characteristic, boolean preparedWrite,
                                                 boolean responseNeeded, int offset, byte[] value) {
            mClientDevice = device;
            byte[] tmp;

            if(responseNeeded)
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);

            try {
                //String msg = new String(value, "UTF-8");
                //Log.i(TAG, "data = " + utils.bytesToHexString(value));
                String msg = utils.bytesToHexString(value);
                //mOnCallBackListener.writeRequest(msg);
                Log.e(TAG, "客户端写数据 + message= " + msg + " requestId= " + requestId + " offset= " + offset);
                mstr.append("Received: \n" + "data = " + msg + " id = " + requestId + " offset = " + offset + "\n");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        receive_data.setText(mstr.toString());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
/*
            if(value[0] == Command.END)
            {
                if(is_receiving) {
                    mReceived.clear();
                }
                else {
                    is_receiving = true;
                    tmp = DataFormatter.perse(value);
                    for (byte m : tmp) {
                        mReceived.add(m);
                    }
                }
            }
            else
            {
                if(is_receiving)
                {
                    tmp = DataFormatter.perse(value);
                    for(byte m : tmp)
                    {
                        mReceived.add(m);
                    }
                }
            }

            if(value[value.length - 1] == Command.END)
            {
                if(is_receiving) {
                    receive_done = true;
                    is_receiving = false;
                    DataFormatter.format(mReceived, mcmd);
                    Message msg = mHandler.obtainMessage();
                    msg.what = MSG_CMD_AVAILABLE;
                    mHandler.sendMessage(msg);
                    mReceived.clear();
                }
            }
*/

        }
        //当有客户端来写Descriptor时回调的接口
        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor,
                                             boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            mClientDevice = device;
            Log.e(TAG, "onDescriptorWriteRequest === ");
            // now tell the connected device that this was all successfull
            mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
        }
    }

}
