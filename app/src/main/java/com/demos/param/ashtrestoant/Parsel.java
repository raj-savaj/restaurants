package com.demos.param.ashtrestoant;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.demos.param.ashtrestoant.Adapter.Auto_Item_Adapter;
import com.demos.param.ashtrestoant.Adapter.ParselAdapter;
import com.demos.param.ashtrestoant.DB.DaoSession;
import com.demos.param.ashtrestoant.DB.OrderDao;
import com.demos.param.ashtrestoant.DB.productList;
import com.demos.param.ashtrestoant.DB.productListDao;
import com.demos.param.ashtrestoant.Services.WifiCommunication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.demos.param.ashtrestoant.Model.TableProduct;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Parsel extends AppCompatActivity {

    Session s;
    RecyclerView rcv;
    List<TableProduct> productList;
    ParselAdapter parselAdapter;
    Toolbar toolbar;
    public  TextView txt_tot,txt_name,txt_select;
    ImageView img_print;
    public  Map<Integer,TableProduct> productMap;
    List<String> qty=new ArrayList<String>();
    List<String> pid=new ArrayList<String>();
    String BILL = "";
    String[] bill=new String[3];
    //Wifi
    Parsel.revMsgThread revThred = null;
    WifiCommunication wfComm = null;
    int connFlag=0;

    AutoCompleteTextView acupdate;
    Auto_Item_Adapter adapter;
    List<productList> productLists=null;
    DaoSession daoSession;
    OrderDao orderDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parsel);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txt_tot=findViewById(R.id.total);
        txt_name=findViewById(R.id.pr_name);
        txt_select=findViewById(R.id.selected);
        img_print=findViewById(R.id.print);
        productMap=new HashMap<>();
        setSupportActionBar(toolbar);
        s=new Session(getApplicationContext());
        rcv=findViewById(R.id.rcv);
        rcv.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));

        acupdate = (AutoCompleteTextView) findViewById(R.id.acupdate);

        daoSession = ((App) getApplication()).getDaoSession();
        productListDao plistdao=daoSession.getProductListDao();
        productLists=plistdao.loadAll();
        adapter = new Auto_Item_Adapter(getApplicationContext(), productLists);
        acupdate.setAdapter(adapter);

        getParselid();
        getProduct();
        img_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Size",""+productMap.size());
                if(productMap.size()>0)
                {
                    new SweetAlertDialog(Parsel.this,SweetAlertDialog.WARNING_TYPE).setTitleText("Order Confirm ?")
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
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.cancel();
                                    Print();
                                }
                            }).show();
                }
            }
        });

        acupdate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                productList l1=(productList)adapterView.getAdapter().getItem(i);
                TableProduct tmp=new TableProduct(0,l1.getId(),l1.getName(),l1.getPrice(),l1.getImage(),0,l1.getFd_code());
                productList.add(tmp);
                parselAdapter.notifyDataSetChanged();
            }
        });

    }
    public void Print()
    {
        int total=0;
        bill[0]=txt_name.getText().toString();
        for (int count=0;count<productMap.size();count++)
        {
            TableProduct t=productMap.get(productMap.keySet().toArray()[count]);
            if(t.getQty()!=0)
            {
                BILL = BILL + String.format("%-35s %-7s %-7s %-7s\n", ""+t.getName()+"", ""+t.getQty()+"", ""+t.getPrice()+"", ""+t.getQty()*t.getPrice()+"");
                qty.add(String.valueOf(t.getQty()));
                pid.add(String.valueOf(t.getId()));
                total+=t.getQty()*t.getPrice();

            }
        }
        bill[1]=BILL;
        bill[2]=String.valueOf(total);
        sendToserver();
    }

    private void sendToserver() {
        wfComm = new WifiCommunication(this.mHandler);
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(s.getIp())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        Api api=retrofit.create(Api.class);

        Call<String> call=api.sendParsel(pid,qty,txt_name.getText().toString());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful())
                {
                    Log.e("Ok","Ok");
                    wfComm.initSocket(s.getPrinterIp(), 9100,bill);
                }
                else {
                    Log.e("Error For Web",":"+response.message());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("Faild",":"+t.getMessage());
                qty.clear();
                pid.clear();
            }
        });
    }

    public static String getWhiteSpace(int size) {
        StringBuilder builder = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            builder.append(' ');
        }
        return builder.toString();
    }

    public void getProduct()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(s.getIp())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Api api = retrofit.create(Api.class);

        Call<List<TableProduct>> call = api.parselitem();
        call.enqueue(new Callback<List<TableProduct>>() {
            @Override
            public void onResponse(Call<List<TableProduct>> call, Response<List<TableProduct>> response) {
                if(response.isSuccessful())
                {
                    productList=response.body();
                    if(productList!=null)
                    {
                        parselAdapter=new ParselAdapter(productList,Parsel.this);
                        rcv.setAdapter(parselAdapter);
                    }
                }
                else {
                    Toast.makeText(Parsel.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TableProduct>> call, Throwable t) {
                Toast.makeText(Parsel.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void getParselid()
    {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(s.getIp())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        Api api=retrofit.create(Api.class);

        Call<String> call=api.parselid();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful())
                {
                    try
                    {
                        JSONArray sys=new JSONArray(response.body());
                        JSONObject status = sys.getJSONObject(0);
                        txt_name.setText(Html.fromHtml("<b><font color='#FFFFFF' face='sans-serif-medium'> Parsel - "+status.getInt("count")+" </font></b>"));
                    }catch (Exception e) {
                        Toast.makeText(Parsel.this, "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(Parsel.this, "Error : "+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(Parsel.this, "Error : "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    connFlag = 0;
                    revThred = new Parsel.revMsgThread();
                    revThred.start();
                    Intent i=new Intent(Parsel.this,Parsel.class);
                    startActivity(i);
                    finish();
                    return;
                case 1:
                    Toast.makeText(Parsel.this, "Disconnect the WIFI-printer successful", Toast.LENGTH_SHORT).show();
                    revThred.interrupt();
                    return;
                case 2:
                    connFlag = 0;
                    Toast.makeText(Parsel.this, "Connect the WIFI-printer error", Toast.LENGTH_SHORT).show();
                    return;
                case 4:
                    connFlag = 0;
                    Toast.makeText(Parsel.this, "Send Data Failed,please reconnect", Toast.LENGTH_SHORT).show();
                    revThred.interrupt();
                    return;
                case 6:
                    if (((((byte) Integer.parseInt(msg.obj.toString())) >> 6) & 1) == 1) {
                        Toast.makeText(Parsel.this, "The printer has no paper", Toast.LENGTH_LONG).show();
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