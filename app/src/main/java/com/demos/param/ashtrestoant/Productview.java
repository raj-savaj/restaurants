package com.demos.param.ashtrestoant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import com.demos.param.ashtrestoant.Adapter.TableRecyclerAdapter;
import com.demos.param.ashtrestoant.Model.Table;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Productview extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    List<Table> seat;
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewAdapter;
    RecyclerView.LayoutManager recylerViewLayoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productview);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_table);
        recylerViewLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(recylerViewLayoutManager);
        swipeRefreshLayout.setOnRefreshListener(this);
        getSeat();
    }

    public void getSeat()
    {
        Session s=new Session(getApplicationContext());
        final ProgressDialog dialog = new ProgressDialog(Productview.this);
        dialog.setMessage("Loading Tables ...");
        dialog.show();
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(s.getIp())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Api api=retrofit.create(Api.class);

        Call<List<Table>> call=api.seat();
        call.enqueue(new Callback<List<Table>>() {
            @Override
            public void onResponse(Call<List<Table>> call, Response<List<Table>> response) {
                seat=response.body();
                recyclerViewAdapter = new TableRecyclerAdapter(seat,Productview.this);
                recyclerView.setAdapter(recyclerViewAdapter);
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Table>> call, Throwable t) {
                dialog.dismiss();
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), "No internet connection!", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }

    @Override
    public void onRefresh() {
        seat.clear();
        getSeat();
        recyclerViewAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.Parcel:
                Intent parsel_intent=new Intent(getApplicationContext(),Parsel.class);
                startActivity(parsel_intent);
                return true;
            case R.id.history:
                Intent i=new Intent(getApplicationContext(),History.class);
                startActivity(i);
                return true;
            case R.id.print_setting:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
