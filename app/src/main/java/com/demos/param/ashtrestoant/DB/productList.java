package com.demos.param.ashtrestoant.DB;

import com.google.gson.annotations.SerializedName;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class productList {
    @SerializedName("fd_id")
    private int id;
    @SerializedName("fd_name")
    private String name;
    @SerializedName("fd_Price")
    private int price;
    @SerializedName("fd_image")
    private String image;
    @SerializedName("fd_code")
    private String fd_code;

    private String qty;
    public productList(int id, String name, int price, String image,String fd_code) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        qty="";
        this.fd_code=fd_code;
    }

    @Generated(hash = 737275406)
    public productList(int id, String name, int price, String image, String fd_code,
            String qty) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.fd_code = fd_code;
        this.qty = qty;
    }

    @Generated(hash = 933920063)
    public productList() {
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

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getFd_code() {
        return fd_code;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setFd_code(String fd_code) {
        this.fd_code = fd_code;
    }
}

