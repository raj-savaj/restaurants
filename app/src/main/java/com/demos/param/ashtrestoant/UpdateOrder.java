package com.demos.param.ashtrestoant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.demos.param.ashtrestoant.Adapter.Auto_Item_Adapter;
import com.demos.param.ashtrestoant.Adapter.Update_Order_Adapter;
import com.demos.param.ashtrestoant.DB.DaoSession;
import com.demos.param.ashtrestoant.DB.Order;
import com.demos.param.ashtrestoant.DB.OrderDao;
import com.demos.param.ashtrestoant.DB.productListDao;
import com.demos.param.ashtrestoant.Services.WifiCommunication;

import com.demos.param.ashtrestoant.Model.TableProduct;
import com.demos.param.ashtrestoant.DB.productList;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class UpdateOrder extends AppCompatActivity {

    String BILL = "";
    CoordinatorLayout updateosnack;
    AutoCompleteTextView acupdate;
    Auto_Item_Adapter adapter;
    List<productList> productLists=null;
    String tableid,tablename,ouid;

    List<TableProduct> plist;
    List<Order> orderList;
    int pcount=0;
    RecyclerView rcvupdate;
    RecyclerView.Adapter recyclerViewAdapter;
    Session s;

    UpdateOrder.revMsgThread revThred = null;
    WifiCommunication wfComm = null;

    DaoSession daoSession;
    OrderDao orderDao;
    int connFlag=0;
    String[] bill=new String[3];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_order);
        s=new Session(getApplicationContext());
        updateosnack=(CoordinatorLayout)findViewById(R.id.updateosnack);
        tableid=getIntent().getExtras().getString("tableid");
        tablename=getIntent().getExtras().getString("name");
        getSupportActionBar().setTitle(tablename);
        acupdate = (AutoCompleteTextView) findViewById(R.id.acupdate);

        daoSession = ((App) getApplication()).getDaoSession();
        productListDao plistdao=daoSession.getProductListDao();
        orderDao=daoSession.getOrderDao();
        orderList=new ArrayList<>();

        productLists=plistdao.loadAll();
        adapter = new Auto_Item_Adapter(getApplicationContext(), productLists);
        acupdate.setAdapter(adapter);

        acupdate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                productList l1=(productList)adapterView.getAdapter().getItem(i);
                TableProduct tmp=new TableProduct(0,l1.getId(),l1.getName(),l1.getPrice(),l1.getImage(),0,l1.getFd_code());
                plist.add(0,tmp);
                recyclerViewAdapter.notifyDataSetChanged();
                rcvupdate.smoothScrollToPosition(0);
            }
        });

        rcvupdate = findViewById(R.id.urcv);
        rcvupdate.setLayoutManager(new LinearLayoutManager(this));
        TableData(tableid);
    }


    private void TableData(String tid) {
        final ProgressDialog dialog = new ProgressDialog(UpdateOrder.this);
        dialog.setMessage("Getting Order...");
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(s.getIp())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Api api = retrofit.create(Api.class);

        Call<List<TableProduct>> call = api.tableproduct(tid);
        call.enqueue(new Callback<List<TableProduct>>() {
            @Override
            public void onResponse(Call<List<TableProduct>> call, Response<List<TableProduct>> response) {
                dialog.dismiss();
                plist=response.body();
                pcount=plist.size();
                recyclerViewAdapter=new Update_Order_Adapter(plist,UpdateOrder.this);
                rcvupdate.setAdapter(recyclerViewAdapter);
            }

            @Override
            public void onFailure(Call<List<TableProduct>> call, Throwable t) {
                dialog.dismiss();
                Snackbar snackbar = Snackbar
                        .make(updateosnack, "No internet connection!", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    public void makeorder(View view) {
        List<String> qty=new ArrayList<String>();
        List<String> pid=new ArrayList<String>();
        List<String> oid=new ArrayList<String>();
        if(plist.size() > 0)
        {
            final ProgressDialog dialog = new ProgressDialog(UpdateOrder.this);
            dialog.setMessage("Sending To Kitchen...");
            dialog.show();
            for( TableProduct t:plist)
            {
                if(t.getQty()!=0)
                {
                    qty.add(String.valueOf(t.getQty()));
                    pid.add(String.valueOf(t.getId()));
                    oid.add(String.valueOf(t.getOid()));
                }
            }
            Retrofit retrofit=new Retrofit.Builder()
                    .baseUrl(s.getIp())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            Api api=retrofit.create(Api.class);

            Call<String> call=api.updateOrder(oid.toArray(new String[oid.size()]),qty.toArray(new  String[qty.size()]),pid.toArray(new String[pid.size()]),tableid);
            call.enqueue(new Callback<String>() {

                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    dialog.dismiss();
                    Intent i=new Intent(UpdateOrder.this,Productview.class);
                    startActivity(i);
                    finish();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    dialog.dismiss();
                    Snackbar snackbar = Snackbar
                            .make(updateosnack, "No internet connection!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });
        }
    }

    public void FinishOrder(View view) {
        if(pcount==plist.size()) {

            new SweetAlertDialog(UpdateOrder.this,SweetAlertDialog.WARNING_TYPE).setTitleText("Are you sure?")
                    .setContentText("Order Will Be Finished!")
                    .setCancelText("cancel")
                    .setConfirmText("Confirm")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(final SweetAlertDialog sDialog) {
                            sDialog.cancel();
                            final ProgressDialog dialog = new ProgressDialog(UpdateOrder.this);
                            dialog.setMessage("Finishing Order ...");
                            dialog.show();

                            final Date d=new Date();
                            final DateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
                            DateFormat dateFormatdb = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            ouid=String.valueOf(dateFormat.format(d));
                            bill[0]=getIntent().getExtras().getString("name");
                            List<String> oid=new ArrayList<String>();
                            int total=0;

                            for( TableProduct t:plist)
                            {
                                if(t.getQty()!=0)
                                {
                                    Order order=new Order(ouid,Integer.parseInt(tableid),tablename,t.getId(),t.getName(),t.getPrice(),t.getQty(),t.getFd_code(),String.valueOf(dateFormatdb.format(d)));
                                    oid.add(String.valueOf(t.getOid()));
                                    BILL = BILL + String.format("%-35s %-7s %-7s %-7s\n", ""+t.getName()+"", ""+t.getQty()+"", ""+t.getPrice()+"", ""+t.getQty()*t.getPrice()+"");
                                    total+=t.getQty()*t.getPrice();
                                    orderList.add(order);
                                }
                            }
                            orderDao.insertInTx(orderList);
                            bill[1]=BILL;
                            bill[2]=String.valueOf(total);
                            wfComm = new WifiCommunication(mHandler);

                            Retrofit retrofit=new Retrofit.Builder()
                                    .baseUrl(s.getIp())
                                    .addConverterFactory(ScalarsConverterFactory.create())
                                    .build();
                            Api api=retrofit.create(Api.class);

                            Call<String> call=api.finishOrder(oid.toArray(new String[oid.size()]),tableid,ouid);
                            call.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    dialog.dismiss();
                                    wfComm.initSocket(s.getPrinterIp(), 9100,bill);
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Snackbar snackbar = Snackbar
                                            .make(updateosnack, "No internet connection!", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                            });

                        }
                    })
                    .show();
        }
        else
        {
            new SweetAlertDialog(UpdateOrder.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Make Order After Finish")
                    .show();
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    connFlag = 0;
                    revThred = new UpdateOrder.revMsgThread();
                    revThred.start();
                    Intent i=new Intent(UpdateOrder.this,Productview.class);
                    startActivity(i);
                    finish();
                    return;
                case 1:
                    Toast.makeText(UpdateOrder.this, "Disconnect the WIFI-printer successful", Toast.LENGTH_SHORT).show();
                    revThred.interrupt();
                    return;
                case 2:
                    connFlag = 0;
                    Toast.makeText(UpdateOrder.this, "Connect the WIFI-printer error", Toast.LENGTH_SHORT).show();
                    return;
                case 4:
                    connFlag = 0;
                    Toast.makeText(UpdateOrder.this, "Send Data Failed,please reconnect", Toast.LENGTH_SHORT).show();
                    revThred.interrupt();
                    return;
                case 6:
                    if (((((byte) Integer.parseInt(msg.obj.toString())) >> 6) & 1) == 1) {
                        Toast.makeText(UpdateOrder.this, "The printer has no paper", Toast.LENGTH_LONG).show();
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    };

    class revMsgThread extends Thread {
        revMsgThread() {
        }

        public void run() {
            try {
                Message msg = new Message();
                while (true) {
                    int revData = wfComm.revByte();
                    if (revData != -1) {
                        msg = mHandler.obtainMessage(6);
                        msg.obj = Integer.valueOf(revData);
                        mHandler.sendMessage(msg);
                    }
                    Thread.sleep(20);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e("wifi调试","eror"+e.getMessage());
            }
        }
    }
}
