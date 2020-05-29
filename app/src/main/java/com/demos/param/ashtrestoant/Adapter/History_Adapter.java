package com.demos.param.ashtrestoant.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.demos.param.ashtrestoant.DB.Order;
import com.demos.param.ashtrestoant.R;

import java.util.List;

public class History_Adapter extends RecyclerView.Adapter<History_Adapter.ViewHolder> {

    List<Order> orderList;
    View view;
    public ItemClickListen onClickListener;

    public History_Adapter(List<Order> orderList, ItemClickListen onClickListener) {
        this.orderList = orderList;
        this.onClickListener=onClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_histrory,parent,false);
        History_Adapter.ViewHolder viewHolder1 = new History_Adapter.ViewHolder(view);
        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        Order order=orderList.get(position);
        holder.txttbname.setText(order.getTbname());
        holder.txttime.setText(order.getTime());
        holder.txttot.setText(""+order.getPrice());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.orderItemClick(v, position);
            }
        });
    }

    public interface ItemClickListen {

        void orderItemClick(View v, int position);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
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
        TextView txttbname,txttime,txttot;
        public ViewHolder(View v) {
            super(v);
            txttbname=v.findViewById(R.id.tbname);
            txttime=v.findViewById(R.id.txttime);
            txttot=v.findViewById(R.id.txttot);

        }
    }
}
