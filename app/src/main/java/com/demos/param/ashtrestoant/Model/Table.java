package com.demos.param.ashtrestoant.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 4/4/18.
 */

public class Table {
    @SerializedName("stid")
    private int stid;
    @SerializedName("tbname")
    private String tbname;
    @SerializedName("tbseat")
    private String tbseat;
    @SerializedName("status")
    private int status;


    public Table(int stid, String tbname, String tbseat, int status) {
        this.stid = stid;
        this.tbname = tbname;
        this.tbseat = tbseat;
        this.status = status;
    }

    public int getStid() {
        return stid;
    }

    public String getTbname() {
        return tbname;
    }

    public String getTbseat() {
        return tbseat;
    }

    public int getStatus() {
        return status;
    }
}
