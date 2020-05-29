package com.demos.param.ashtrestoant.Services;


import android.os.Handler;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WifiCommunication {

    final byte[] ChooseFontA = {27, 77, 0};
    final byte[] ChooseFontB = {27, 77, 1};
    final byte[] ChooseFontC = {27, 77, 48};
    final byte[] ChooseFontD = {27, 77, 49};

    private static Socket client = null;
    private static InputStream inStream = null;
    private static OutputStream out = null;
    private String AddressIp = null;
    private ConnectThread mConnection = null;
    private final Handler mHandler;
    private int port = 0;
    private String[] BILL;

    private class ConnectThread extends Thread {

        ByteArrayOutputStream or;
        DataOutputStream dow;
        private ConnectThread() {
        }

        ConnectThread(WifiCommunication wifiCommunication, ConnectThread connectThread) {
            this();
        }

        public void run() {
            try {
                WifiCommunication.client = new Socket();
                WifiCommunication.client.connect(new InetSocketAddress(InetAddress.getByName(AddressIp), port), 3000);

                if (WifiCommunication.client != null) {
                    WifiCommunication.out = WifiCommunication.client.getOutputStream();
                    WifiCommunication.inStream = WifiCommunication.client.getInputStream();

                    or=new ByteArrayOutputStream();
                    dow=new DataOutputStream(WifiCommunication.out);

                    byte[] mFormat=new byte[]{27, 33, 0};
                    StringBold("Akshar Restaurant\n");

                    String he="1st Floor, Sahajanand Market, Station Road,\n" +
                            " Opposite IDBI Bank , Amreli \n" +
                            " Bharatbhai  9152581380 \n" +
                            "-------------------------------------------------------------\n";
                    StringNormal(he);

                    StringNormalBold(BILL[0]+"\n");

                    Date d=new Date();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String date=String.valueOf(dateFormat.format(d));
                    NormalBold(date+"\n-------------------------------------------------------------\n");

                    String headbill = String.format("%-25s %-5s %-5s %-7s", "Item", "Qty", "Rate", "Total");
                    StringNormalBold(headbill);
                    NormalBold("\n-------------------------------------------------------------\n");
                    SNormal(BILL[1]);

                    NormalBold("____________________________________________________________\n");
                    StringNormalBold(String.format("%-37s %-5s", "Total Amount",BILL[2]));
                    NormalBold("\n____________________________________________________________\n");
                    String foot="\nThanks For Visit\n";
                    foot +="Akshar Restaurant";;
                    SNormal(foot);
                    NormalBold("\n");
                    NormalBold("\n");
                    NormalBold("\n\n");
                    cut();
                }
                if (WifiCommunication.client != null && WifiCommunication.out != null && WifiCommunication.inStream != null) {
                    WifiCommunication.this.mHandler.sendMessage(WifiCommunication.this.mHandler.obtainMessage(0));
                }
            } catch (Exception e) {
                Log.e("Error",e.getMessage());
                WifiCommunication.this.mHandler.sendMessage(WifiCommunication.this.mHandler.obtainMessage(2));
            }
        }
        public void cut()
        {
            try {
                or.write(10);
                or.write(new byte[]{(byte) 27, (byte) 109});
                dow.write(or.toByteArray());
            }catch(Exception  ee)
            {

            }
        }

        public void StringBold(String str)
        {
            try {
                byte[] mFormat=new byte[]{27, 33, 0};
                or.write(mFormat);
                or.write(new byte[]{0x1B, 'a', 0x01});
                mFormat[2]=((byte) (0x12 | mFormat[2]));
                or.write(mFormat);
                mFormat[2]=((byte) (0x8 | mFormat[2]));
                or.write(mFormat);
                or.write(str.getBytes("GB18030"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void StringNormalBold(String str)
        {
            try {
                byte[] mFormat=new byte[]{27, 33, 0};
                or.write(mFormat);
                or.write(new byte[]{0x1B, 'a', 0x01});
                mFormat[2]=((byte) (0x10 | mFormat[2]));
                or.write(mFormat);
                mFormat[2]=((byte) (0x10 | mFormat[2]));
                or.write(mFormat);
                or.write(str.getBytes("GB18030"));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void StringNormal(String str)
        {
            try {
                byte[] mFormat=new byte[]{27, 33, 0x01};
                or.write(mFormat);
                or.write(str.getBytes("GB18030"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void NormalBold(String str)
        {
            try {
                byte[] mFormat=new byte[]{0x1B, 33, 0x01};
                or.write(mFormat);
                or.write(str.getBytes("GB18030"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void SNormal(String str)
        {
            try {
                byte[] mFormat=new byte[]{27, 33, 0};
                or.write(mFormat);
                mFormat[2]=((byte) (0x10 | mFormat[2]));
                or.write(mFormat);
                mFormat[2]=((byte) (0x7 | mFormat[2]));
                or.write(mFormat);
                or.write(str.getBytes("GB18030"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void HeadBold(String str)
        {
            try {
                byte[] mFormat=new byte[]{27, 33, 0x01};
                or.write(mFormat);
                or.write(mFormat);
                or.write(new byte[]{0x1B, 'a', 0x01});
                or.write(str.getBytes("GB18030"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public WifiCommunication(Handler handler) {
        this.mHandler = handler;
    }

    public void initSocket(String AddressIp, int port, String[] BILL) {
        this.AddressIp = AddressIp;
        this.port = port;
        this.BILL=BILL;
        if (this.mConnection != null) {
            this.mConnection = null;
        }
        if (this.mConnection == null) {
            this.mConnection = new ConnectThread(this, null);
            this.mConnection.start();
        }
    }

    public void sendMsg(String sndMsg, String charset) {
        if (sndMsg != null) {
            byte[] send;
            try {
                send = sndMsg.getBytes(charset);
            } catch (UnsupportedEncodingException e) {
                send = sndMsg.getBytes();
            }
            try {
                if (client.isConnected() && !client.isOutputShutdown()) {
                    out.write(send);
                    out.flush();
                }
            } catch (IOException e2) {
                this.mHandler.sendMessage(this.mHandler.obtainMessage(4));
                Log.e("WIFI-printer", e2.toString());
            }
        }
    }

    public void sndByte(byte[] send) {
        if (send != null) {
            try {
                if (client.isConnected() && !client.isOutputShutdown()) {
                    out.write(send);
                    out.flush();
                }
            } catch (IOException e) {
                Log.d("WIFI-printer", e.toString());
                this.mHandler.sendMessage(this.mHandler.obtainMessage(4));
            }
        }
    }

    public void close() {
        try {
            if (out != null) {
                out.close();
            }
            if (inStream != null) {
                inStream.close();
            }
            if (client != null) {
                client.close();
                out = null;
                inStream = null;
                client = null;
                this.mHandler.sendMessage(this.mHandler.obtainMessage(1));
            }
        } catch (IOException e) {
            Log.d("WIFI-printer", e.toString());
        }
    }

    public byte[] revMsg() {
        try {
            byte[] revData = new byte[1024];
            inStream.read(revData);
            return revData;
        } catch (Exception e) {
            Log.d("WIFI-printer", e.toString());
            return null;
        }
    }

    public int revByte() {
        try {
            return inStream.read();
        } catch (Exception e) {
            Log.d("WIFI-printer", e.toString());
            return -1;
        }
    }

    public String bytesToString(byte[] b) {
        String str = null;
        try {
            return new String(b, "UTF-8").trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return str;
        }
    }
}
