package com.demos.param.ashtrestoant.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.demos.param.ashtrestoant.ProcessOrder;
import com.demos.param.ashtrestoant.R;
import com.demos.param.ashtrestoant.Session;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import com.demos.param.ashtrestoant.Model.TableProduct;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by root on 11/4/18.
 */

public class Order_adapter extends RecyclerView.Adapter<Order_adapter.ViewHolder>
{
    View view1;
    Order_adapter.ViewHolder viewHolder1;
    Context context;
    public List<TableProduct> plist;
    ProcessOrder updateOrder;
    int total=0;
    List<Integer> qty;
    public Order_adapter(List<TableProduct> plist, ProcessOrder updateOrder)
    {
        this.plist=plist;
        this.updateOrder=updateOrder;
        qty=new ArrayList<Integer>();
        for(TableProduct t:plist)
        {
            total+= t.getQty()*t.getPrice();
            qty.add(t.getQty());
        }
        updateOrder.getSupportActionBar().setSubtitle(String.valueOf(total));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_row,parent,false);
        viewHolder1 = new Order_adapter.ViewHolder(view1);
        context=parent.getContext();
        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        TableProduct t=plist.get(position);
        holder.pname.setText(t.getName());
        holder.price.setText(String.valueOf(t.getPrice()));
        holder.id.setText(String.valueOf(t.getId()));
        holder.pqty.setText(String.valueOf(t.getQty()));

        Session s=new Session(context);

        Picasso.with(context).load(s.getImg()+t.getImage())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .resize(80,60)
                .into(holder.pimg);

        if(t.getOid() == 0)
        {
            holder.iv_remove.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.iv_remove.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return plist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView pname,price,id;
        public EditText pqty;
        public CircleImageView pimg;
        public ImageView iv_plus, iv_minus, iv_remove;
        public ViewHolder(View v) {
            super(v);
            pname=(TextView) v.findViewById(R.id.p_name);
            price=(TextView) v.findViewById(R.id.p_price);
            id=(TextView)v.findViewById(R.id.po_id);
            pqty=(EditText) v.findViewById(R.id.qty);
            pimg=(CircleImageView)v.findViewById(R.id.p_img);
            iv_plus = (ImageView) v.findViewById(R.id.qty_minus);
            iv_minus = (ImageView) v.findViewById(R.id.qty_plus);
            iv_remove = (ImageView) v.findViewById(R.id.imageView2);
            iv_minus.setOnClickListener(this);
            iv_plus.setOnClickListener(this);
            iv_remove.setOnClickListener(this);
            pqty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    int id = view.getId();
                    int position = getAdapterPosition();
                    try
                    {
                        TableProduct p=plist.get(position);
                        if(!hasFocus)
                        {
                            if(pqty.getText().toString().equals(""))
                            {
                                pqty.setText("0");
                            }
                            if(p.getQty()!=0)
                            {
                                total-=p.getQty()*p.getPrice();
                            }
                            p.setQty(Integer.parseInt(pqty.getText().toString()));
                            total+=p.getQty()*p.getPrice();
                            updateOrder.getSupportActionBar().setSubtitle(String.valueOf(total));
                        }
                    }
                    catch (Exception ee)
                    {
                        Log.e("error Delete",ee.getMessage());
                    }
                }
            });
        }

        @Override
        public void onClick(View view)
        {
            int id = view.getId();
            int position = getAdapterPosition();
            TableProduct p=plist.get(position);

            if (id == R.id.qty_plus)
            {
                int qty = Integer.valueOf(pqty.getText().toString());
                qty = qty + 1;
                pqty.setText(String.valueOf(qty));
                p.setQty(Integer.parseInt(pqty.getText().toString()));
                total+=Integer.parseInt(price.getText().toString());
                updateOrder.getSupportActionBar().setSubtitle(String.valueOf(total));
            }
            if (id == R.id.qty_minus)
            {
                if(position < qty.size())
                {
                    int qty = 0;
                    if (!pqty.getText().toString().equalsIgnoreCase(""))
                        qty = Integer.valueOf(pqty.getText().toString());

                    if (qty > 0) {
                        qty = qty - 1;
                        pqty.setText(String.valueOf(qty));
                        p.setQty(Integer.parseInt(pqty.getText().toString()));
                        total-=Integer.parseInt(price.getText().toString());
                        updateOrder.getSupportActionBar().setSubtitle(String.valueOf(total));
                    }
                }
                else
                {
                    int qty = 0;
                    if (!pqty.getText().toString().equalsIgnoreCase(""))
                        qty = Integer.valueOf(pqty.getText().toString());

                    if (qty > 0) {
                        qty = qty - 1;
                        pqty.setText(String.valueOf(qty));
                        p.setQty(Integer.parseInt(pqty.getText().toString()));
                        total-=Integer.parseInt(price.getText().toString());
                        updateOrder.getSupportActionBar().setSubtitle(String.valueOf(total));
                    }
                }
            }
            if(id==R.id.imageView2)
            {
                pqty.setText(String.valueOf(0));
                if(p.getQty()!=0)
                {
                    total-=p.getQty()*p.getPrice();
                    updateOrder.getSupportActionBar().setSubtitle(String.valueOf(total));
                }
                plist.remove(position);
                notifyDataSetChanged();
            }
        }
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