package com.demos.param.ashtrestoant;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class Session {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE = 0;

    public Session(Context context) {
        this.context = context;
        this.context = context;
        pref = context.getSharedPreferences("AVRESTO", PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setIpAddress(String ipAddress){
        editor.putString("IP",ipAddress);
        editor.putString("API_URL",ipAddress+"/public/api/");
        editor.putString("IMG_URL",ipAddress+"/public/upload/");
        editor.commit();
    }

    public String getBaseIp()
    {
        return pref.getString("IP","http://kuberpublicity.com/rresto/");
    }

    public String getIp()
    {
        return pref.getString("API_URL","http://kuberpublicity.com/rresto/public/api/");
    }
    public String getImg()
    {
        return pref.getString("IMG_URL","http://kuberpublicity.com/rresto/public/upload/");
    }

    public void setPrinterIp(String ipAddress){
        editor.putString("PRINT_IP",ipAddress);
        editor.commit();
    }

    public String getPrinterIp()
    {
        return pref.getString("PRINT_IP","192.168.225.118");
    }
}
