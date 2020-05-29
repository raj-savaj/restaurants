package com.demos.param.ashtrestoant.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.demos.param.ashtrestoant.Parsel;
import com.demos.param.ashtrestoant.R;
import com.demos.param.ashtrestoant.Session;
import com.squareup.picasso.Picasso;

import java.util.List;

import com.demos.param.ashtrestoant.Model.TableProduct;
import de.hdodenhof.circleimageview.CircleImageView;

public class ParselAdapter extends RecyclerView.Adapter<ParselAdapter.ViewHolder> {

    List<TableProduct> productList;
    Context context;
    Session s;
    Parsel parsel;
    int count=0,total=0;
    public ParselAdapter(List<TableProduct> productList, Parsel parsel) {
        this.productList = productList;
        this.parsel=parsel;
        parsel.txt_tot.setText(""+total);
        parsel.txt_select.setText("Selected Item "+count);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_parsel,parent,false);
        context=view.getContext();
        s=new Session(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final TableProduct t=productList.get(position);
        holder.p_name.setText(t.getName()+" ("+t.getPrice()+")");
        Picasso.with(context).load(s.getImg()+t.getImage())
                .placeholder(R.drawable.thali)
                .error(R.drawable.thali)
                .into(holder.cimg);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, TextWatcher {
        CircleImageView cimg,checkedimg;
        Button minus,plus;
        EditText qty;
        TextView p_name;
        public ViewHolder(View v) {
            super(v);
            cimg=v.findViewById(R.id.p_img);
            p_name=v.findViewById(R.id.p_name);
            checkedimg=v.findViewById(R.id.checked);
            minus=v.findViewById(R.id.minus);
            plus=v.findViewById(R.id.plus);
            qty=v.findViewById(R.id.qty);
            cimg.setOnClickListener(this);
            minus.setOnClickListener(this);
            plus.setOnClickListener(this);
            qty.addTextChangedListener(this);
            qty.setEnabled(false);
            minus.setEnabled(false);
            plus.setEnabled(false);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int position = getAdapterPosition();
            TableProduct p=productList.get(position);
            if(id==R.id.p_img)
            {
                if(p.isSelected())
                {
                    checkedimg.setVisibility(View.GONE);
                    p.setSelected(false);
                    qty.setEnabled(false);
                    minus.setEnabled(false);
                    plus.setEnabled(false);
                    qty.setText("0");
                    total-=p.getQty()*p.getPrice();
                    parsel.txt_tot.setText(""+total);
                    p.setQty(0);
                    parsel.productMap.remove(p.getId());
                    count--;
                }
                else {
                    checkedimg.setVisibility(View.VISIBLE);
                    p.setSelected(true);
                    qty.setEnabled(true);
                    minus.setEnabled(true);
                    plus.setEnabled(true);
                    parsel.productMap.put(p.getId(),p);
                    count++;
                }
                parsel.txt_select.setText("Selected Item "+count);
            }
            if (id == R.id.plus)
            {
                if(p.isSelected())
                {
                    int q = Integer.valueOf(qty.getText().toString());
                    q=q+1;
                    qty.setText(String.valueOf(q));
                    parsel.productMap.put(p.getId(),p);
                }
            }

            if (id == R.id.minus && p.isSelected())
            {
                int q = 0;
                if (!qty.getText().toString().equalsIgnoreCase(""))
                    q = Integer.valueOf(qty.getText().toString());
                if (q > 0) {
                    q = q - 1;
                    qty.setText(String.valueOf(q));
                }
                parsel.productMap.put(p.getId(),p);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            int position = getAdapterPosition();
            TableProduct p=productList.get(position);
            if(p.isSelected())
            {
                if(charSequence.length()>0)
                {
                    if(qty.getText().toString().equals(""))
                    {
                        qty.setText("0");
                    }
                    if(p.getQty()!=0)
                    {
                        total-=p.getQty()*p.getPrice();
                    }
                    p.setQty(Integer.parseInt(qty.getText().toString()));
                    total+=p.getQty()*p.getPrice();
                    parsel.txt_tot.setText(""+total);
                    parsel.productMap.put(p.getId(),p);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
