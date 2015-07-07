package com.gp19.esgi.simplenotes;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.gp19.esgi.simplenotes.database.DBHelper;
import com.gp19.esgi.simplenotes.database.NoteDataSource;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddNoteFragment extends Fragment {

    private static final Integer[] items = new Integer[]{1,2,3};
    private ArrayAdapter<Integer> adapter;

    public AddNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        setHasOptionsMenu(true);
//        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        final View view =  inflater.inflate(R.layout.fragment_add_note, container, false);
        Spinner s = (Spinner) view.findViewById(R.id.spinner_importance);
        adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item, items);
        s.setAdapter(adapter);
        return view;

    }

    public void saveNote(){
        EditText titleZone = (EditText) getView().findViewById(R.id.note_edit);
        String titleText =  titleZone.getText().toString();
        EditText contentZone = (EditText) getView().findViewById(R.id.note_content);
        String contentText = contentZone.getText().toString();
        Spinner s = (Spinner) getView().findViewById(R.id.spinner_importance);
        int imp = adapter.getItem(s.getSelectedItemPosition());
        DBHelper helper = new DBHelper(getActivity());
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        NoteDataSource noteDataSource = new NoteDataSource(sqLiteDatabase);
        noteDataSource.insert(new Note(titleText, contentText, imp));
        helper.close();
        sqLiteDatabase.close();
        returnMain();
    }

    private void returnMain(){
        getFragmentManager().beginTransaction().replace(R.id.content_frame, NoteListFragment.newInstance(((MainActivity) getActivity()).lastSelected), "NoteListFragment").commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_item_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.done_edit:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
