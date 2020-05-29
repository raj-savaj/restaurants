package com.demos.param.ashtrestoant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import com.demos.param.ashtrestoant.Adapter.Auto_Item_Adapter;
import com.demos.param.ashtrestoant.Adapter.Order_adapter;
import com.demos.param.ashtrestoant.DB.DaoSession;
import com.demos.param.ashtrestoant.DB.productListDao;
import com.demos.param.ashtrestoant.Model.TableProduct;
import com.demos.param.ashtrestoant.DB.productList;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ProcessOrder extends AppCompatActivity {

    CoordinatorLayout ordersnack;
    AutoCompleteTextView autoCompleteTextView;
    Auto_Item_Adapter adapter;
    List<productList> productLists=null;

    List<TableProduct> plist;
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewAdapter;
    String tableid;

    DaoSession daoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_order);
        ordersnack=(CoordinatorLayout)findViewById(R.id.ordersnack);

        tableid=getIntent().getExtras().getString("tableid");
        getSupportActionBar().setTitle(getIntent().getExtras().getString("name"));
        plist=new ArrayList<TableProduct>();
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);

        daoSession = ((App) getApplication()).getDaoSession();
        productListDao plistdao=daoSession.getProductListDao();
        productLists=plistdao.loadAll();
        adapter = new Auto_Item_Adapter(getApplicationContext(), productLists);
        autoCompleteTextView.setAdapter(adapter);


        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                productList l1=(productList)adapterView.getAdapter().getItem(i);
                TableProduct tmp=new TableProduct(0,l1.getId(),l1.getName(),l1.getPrice(),l1.getImage(),0,l1.getFd_code());
                plist.add(0,tmp);
                recyclerViewAdapter.notifyItemInserted(0);
                recyclerView.smoothScrollToPosition(0);
            }
        });
        tablelock();
        recyclerView = findViewById(R.id.rcv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter=new Order_adapter(plist,this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }


    public void makeorder(View view) {
        List<String> qty=new ArrayList<String>();
        List<String> pid=new ArrayList<String>();
        if(plist.size() > 0)
        {
            final ProgressDialog dialog = new ProgressDialog(ProcessOrder.this);
            dialog.setMessage("Sending To Kitchen ...");
            dialog.show();
           for( TableProduct t:plist)
           {
               if(t.getQty()!=0)
               {
                   qty.add(String.valueOf(t.getQty()));
                   pid.add(String.valueOf(t.getId()));
               }
           }
            Session s=new Session(getApplicationContext());
            Retrofit retrofit=new Retrofit.Builder()
                    .baseUrl(s.getIp())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            Api api=retrofit.create(Api.class);

            Call<String> call=api.saveOrder(qty.toArray(new  String[qty.size()]),pid.toArray(new String[pid.size()]),tableid);
            call.enqueue(new Callback<String>() {

                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    dialog.dismiss();
                    Intent i=new Intent(ProcessOrder.this,Productview.class);
                    startActivity(i);
                    finish();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    dialog.dismiss();
                    Snackbar snackbar = Snackbar
                            .make(ordersnack, "No internet connection!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });

        }
    }


    private void tablelock() {
        Session s=new Session(getApplicationContext());

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(s.getIp())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        Api api=retrofit.create(Api.class);

        Call<String> call=api.tableLock(tableid);
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
              //  Log.e("ready",response.body().toString());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), "No internet connection!", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

    }

    public void FinishOrder(View view) {
        new SweetAlertDialog(ProcessOrder.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText("Make Order After Finish")
                .show();
    }
}
