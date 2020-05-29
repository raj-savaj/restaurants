package com.demos.param.ashtrestoant.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.demos.param.ashtrestoant.Api;
import com.demos.param.ashtrestoant.R;
import com.demos.param.ashtrestoant.Session;
import com.squareup.picasso.Picasso;

import java.util.List;

import com.demos.param.ashtrestoant.Model.RunningTab;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RunningTabAdapter extends  RecyclerView.Adapter<RunningTabAdapter.ViewHolder>{

    View view1;
    RunningTabAdapter.ViewHolder viewHolder1;
    Context context;
    public List<RunningTab> plist;
    Session session;
    public RunningTabAdapter(List<RunningTab> plist) {
        this.plist=plist;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_row,parent,false);
        viewHolder1 = new RunningTabAdapter.ViewHolder(view1);
        context=parent.getContext();
        session=new Session(context);
        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RunningTab t=plist.get(position);
        if((t.getQty()-t.getServe())!=0)
        {
            holder.pname.setText(t.getName());
            holder.price.setText(String.valueOf(t.getPrice()));
            holder.id.setText(String.valueOf(t.getOid()));
            holder.pqty.setText(String.valueOf(t.getQty()-t.getServe()));
            Picasso.with(context).load(session.getImg()+t.getImage())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .resize(80,60)
                    .into(holder.pimg);
        }
    }

    @Override
    public int getItemCount() {
        return plist.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView pname,price,id,pqty;
        public CircleImageView pimg;
        public ImageView  iv_minus, iv_remove,iv_plus;
        public ViewHolder(View v) {
            super(v);
            pname=(TextView) v.findViewById(R.id.p_name);
            price=(TextView) v.findViewById(R.id.p_price);
            id=(TextView)v.findViewById(R.id.po_id);
            pqty=(TextView)v.findViewById(R.id.qty);
            pimg=(CircleImageView)v.findViewById(R.id.p_img);
            iv_minus = (ImageView) v.findViewById(R.id.qty_minus);
            iv_plus=(ImageView) v.findViewById(R.id.qty_plus);
            iv_remove = (ImageView) v.findViewById(R.id.imageView2);
            iv_minus.setOnClickListener(this);
            iv_plus.setOnClickListener(this);
            iv_remove.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            int id = view.getId();
            int position = getAdapterPosition();
            RunningTab p=plist.get(position);
            if (id == R.id.qty_minus)
            {
                    int qty = 0;
                    if (!pqty.getText().toString().equalsIgnoreCase(""))
                        qty = Integer.valueOf(pqty.getText().toString());

                    if (qty > 0)
                    {
                        qty = qty - 1;
                        pqty.setText(String.valueOf(qty));
                        p.setQty(Integer.parseInt(pqty.getText().toString()));
                    }
            }
            if (id == R.id.qty_plus)
            {
                int qty = Integer.valueOf(pqty.getText().toString());
                qty = qty + 1;
                pqty.setText(String.valueOf(qty));
                p.setQty(Integer.parseInt(pqty.getText().toString()));
            }
            if(id==R.id.imageView2)
            {
                delete(plist.get(position).getOid(),plist.get(position).getServe(),view,position);
            }
        }
        public void delete(int oid, int serve, final View v, final int position)
        {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(session.getIp())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            Api api = retrofit.create(Api.class);

            Call<String> call = api.cancelProduct(oid,serve);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try
                    {
                        plist.remove(position);
                        notifyDataSetChanged();
                    }
                    catch (Exception ee)
                    {

                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("error",""+t.getMessage());
                    Toast.makeText(v.getContext().getApplicationContext(), "Connect Internet", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
