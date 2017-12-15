package com.yang.bletest;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pools;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.os.Handler;

import com.yang.bletest.utils.utils;

/**
 * Created by Yang_Mstarc on 2017/8/22.
 */

public class central extends Activity {

    final String TAG = "yhy";
    public ResultAdapter mResultAdapter;
    public BluetoothAdapter mBluetoothAdapter;
    public BluetoothLeScanner mlescanner;
    public BluetoothGattCharacteristic mWriteCharacter;
    public BluetoothGattCharacteristic mIndicateCharacter;
    public ReentrantLock lock;

    final int MSG_BLE_SEND = 10;
    final int MSG_BLE_WRITE = 11;

    Handler mHandler;
    HandlerThread handlerThread;
    private boolean mScanning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.central);
        handlerThread = new HandlerThread("yhy");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper())
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what)
                {
                    case MSG_BLE_SEND:
                    {
                        //Log.i(TAG, "MSG_BLE_SEND");
                        if(mWriteCharacter != null) {
                            byte[] tmp = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09
                                    , 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13
                                    , 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d
                                    , 0x1e, 0x1f, 0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27
                                    , 0x28, 0x29, 0x2a, 0x2b, 0x2c, 0x2d, 0x2e, 0x2f, 0x30, 0x31
                                    , 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x3b
                                    , 0x3c, 0x3d, 0x3e, 0x3f, 0x40, 0x0f, 0x10, 0x11, 0x12, 0x13
                                    , 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13
                                    , 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13
                                    , 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13
                                    , 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13
                                    , 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13
                                    , 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13
                                    , 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13
                                    , 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13
                                    , 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13
                                    , 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13
                                    , 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13
                                    , 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13
                                    , 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13
                                    , 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13
                                    , 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x6f
                                    , 0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79
                                    , 0x7a, 0x7b, 0x7c, 0x7e, 0x7d, 0x7e, 0x7f};

                            complated_send = false;

                            //lock.lock();

                            byte[] value = new byte[20];
                            int len = tmp.length;
                            if(len>20) {
                                System.arraycopy(tmp, 0, value, 0, 20);
                                mWriteCharacter.setValue(value);
                                for (int j = 1; j < len/20;j++)
                                {
                                    //value = new byte[20];
                                    System.arraycopy(tmp, 20*j, value, 0, 20);
                                    //lock.lock();
                                    //mWriteCharacter.setValue(value);
                                    mgatt.writeCharacteristic(mWriteCharacter);
                                    synchronized(handlerThread) {
                                        try {
                                            mWriteCharacter.setValue(value);
                                            //Log.i(TAG, "lock " + j);
                                            handlerThread.wait();
                                        } catch (Exception e) {
                                            Log.e(TAG, "Exception:" + e.toString());
                                        }
                                    }
                                }
                                if(len%20 != 0) {
                                    value = new byte[len % 20];
                                    System.arraycopy(tmp, 20*(len/20), value, 0, value.length);
                                    //lock.lock();
                                    mWriteCharacter.setValue(value);
                                    mgatt.writeCharacteristic(mWriteCharacter);
                                    synchronized(handlerThread) {
                                        try {
                                            handlerThread.wait();
                                        } catch (Exception e) {
                                            Log.e(TAG, "Exception:" + e.toString());
                                        }
                                    }
                                }
                                complated_send = true;
                                mgatt.readCharacteristic(mIndicateCharacter);
                            }
                            else
                            {
                                //lock.lock();
                                mWriteCharacter.setValue(tmp);
                                mgatt.writeCharacteristic(mWriteCharacter);
                                complated_send = true;
                                synchronized(handlerThread) {
                                    try {
                                        handlerThread.wait();
                                    } catch (Exception e) {
                                        Log.e(TAG, "Exception:" + e.toString());
                                    }
                                }
                                mgatt.readCharacteristic(mIndicateCharacter);
                            }
                        }

                        break;
                    }
                    case MSG_BLE_WRITE:
                    {
                        Log.i(TAG, "MSG_BLE_WRITE");

                        break;
                    }
                }
            }
        };

        if(!init_ble())
            finish();

        init_view();

    }

    public boolean init_ble()
    {
        Log.i(TAG, "init ble");
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE NOT SUPPORTED!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "BLUETOOTH NOT SUPPORTED!", Toast.LENGTH_SHORT).show();

            return false;
        }

        mlescanner = mBluetoothAdapter.getBluetoothLeScanner();
        if(mlescanner == null)
        {
            Toast.makeText(this, "can not get le scanner", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public BluetoothDevice mdevice;
    public BluetoothGatt mgatt;
    public TextView scan_result;
    public TextView data_rcv;
    public EditText data_snd;
    public Button btn_send;
    public Button scan;
    public StringBuilder mstr = new StringBuilder();

    public void init_view()
    {
        Log.i(TAG, "init view");
        scan = (Button)findViewById(R.id.start_scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mScanning)
                    checkPermissions();
                else
                    scanLeDevice(false);
            }
        });

        ListView devices = (ListView)findViewById(R.id.list2);
        mResultAdapter = new ResultAdapter(this);
        devices.setAdapter(mResultAdapter);
        devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(mScanning)
                    scanLeDevice(false);

                mdevice = mResultAdapter.getItem(position).getDevice();
                //mResultAdapter.clear();
                //mResultAdapter.notifyDataSetChanged();
                setContentView(getLayoutInflater().inflate(R.layout.scan_result, null));
                scan_result = (TextView)findViewById(R.id.device_detail);

                scan_result.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setContentView(getLayoutInflater().inflate(R.layout.send_rcv, null));
                        data_rcv = (TextView) findViewById(R.id.data_rcv);
                        data_rcv.setMovementMethod(new ScrollingMovementMethod());
                        data_snd = (EditText) findViewById(R.id.data_snd);
                        btn_send = (Button)findViewById(R.id.btn_send);
                        btn_send.setText("connect");
                        btn_send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!mconnected && !mconnecting) {
                                    btn_send.setText("connecting");
                                    mgatt = mdevice.connectGatt(central.this, false, mGattCallback);
                                    //mdevice.createBond();
                                    mgatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
                                }
                                else
                                {
                                    Message msg = mHandler.obtainMessage();
                                    msg.what = MSG_BLE_SEND;
                                    mHandler.sendMessage(msg);
                                }
                            }
                        });
                    }
                });

                scan_result.setText(utils.bytesToHexString(mResultAdapter.getItem(position).getScanRecord().getBytes()) + "\n");

                scan_result.append("mac = " + mdevice.getAddress() + "\n");
                scan_result.append("name = "+ mdevice.getName() + "\n");

                if(mResultAdapter.getItem(position).getScanRecord().getServiceUuids() != null)
                    scan_result.append("uuid = " + mResultAdapter.getItem(position).getScanRecord().getServiceUuids().get(0).toString() + "\n");

                //mdevice.connectGatt()


            }
        });
    }

    private List<ScanFilter> mfilter_list = new ArrayList<>();


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    //mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mlescanner.stopScan(mLeScanCallback);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scan.setText("start");
                        }
                    });
                    invalidateOptionsMenu();
                }
            }, 60*1000);

            mScanning = true;
            //mBluetoothAdapter.startLeScan(mLeScanCallback);
            //mfilter_list.add(new ScanFilter.Builder().setDeviceName("UPWEAR666").build());
            mlescanner.startScan(/*mfilter_list, new ScanSettings.Builder().build(), */mLeScanCallback);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    scan.setText("Scanning...");
                }
            });
        } else {
            mScanning = false;
            //mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mlescanner.stopScan(mLeScanCallback);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    scan.setText("scan");
                }
            });
        }
        invalidateOptionsMenu();
    }

    private ArrayList<BluetoothDevice> device_list = new ArrayList<>();

    private ScanCallback mLeScanCallback =
            new ScanCallback() {

                @Override
                public void onScanResult(int callbackType, final ScanResult result) {
                    super.onScanResult(callbackType, result);

                    Log.i(TAG, "scan result:" + utils.bytesToHexString(result.getScanRecord().getBytes()));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!device_list.contains(result.getDevice())) {
                                device_list.add(result.getDevice());
                                mResultAdapter.addResult(result);
                                mResultAdapter.notifyDataSetChanged();
                            }
                        }
                    });

                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                    Log.e(TAG, "scan failed");
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                }

            };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mScanning)
            scanLeDevice(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class ResultAdapter extends BaseAdapter {

        private Context context;
        private List<ScanResult> scanResultList;

        ResultAdapter(Context context) {
            this.context = context;
            scanResultList = new ArrayList<>();
        }

        void addResult(ScanResult result) {
            scanResultList.add(result);
        }

        void clear() {
            scanResultList.clear();
        }

        @Override
        public int getCount() {
            return scanResultList.size();
        }

        @Override
        public ScanResult getItem(int position) {
            if (position > scanResultList.size())
                return null;
            return scanResultList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ResultAdapter.ViewHolder holder;
            if (convertView != null) {
                holder = (ResultAdapter.ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(context, R.layout.adapter_scan_result, null);
                holder = new ResultAdapter.ViewHolder();
                convertView.setTag(holder);
                holder.txt_name = (TextView) convertView.findViewById(R.id.txt_name);
                holder.txt_mac = (TextView) convertView.findViewById(R.id.txt_mac);
                holder.txt_rssi = (TextView) convertView.findViewById(R.id.txt_rssi);
            }

            holder.txt_name.setText(scanResultList.get(position).getDevice().getName());
            holder.txt_mac.setText(scanResultList.get(position).getDevice().getAddress());
            holder.txt_rssi.setText(scanResultList.get(position).getRssi() +" ");

            return convertView;
        }

        class ViewHolder {
            TextView txt_name;
            TextView txt_mac;
            TextView txt_rssi;
        }
    }

    public boolean mconnected = false;
    public boolean mconnecting = false;

    public boolean complated_send = false;

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mgatt.discoverServices();
                mconnecting = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_send.setText("connecting");
                    }
                });

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mconnected = false;
                mconnecting = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_send.setText("connect");
                    }
                });

            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                for(BluetoothGattService i : gatt.getServices())
                {
                    Log.i(TAG, "service: " + i.getUuid().toString());
                    if(i.getUuid().toString().equalsIgnoreCase(peripheral.SERVICE_UUID))
                    {
                        Log.i(TAG, "fond target service");
                        mconnected = true;
                        mWriteCharacter = i.getCharacteristic(UUID.fromString(peripheral.WRITE_UUID));
                        mIndicateCharacter = i.getCharacteristic(UUID.fromString(peripheral.INDICATE_UUID));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btn_send.setText("send");
                            }
                        });

                        break;
                    }
                }

                //Log.i(TAG, "Discover Service: " + gatt.getServices().get(0).getUuid().toString());

            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {

            Log.i(TAG, "on characteristic read: " + characteristic.getUuid().toString());
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "receive data: " + utils.bytesToHexString(characteristic.getValue()));

                mstr.append("Received: \n");
                mstr.append(utils.bytesToHexString(characteristic.getValue()) + "\n");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        data_rcv.setText(mstr.toString());
                    }
                });
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

            Log.i(TAG, "on characteristic write: " + characteristic.getUuid().toString());

            /*Message msg = mHandler.obtainMessage();
            msg.what = MSG_BLE_WRITE;
            mHandler.sendMessage(msg);*/
            synchronized (handlerThread) {
                try {
                    handlerThread.notify();
                    //Log.i(TAG, "notify");
                } catch (Exception e) {
                    Log.e(TAG, "Exception:" + e.toString());
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

            Log.i(TAG, "on characteristic changed:" + characteristic.getUuid().toString());

            if(characteristic.getUuid().toString().equalsIgnoreCase(peripheral.INDICATE_UUID))
            {
                Log.i(TAG, "receive data: " + utils.bytesToHexString(characteristic.getValue()));
                data_rcv.setText(utils.bytesToHexString(characteristic.getValue()));
            }

        }
    };



    @Override
    public final void onRequestPermissionsResult(int requestCode,
                                                 @NonNull String[] permissions,
                                                 @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 12:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            onPermissionGranted(permissions[i]);
                        }
                    }
                }
                break;
        }
    }

    private void checkPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, 12);
        }
    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                scanLeDevice(true);
                break;
        }
    }



}
