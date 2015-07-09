package com.gp19.esgi.simplenotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class DrawerGroupAdapter extends ArrayAdapter<NoteGroup>{

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public DrawerGroupAdapter(Context context, int resource, List<NoteGroup> objects) {
        super(context, resource, objects);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView view = (TextView)inflater.inflate(R.layout.drawer_list_item, parent, false);
        view.setText(getItem(position).getGroupName());
        return view;

    }
}
