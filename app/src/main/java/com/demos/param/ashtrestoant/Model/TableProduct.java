package com.demos.param.ashtrestoant.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 11/4/18.
 */

public class TableProduct {

    @SerializedName("Id")
    private int oid;
    @SerializedName("fd_id")
    private int id;
    @SerializedName("fd_name")
    private String name;
    @SerializedName("fd_Price")
    private int price;
    @SerializedName("fd_image")
    private String image;
    @SerializedName("Qty")
    private int qty=0;
    @SerializedName("fd_code")
    private String fd_code;

    private boolean selected=false;
    public TableProduct(int oid,int id, String name, int price, String image,int qty,String fd_code) {
        this.oid=oid;
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.qty=qty;
        this.fd_code=fd_code;
    }

    public int getId() { return id; }
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
    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getFd_code() {
        return fd_code;
    }
}
