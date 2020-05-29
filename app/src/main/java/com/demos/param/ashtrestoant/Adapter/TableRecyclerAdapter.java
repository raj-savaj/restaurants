package com.demos.param.ashtrestoant.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.demos.param.ashtrestoant.Api;
import com.demos.param.ashtrestoant.Model.Table;
import com.demos.param.ashtrestoant.ProcessOrder;
import com.demos.param.ashtrestoant.Productview;
import com.demos.param.ashtrestoant.R;
import com.demos.param.ashtrestoant.RunningTable;
import com.demos.param.ashtrestoant.Session;
import com.demos.param.ashtrestoant.UpdateOrder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by root on 4/4/18.
 */

public class TableRecyclerAdapter extends RecyclerView.Adapter<TableRecyclerAdapter.ViewHolder> {
    List<Table> seat;
    View view1;
    ViewHolder viewHolder1;
    TextView textView;
    TextView tablename;
    public static Productview productview;
    public TableRecyclerAdapter(List<Table> seat1, Productview productview){
        seat=seat1;
        this.productview=productview;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textView;
        public TextView tablename;
        public LinearLayout img;
        public ViewHolder(final View v)
        {
            super(v);

            textView = (TextView)v.findViewById(R.id.table);
            tablename=(TextView)v.findViewById(R.id.table_name);
            img=v.findViewById(R.id.cardview);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView textView=view.findViewById(R.id.table);
                    String tid=textView.getText().toString();
                    checkSeat(tid,view.getContext());
                }
            });

            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Intent i=new Intent(v.getContext(), RunningTable.class);
                    i.putExtra("tableid",textView.getText().toString());
                    i.putExtra("name","TABLE-"+tablename.getText().toString());
                    v.getContext().startActivity(i);
                    return true;
                }
            });
        }

        public void checkSeat(final String email, final Context t1) {
            Session s=new Session(t1);
            final ProgressDialog dialog = new ProgressDialog(t1);
            dialog.setMessage("Searching Network ...");
            dialog.show();
            Retrofit retrofit=new Retrofit.Builder()
                    .baseUrl(s.getIp())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            Api api=retrofit.create(Api.class);

            Call<String> call=api.ceckstate(email);
            call.enqueue(new Callback<String>() {

                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try
                    {
                        JSONObject sys=new JSONObject(response.body());
                        int status = sys.getInt("status");
                        if(status==0)
                        {
                            dialog.dismiss();
                            Intent i=new Intent(t1, ProcessOrder.class);
                            i.putExtra("tableid",email);
                            i.putExtra("name","TABLE-"+tablename.getText().toString());
                            t1.startActivity(i);
                        }
                        else
                        {
                            dialog.dismiss();
                            Intent i=new Intent(t1, UpdateOrder.class);
                            i.putExtra("tableid",email);
                            i.putExtra("name","TABLE-"+tablename.getText().toString());
                            t1.startActivity(i);
                        }
                    }catch (JSONException e) {
                        Log.e("error in check  Detail",e.getMessage());
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    dialog.dismiss();
                    Snackbar snackbar = Snackbar
                            .make(productview.findViewById(android.R.id.content), "No internet connection!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });

        }

    }

    @Override
    public TableRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_layout,parent,false);
        viewHolder1 = new ViewHolder(view1);
        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        Table t=seat.get(position);
        holder.textView.setText(String.valueOf(t.getStid()));
        holder.tablename.setText(t.getTbname().replaceAll("[^0-9.]",""));
        if(t.getStatus()==1)
        {
            holder.img.setBackgroundResource(R.drawable.table_res_border);
            holder.tablename.setTextColor(Color.parseColor("#c1465a"));
        }
    }

    @Override
    public int getItemCount(){
        return seat.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
