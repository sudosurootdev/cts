/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.cts.verifier.bluetooth;

import java.util.UUID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

public class BleAdvertiserService extends Service {

    public static final boolean DEBUG = true;
    public static final String TAG = "BleAdvertiseService";

    public static final int COMMAND_START_ADVERTISE = 0;
    public static final int COMMAND_STOP_ADVERTISE = 1;
    public static final int COMMAND_START_POWER_LEVEL = 2;
    public static final int COMMAND_STOP_POWER_LEVEL = 3;

    public static final String BLE_START_ADVERTISE =
            "com.android.cts.verifier.bluetooth.BLE_START_ADVERTISE";
    public static final String BLE_STOP_ADVERTISE =
            "com.android.cts.verifier.bluetooth.BLE_STOP_ADVERTISE";
    public static final String BLE_START_POWER_LEVEL =
            "com.android.cts.verifier.bluetooth.BLE_START_POWER_LEVEL";
    public static final String BLE_STOP_POWER_LEVEL =
            "com.android.cts.verifier.bluetooth.BLE_STOP_POWER_LEVEL";

    public static final String EXTRA_COMMAND =
            "com.android.cts.verifier.bluetooth.EXTRA_COMMAND";

    private static final UUID SERVICE_UUID =
            UUID.fromString("00009999-0000-1000-8000-00805f9b34fb");
    public static final byte MANUFACTURER_TEST_ID = (byte)0x07;
    public static final int PRIVACY_MAC_UUID = 0x9999;
    public static final int POWER_LEVEL_UUID = 0x8888;
    public static final byte[] PRIVACY_MAC_DATA = new byte[]{(byte)0x99, (byte)0x99, 3, 1, 4};
    public static final byte[] POWER_LEVEL_DATA = new byte[]{(byte)0x88, (byte)0x88, 15, 0};
    public static final byte[] POWER_LEVEL_MASK = new byte[]{1, 1, 1, 0};

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeAdvertiser mAdvertiser;
    private BluetoothGattServer mGattServer;
    private AdvertiseCallback mCallback;
    private Handler mHandler;

    private int[] mPowerLevel;
    private Map<Integer, AdvertiseCallback> mPowerCallback;

    @Override
    public void onCreate() {
        super.onCreate();

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        mGattServer = mBluetoothManager.openGattServer(getApplicationContext(),
            new BluetoothGattServerCallback() {});
        mCallback = new BLEAdvertiseCallback();
        mHandler = new Handler();

        mPowerLevel = new int[]{
            AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW,
            AdvertiseSettings.ADVERTISE_TX_POWER_LOW,
            AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM,
            AdvertiseSettings.ADVERTISE_TX_POWER_HIGH};
        mPowerCallback = new HashMap<Integer, AdvertiseCallback>();
        for (int x : mPowerLevel) {
            mPowerCallback.put(x, new BLEAdvertiseCallback());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) handleIntent(intent);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdvertiser.stopAdvertising(mCallback);
    }

    private void handleIntent(Intent intent) {
        int command = intent.getIntExtra(EXTRA_COMMAND, -1);

        switch (command) {
            case COMMAND_START_ADVERTISE:
                List<ParcelUuid> serviceUuid = new ArrayList<ParcelUuid>();
                serviceUuid.add(new ParcelUuid(SERVICE_UUID));
                AdvertiseData data = new AdvertiseData.Builder()
                    .setManufacturerData(MANUFACTURER_TEST_ID, new byte[]{MANUFACTURER_TEST_ID, 0})
                    .setServiceData(new ParcelUuid(SERVICE_UUID), PRIVACY_MAC_DATA)
                    .build();
                AdvertiseSettings setting = new AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                    .setIsConnectable(false)
                    .build();
                mAdvertiser.startAdvertising(setting, data, mCallback);
                sendBroadcast(new Intent(BLE_START_ADVERTISE));
                break;
            case COMMAND_STOP_ADVERTISE:
                mAdvertiser.stopAdvertising(mCallback);
                sendBroadcast(new Intent(BLE_STOP_ADVERTISE));
                break;
            case COMMAND_START_POWER_LEVEL:
                for (int t : mPowerLevel) {
                    AdvertiseData d = new AdvertiseData.Builder()
                        .setManufacturerData(MANUFACTURER_TEST_ID,
                            new byte[]{MANUFACTURER_TEST_ID, 0})
                        .setServiceData(new ParcelUuid(SERVICE_UUID),
                            new byte[]{(byte)0x88, (byte)0x88, 15, (byte)t})
                        .setIncludeTxPowerLevel(true)
                        .build();
                    AdvertiseSettings settings = new AdvertiseSettings.Builder()
                        .setTxPowerLevel(t)
                        .build();
                    mAdvertiser.startAdvertising(settings, d, mPowerCallback.get(t));
                }
                sendBroadcast(new Intent(BLE_START_POWER_LEVEL));
                break;
            case COMMAND_STOP_POWER_LEVEL:
                for (int t : mPowerLevel) {
                    mAdvertiser.stopAdvertising(mPowerCallback.get(t));
                }
                sendBroadcast(new Intent(BLE_STOP_POWER_LEVEL));
                break;
            default:
                showMessage("Unrecognized command: " + command);
                break;
        }
    }

    private void showMessage(final String msg) {
        mHandler.post(new Runnable() {
            public void run() {
                Toast.makeText(BleAdvertiserService.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class BLEAdvertiseCallback extends AdvertiseCallback {
        @Override
        public void onStartFailure(int errorCode) {
            Log.e(TAG, "fail. Error code: " + errorCode);
        }

        @Override
        public void onStartSuccess(AdvertiseSettings setting) {
            if (DEBUG) Log.d(TAG, "success.");
        }
    }
}