package com.ego.sandbox.sqlitesync.helpers;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ego.sandbox.sqlitesync.R;
import com.ego.sandbox.sqlitesync.entities.Store;

import java.util.ArrayList;

public class StoreAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Store> mStores;

    public StoreAdapter(Context context, ArrayList<Store> stores) {
        mContext = context;
        mStores = stores;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mStores.size();
    }

    @Override
    public Object getItem(int position) {
        return mStores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get view for row item
        View rowView = mLayoutInflater.inflate(R.layout.list_item_store, parent, false);
        TextView nameTextView = rowView.findViewById(R.id.storeName);
        Store store = (Store) getItem(position);
        nameTextView.setText(store.getName());

        return rowView;
    }

}
