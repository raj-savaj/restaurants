package com.demos.param.ashtrestoant.DB;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Order {

    @SerializedName("oid")
    private String oid;
    @SerializedName("tbno")
    private int tbno;
    @SerializedName("tbname")
    private String tbname;
    @SerializedName("fd_id")
    private int id;
    @SerializedName("fd_name")
    private String name;
    @SerializedName("fd_Price")
    private int price;
    @SerializedName("Qty")
    private int qty=0;
    @SerializedName("fd_code")
    private String fd_code;
    @SerializedName("time")
    private String time;
    @Generated(hash = 459006451)
    public Order(String oid, int tbno, String tbname, int id, String name,
            int price, int qty, String fd_code, String time) {
        this.oid = oid;
        this.tbno = tbno;
        this.tbname = tbname;
        this.id = id;
        this.name = name;
        this.price = price;
        this.qty = qty;
        this.fd_code = fd_code;
        this.time = time;
    }
    @Generated(hash = 1105174599)
    public Order() {
    }

    public Order(String oid,String tbname,int price,String time)
    {
        this.oid=oid;
        this.tbname=tbname;
        this.price=price;
        this.time=time;
    }

    public String getOid() {
        return this.oid;
    }
    public void setOid(String oid) {
        this.oid = oid;
    }
    public int getTbno() {
        return this.tbno;
    }
    public void setTbno(int tbno) {
        this.tbno = tbno;
    }
    public String getTbname() {
        return this.tbname;
    }
    public void setTbname(String tbname) {
        this.tbname = tbname;
    }
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getPrice() {
        return this.price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public int getQty() {
        return this.qty;
    }
    public void setQty(int qty) {
        this.qty = qty;
    }
    public String getFd_code() {
        return this.fd_code;
    }
    public void setFd_code(String fd_code) {
        this.fd_code = fd_code;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }

}
