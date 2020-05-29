package com.demos.param.ashtrestoant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.demos.param.ashtrestoant.Adapter.RunningTabAdapter;
import com.demos.param.ashtrestoant.Model.RunningTab;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RunningTable extends AppCompatActivity {

    String tableid;
    CoordinatorLayout updateosnack;
    List<RunningTab> plist;
    RecyclerView rcvupdate;
    RecyclerView.Adapter recyclerViewAdapter;
    Session session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_table);
        session=new Session(getApplicationContext());
        tableid=getIntent().getExtras().getString("tableid");
        getSupportActionBar().setTitle(getIntent().getExtras().getString("name"));
        updateosnack=(CoordinatorLayout)findViewById(R.id.rrsnack);
        rcvupdate = findViewById(R.id.rrcv);
        rcvupdate.setLayoutManager(new LinearLayoutManager(this));
        TableData(tableid);
    }

    private void TableData(String tid)
    {
        final ProgressDialog dialog = new ProgressDialog(RunningTable.this);
        dialog.setMessage("Getting Order...");
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(session.getIp())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Api api = retrofit.create(Api.class);

        Call<List<RunningTab>> call = api.RunningTable(tid);
        call.enqueue(new Callback<List<RunningTab>>() {
            @Override
            public void onResponse(Call<List<RunningTab>> call, Response<List<RunningTab>> response) {
                dialog.dismiss();
                plist=response.body();
                recyclerViewAdapter=new RunningTabAdapter(plist);
                rcvupdate.setAdapter(recyclerViewAdapter);
            }

            @Override
            public void onFailure(Call<List<RunningTab>> call, Throwable t) {
                dialog.dismiss();
                Snackbar snackbar = Snackbar
                        .make(updateosnack, "No internet connection!", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    public void ChangeOrder(View view) {
        List<String> oid=new ArrayList<String>();;
        List<String> qty=new ArrayList<String>();
        if(plist.size() > 0)
        {
            final ProgressDialog dialog = new ProgressDialog(RunningTable.this);
            dialog.setMessage("Updating Order...");
            dialog.show();
            for( RunningTab t:plist)
            {
                qty.add(String.valueOf(t.getQty()));
                oid.add(String.valueOf(t.getOid()));
            }
            Retrofit retrofit=new Retrofit.Builder()
                    .baseUrl(session.getIp())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            Api api=retrofit.create(Api.class);

            Call<String> call=api.RunningItem(oid.toArray(new  String[oid.size()]),qty.toArray(new  String[qty.size()]));
            call.enqueue(new Callback<String>() {

                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    dialog.dismiss();
                    Intent i=new Intent(RunningTable.this,Productview.class);
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
        else
        {
            Intent i=new Intent(RunningTable.this,Productview.class);
            startActivity(i);
            finish();
        }
    }

    public void removetable(View view) {

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(session.getIp())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        Api api=retrofit.create(Api.class);

        Call<String> call=api.freeTable(tableid);
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful())
                {
                    Log.e("Res",""+response.body());
                    Intent i=new Intent(getApplicationContext(),Productview.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    Toast.makeText(RunningTable.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), "No internet connection!", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }
}