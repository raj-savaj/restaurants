package com.demos.param.ashtrestoant.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.demos.param.ashtrestoant.R;
import com.demos.param.ashtrestoant.Session;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import com.demos.param.ashtrestoant.DB.productList;

/**
 * Created by root on 6/4/18.
 */

public class Auto_Item_Adapter extends ArrayAdapter<productList> {
    List<productList> customers,tempCustomer,suggestions;
    Session s;
    public Auto_Item_Adapter(Context context, List<productList> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
        s=new Session(context);
        this.customers = objects;
        this.tempCustomer = new ArrayList<productList>(objects);
        this.suggestions = new ArrayList<productList>(objects);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        productList plist=getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.customer_row, parent, false);
        }
        TextView txtCustomer = (TextView) convertView.findViewById(R.id.tvCustomer);
        ImageView ivCustomerImage = (ImageView) convertView.findViewById(R.id.ivCustomerImage);
        if (txtCustomer != null)
            txtCustomer.setText(plist.getName()+" - "+plist.getFd_code());
            Picasso.with(convertView.getContext()).load(s.getImg()+plist.getImage())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .resize(80,60)
                    .into(ivCustomerImage);
        return convertView;
    }


    @Override
    public Filter getFilter() {
        return myFilter;
    }

    Filter myFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            productList customer = (productList) resultValue;
            return "";
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if (charSequence != null) {
                suggestions.clear();
                for (productList people : tempCustomer) {
                    if(charSequence.length()<4)
                    {
                        if (people.getFd_code().toLowerCase().startsWith(charSequence.toString().toLowerCase())) {
                            suggestions.add(people);
                        }
                    }
                    else
                    {
                        if (people.getName().toLowerCase().contains(charSequence.toString().toLowerCase()) || people.getFd_code().toLowerCase().startsWith(charSequence.toString().toLowerCase())) {
                            suggestions.add(people);
                        }
                    }

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            ArrayList<productList> c = (ArrayList<productList>) filterResults.values;
            if (filterResults != null && filterResults.count > 0) {
                clear();
                for (productList cust : c) {
                    add(cust);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
