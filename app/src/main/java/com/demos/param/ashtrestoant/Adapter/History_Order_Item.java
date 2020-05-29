package com.demos.param.ashtrestoant.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.demos.param.ashtrestoant.DB.Order;
import com.demos.param.ashtrestoant.R;

import java.util.List;

public class History_Order_Item extends RecyclerView.Adapter<History_Order_Item.ViewHolder> {
    List<Order> orderItemList;

    public History_Order_Item(List<Order> orderItemList) {
        this.orderItemList = orderItemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_item,parent,false);
        History_Order_Item.ViewHolder viewHolder1 = new History_Order_Item.ViewHolder(view);
        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Order order=orderItemList.get(position);
        holder.iname.setText(order.getName());
        holder.qty.setText(""+order.getQty());
        holder.price.setText(String.format("%4s",order.getPrice()));
        holder.totprice.setText(String.format("%5s",(order.getQty()*order.getPrice())));
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView iname,qty,price,totprice;
        public ViewHolder(View v) {
            super(v);
            iname=v.findViewById(R.id.name);
            qty=v.findViewById(R.id.qty);
            price=v.findViewById(R.id.price);
            totprice=v.findViewById(R.id.tot_price);
        }
    }
}
