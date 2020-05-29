package com.demos.param.ashtrestoant.Model;

import com.google.gson.annotations.SerializedName;

public class RunningTab {

    @SerializedName("Id")
    private int oid;
    @SerializedName("fd_name")
    private String name;
    @SerializedName("fd_Price")
    private int price;
    @SerializedName("fd_image")
    private String image;
    @SerializedName("Qty")
    private int qty;
    @SerializedName("serve")
    private int serve;
    public RunningTab(int oid, String name, int price, String image,int qty,int serve) {
        this.oid=oid;
        this.name = name;
        this.price = price;
        this.image = image;
        this.qty=qty;
        this.serve=serve;
    }

    public String getName() {
        return name;
    }
    public int getPrice() {
        return price;
    }
    public String getImage() {
        return image;
    }

    public int getQty() {
        return qty;
    }
    public int getOid() {
        return oid;
    }
    public void setQty(int qty) {
        this.qty = qty;
    }
    public int getServe() {
        return serve;
    }

}
