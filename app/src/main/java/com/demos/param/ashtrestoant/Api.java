package com.demos.param.ashtrestoant;

import java.util.ArrayList;
import java.util.List;

import com.demos.param.ashtrestoant.Model.RunningTab;
import com.demos.param.ashtrestoant.Model.Table;
import com.demos.param.ashtrestoant.Model.TableProduct;
import com.demos.param.ashtrestoant.DB.productList;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by root on 3/4/18.
 */

public interface Api {

    @FormUrlEncoded
    @POST("login")
    Call<String> Login(@Field("Ltype") String username, @Field("Password") String password);

    @GET("seat")
    Call<List<Table>> seat();

    @FormUrlEncoded
    @POST("ceckstate")
    Call<String> ceckstate(@Field("tbid") String tableid);

    @GET("getProduct")
    Call<ArrayList<productList>> getProduct();

    @FormUrlEncoded
    @POST("saveOrder")
    Call<String> saveOrder(@Field("qty[]") String[] qty,@Field("pid[]") String[] pid,@Field("tableid") String tableid);

    @FormUrlEncoded
    @POST("tableLock")
    Call<String> tableLock(@Field("tableid") String tableid);

    @FormUrlEncoded
    @POST("getSortProduct")
    Call<ArrayList<productList>> getSortProduct(@Field("tableid") String tableid);

    @FormUrlEncoded
    @POST("TableProduct")
    Call<List<TableProduct>> tableproduct(@Field("tableid") String tableid);

    @FormUrlEncoded
    @POST("updateOrder")
    Call<String> updateOrder(@Field("oid[]") String[] oids,@Field("qty[]") String[] qty,@Field("pid[]") String[] pid,@Field("tableid") String tableid);

    @FormUrlEncoded
    @POST("finishOrder")
    Call<String> finishOrder(@Field("oid[]") String[] pid,@Field("tableid") String tableid,@Field("uoid") String oid);

    @FormUrlEncoded
    @POST("RunningTable")
    Call<List<RunningTab>> RunningTable(@Field("tableid") String tableid);

    @FormUrlEncoded
    @POST("cancelItem")
    Call<String> cancelProduct(@Field("oid") int oid,@Field("serve") int sqty);

    @FormUrlEncoded
    @POST("changeItem")
    Call<String> RunningItem(@Field("oid[]") String[] oid,@Field("qty[]") String[] qty);

    @FormUrlEncoded
    @POST("freeTable")
    Call<String> freeTable(@Field("tbid") String tableid);

    @GET("parselitem")
    Call<List<TableProduct>> parselitem();

    @GET("parselid")
    Call<String> parselid();

    @FormUrlEncoded
    @POST("sendParsel")
    Call<String> sendParsel(@Field("pid[]") List<String> pid,@Field("qty[]") List<String> qty,@Field("name") String name);
}
