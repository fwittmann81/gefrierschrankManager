package com.example.wittmanf.gefrierschrankmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class ItemListViewAdapter extends ArrayAdapter<Item> {

    private Context mContext;
    private int mResource;

    public ItemListViewAdapter(Context context, int resource, ArrayList<Item> objects) {
        super(context, resource, objects);
        this.mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name = getItem(position).getName();
        int amount = getItem(position).getAmount();
        String einheit = getItem(position).getEinheit().getDescription();
        int fach = getItem(position).getFach();
        Date haltbarkeit = getItem(position).getMaxFreezeDate();
        String formatedHaltbarkeit = Constants.SDF.format(haltbarkeit);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView nameTV = convertView.findViewById(R.id.customLayoutName);
        TextView fachTV = convertView.findViewById(R.id.customLayoutFach);
        TextView haltbarkeitTV = convertView.findViewById(R.id.customLayoutHaltbarkeit);

        nameTV.setText(name + " (" + amount + " " + einheit + ")");
        fachTV.setText("Fach " + fach);
        haltbarkeitTV.setText(Constants.DEFAULT_MAX_FREEZE_DATE.equals(formatedHaltbarkeit) ? "" : formatedHaltbarkeit);

        return convertView;
    }
}
