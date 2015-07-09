package com.gp19.esgi.simplenotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.List;

public class NoteGroupAttachAdapter extends ArrayAdapter<NoteGroup> {


    private List<Long> mapped;
    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public NoteGroupAttachAdapter(Context context, int resource, List<NoteGroup> objects, List<Long> map) {
        super(context, resource, objects);
        this.mapped = map;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.group_row_layout, parent, false);
        ((TextView) view.findViewById(R.id.textView3)).setText(getItem(position).getGroupName());
        CheckBox checkBox = ((CheckBox) view.findViewById(R.id.checkBox2));
        if (mapped.contains(getItem(position).getId())) checkBox.setChecked(true);
        else checkBox.setChecked(false);
        return view;
    }
}
