package com.gp19.esgi.simplenotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class EndlessAdapter extends ArrayAdapter {

    private List<String> itemList;
    private Context context;
    private int layoutId;

    public EndlessAdapter(Context context, List<String> itemList, int layoutId)
    {
        super(context, layoutId, itemList);
        this.itemList = itemList;
        this.context = context;
        this.layoutId  = layoutId;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return itemList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result  = convertView;

        if (result == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            result = inflater.inflate(layoutId, parent, false);
        }

        TextView tv = (TextView) result.findViewById(R.id.txt1);
        tv.setText(itemList.get(position));

        return result;
    }
}