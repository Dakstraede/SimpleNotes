package com.gp19.esgi.simplenotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.TextView;

import com.gp19.esgi.simplenotes.database.DBHelper;
import com.gp19.esgi.simplenotes.database.NoteDataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class EndlessAdapter extends ArrayAdapter<Note> {
    private int layoutId;
    private HashMap<Integer, Boolean> mSelection = new HashMap<>();


    public EndlessAdapter(Context context, List<Note> itemList, int layoutId)
    {
        super(context, layoutId, itemList);
        this.layoutId  = layoutId;
    }

    public void setNewSelection(int position, boolean value){
        mSelection.put(position, value);
        notifyDataSetChanged();
    }

    public boolean isPositionChecked(int position){
        Boolean result = mSelection.get(position);
        return  result == null ? false : result;
    }

    public Set<Integer> getCurrentCheckedPosition(){
        return mSelection.keySet();
    }

    public void removeSelection(int position){
        mSelection.remove(position);
        notifyDataSetChanged();
    }

    public void clearSelection(){
        mSelection = new HashMap<>();
        notifyDataSetChanged();
    }

    public void deleteSelectedItems(){
        DBHelper helper = new DBHelper(getContext());
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        NoteDataSource noteDataSource = new NoteDataSource(sqLiteDatabase);
        for (Integer i : getCurrentCheckedPosition())
        {
            Note tmp = getItem(i);
            noteDataSource.delete(tmp);
        }
        helper.close();
        sqLiteDatabase.close();
        notifyDataSetChanged();
    }

    public void archiveSelectedItems(){
        DBHelper helper = new DBHelper(getContext());
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        NoteDataSource noteDataSource = new NoteDataSource(sqLiteDatabase);
        for (Integer i : getCurrentCheckedPosition())
        {
            getItem(i).setArchived(true);
            noteDataSource.update(getItem(i));
        }
        helper.close();
        sqLiteDatabase.close();
        notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result  = convertView;

        if (result == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            result = inflater.inflate(layoutId, parent, false);
        }


        TextView tv = (TextView) result.findViewById(R.id.title);
        tv.setText(getItem(position).getNoteTitle());
        TextView tv2 = (TextView) result.findViewById(R.id.txt1);
        if (getItem(position).getNoteContent().length() > 15){
            tv2.setText(getItem(position).getNoteContent().substring(0,14) + "[...]");
        }
        else tv2.setText(getItem(position).getNoteContent());
//        TextView tv3 = (TextView) result.findViewById(R.id.txtDate);
//        tv3.setText(itemList.get(position).getCreationDate().toString());
        result.setBackgroundColor(getContext().getResources().getColor(R.color.background_material_light));
        if (mSelection.get(position) != null)
        {
            result.setBackgroundColor(getContext().getResources().getColor(R.color.material_blue_grey_800));
        }
        return result;
    }
}
