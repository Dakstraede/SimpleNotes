package com.gp19.esgi.simplenotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.gp19.esgi.simplenotes.database.DBHelper;
import com.gp19.esgi.simplenotes.database.NoteDataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class EndlessAdapter extends ArrayAdapter<Note> implements Filterable{
    private int layoutId;
    private HashMap<Integer, Boolean> mSelection = new HashMap<>();
    ArrayList<Note> originalList;
    private ArrayList<Note> filteredList;
    private NoteFilter noteFilter;



    public EndlessAdapter(Context context, ArrayList<Note> itemList, int layoutId)
    {
        super(context, layoutId, itemList);
        this.layoutId  = layoutId;
        this.filteredList = itemList;
        this.originalList = itemList;

        getFilter();
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

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Note getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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

    public void archiveSelectedItems(boolean archivedParam){
        DBHelper helper = new DBHelper(getContext());
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        NoteDataSource noteDataSource = new NoteDataSource(sqLiteDatabase);
        for (Integer i : getCurrentCheckedPosition())
        {
            getItem(i).setArchived(archivedParam);
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

    @Override
    public Filter getFilter() {
        if (noteFilter == null){
            noteFilter = new NoteFilter();
        }
        return noteFilter;
    }

    public void duplicateSelectedItems() {

        DBHelper helper = new DBHelper(getContext());
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        NoteDataSource noteDataSource = new NoteDataSource(sqLiteDatabase);
        for (Integer i : getCurrentCheckedPosition())
        {
            Note tmp = getItem(i);
            noteDataSource.insert(new Note(tmp.getNoteTitle(), tmp.getNoteContent(), tmp.getImportanceLevel(), tmp.isArchived()));
        }
        helper.close();
        sqLiteDatabase.close();
        notifyDataSetChanged();
    }

    private class NoteFilter extends Filter{


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length()>0)
            {
                ArrayList<Note> tempList = new ArrayList<>();

                for (Note note : originalList)
                {
                    if (note.getNoteTitle().toLowerCase().contains(constraint.toString().toLowerCase()))
                    {
                        tempList.add(note);
                    }
                }
                filterResults.count = tempList.size();
                filterResults.values = tempList;
            }
            else {
                filterResults.count = originalList.size();
                filterResults.values = originalList;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = ((ArrayList<Note>) results.values);
            notifyDataSetChanged();
        }
    }
}
