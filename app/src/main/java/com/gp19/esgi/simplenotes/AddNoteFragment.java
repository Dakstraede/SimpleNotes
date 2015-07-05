package com.gp19.esgi.simplenotes;


import android.app.FragmentManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

        final View view =  inflater.inflate(R.layout.fragment_add_note, container, false);
        Spinner s = (Spinner) view.findViewById(R.id.spinner_importance);
        adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item, items);
        s.setAdapter(adapter);

        Button buttonSave = ((Button) view.findViewById(R.id.save_new_note));
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote(view);
            }
        });
        return view;

    }

    public void saveNote(View view){
        EditText titleZone = (EditText) view.findViewById(R.id.note_edit);
        String titleText =  titleZone.getText().toString();

        EditText contentZone = (EditText) view.findViewById(R.id.note_content);
        String contentText = contentZone.getText().toString();
        Spinner s = (Spinner) view.findViewById(R.id.spinner_importance);
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
        getFragmentManager().popBackStack("ADDNOTE", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }


}
