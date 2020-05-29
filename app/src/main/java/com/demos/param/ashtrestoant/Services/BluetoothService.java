package com.demos.param.ashtrestoant.Services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothService {
    /* renamed from: D */
    private static final boolean f0D = true;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String NAME = "BTPrinter";
    private static final String TAG = "BluetoothService";
    private AcceptThread mAcceptThread;
    private final BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private final Handler mHandler;
    private int mState = 0;
    private String data;
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = BluetoothService.this.mAdapter.listenUsingRfcommWithServiceRecord(BluetoothService.NAME, BluetoothService.MY_UUID);
            } catch (IOException e) {
                Log.e(BluetoothService.TAG, "listen() failed", e);
            }
            this.mmServerSocket = tmp;
        }

        public void run() {
            Log.d("AcceptThread线程运行", "正在运行......");
            Log.d(BluetoothService.TAG, "BEGIN mAcceptThread" + this);
            setName("AcceptThread");
            while (BluetoothService.this.mState != 3) {

            }
            Log.i(BluetoothService.TAG, "END mAcceptThread");
            return;
        }

        public void cancel() {
            Log.d(BluetoothService.TAG, "cancel " + this);
            try {
                this.mmServerSocket.close();
            } catch (IOException e) {
                Log.e(BluetoothService.TAG, "close() of server failed", e);
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothDevice mmDevice;
        private final BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device) {
            this.mmDevice = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(BluetoothService.MY_UUID);
            } catch (IOException e) {
                Log.e(BluetoothService.TAG, "create() failed", e);
            }
            this.mmSocket = tmp;
        }

        public void run() {
            Log.i(BluetoothService.TAG, "BEGIN mConnectThread");
            setName("ConnectThread");
            BluetoothService.this.mAdapter.cancelDiscovery();
            try {
                this.mmSocket.connect();
                synchronized (BluetoothService.this) {
                    BluetoothService.this.mConnectThread = null;
                }
                BluetoothService.this.connected(this.mmSocket, this.mmDevice);
            } catch (IOException e) {
                BluetoothService.this.connectionFailed();
                try {
                    this.mmSocket.close();
                } catch (IOException e2) {
                    Log.e(BluetoothService.TAG, "unable to close() socket during connection failure", e2);
                }
                BluetoothService.this.start();
            }
        }

        public void cancel() {
            try {
                this.mmSocket.close();
            } catch (IOException e) {
                Log.e(BluetoothService.TAG, "close() of connect socket failed", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private final BluetoothSocket mmSocket;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(BluetoothService.TAG, "create ConnectedThread");
            this.mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
                DataOutputStream dow=new DataOutputStream(tmpOut);
                byte[] mFormat=new byte[]{27, 33, 0};
                String title = "\n      Akshar Restaurant   \n";
                mFormat[2]=((byte) (0x12 | mFormat[2]));
                dow.write(mFormat);
                mFormat[2]=((byte) (0x8 | mFormat[2]));
                dow.write(mFormat);
                dow.write(title.getBytes("GBK"));
                mFormat[2]=0;
                dow.write(mFormat);
                dow.write(data.getBytes("GBK"));
            } catch (Exception e) {
                Log.e(BluetoothService.TAG, "temp sockets not created", e);
            }
            this.mmInStream = tmpIn;
            this.mmOutStream = tmpOut;
        }

        public void run() {
            Log.d("ConnectedThread线程运行", "正在运行......");
            Log.i(BluetoothService.TAG, "BEGIN mConnectedThread");
            while (true) {
                try {
                    byte[] buffer = new byte[256];
                    int bytes = this.mmInStream.read(buffer);
                    if (bytes <= 0) {
                        break;
                    }
                    BluetoothService.this.mHandler.obtainMessage(2, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e(BluetoothService.TAG, "disconnected", e);
                    BluetoothService.this.connectionLost();
                    if (BluetoothService.this.mState != 0) {
                        BluetoothService.this.start();
                        return;
                    }
                    return;
                }
            }
            Log.e(BluetoothService.TAG, "disconnected");
            BluetoothService.this.connectionLost();
            if (BluetoothService.this.mState != 0) {
                Log.e(BluetoothService.TAG, "disconnected");
                BluetoothService.this.start();
            }
        }

        public void cancel() {
            try {
                this.mmSocket.close();
            } catch (IOException e) {
                Log.e(BluetoothService.TAG, "close() of connect socket failed", e);
            }
        }
    }

    public BluetoothService(Context context, Handler handler) {
        this.mHandler = handler;
    }

    public synchronized boolean isAvailable() {
        boolean z;
        if (this.mAdapter == null) {
            z = false;
        } else {
            z = true;
        }
        return z;
    }

    public synchronized boolean isBTopen() {
        boolean z;
        if (this.mAdapter.isEnabled()) {
            z = true;
        } else {
            z = false;
        }
        return z;
    }

    public synchronized BluetoothDevice getDevByMac(String mac) {
        return this.mAdapter.getRemoteDevice(mac);
    }

    private synchronized void setState(int state) {
        this.mState = state;
        this.mHandler.obtainMessage(1, state, -1).sendToTarget();
    }

    public synchronized void start() {
        Log.d(TAG, "start");
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        if (this.mAcceptThread == null) {
            this.mAcceptThread = new AcceptThread();
            this.mAcceptThread.start();
        }
        setState(1);
    }

    public synchronized void connect(BluetoothDevice device, String data) {
        Log.d(TAG, "connect to: " + device);
        this.data=data;
        if (this.mState == 2 && this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        this.mConnectThread = new ConnectThread(device);
        this.mConnectThread.start();
        setState(2);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        Log.d(TAG, "connected");
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        if (this.mAcceptThread != null) {
            this.mAcceptThread.cancel();
            this.mAcceptThread = null;
        }
        this.mConnectedThread = new ConnectedThread(socket);
        this.mConnectedThread.start();
        this.mHandler.sendMessage(this.mHandler.obtainMessage(4));
        setState(3);
    }

    public synchronized void stop() {
        Log.d(TAG, "stop");
        setState(0);
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        if (this.mAcceptThread != null) {
            this.mAcceptThread.cancel();
            this.mAcceptThread = null;
        }
    }

    private void connectionFailed() {
        setState(1);
        this.mHandler.sendMessage(this.mHandler.obtainMessage(6));
    }

    private void connectionLost() {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(5));
    }
}
