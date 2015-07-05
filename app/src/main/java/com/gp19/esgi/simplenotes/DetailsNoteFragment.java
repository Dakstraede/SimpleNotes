package com.gp19.esgi.simplenotes;


import android.app.FragmentManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.gp19.esgi.simplenotes.database.DBHelper;
import com.gp19.esgi.simplenotes.database.NoteDataSource;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsNoteFragment extends Fragment {

    private static final String KEY = "NOTE";
    private Note mNote;
    private SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    private static final Integer[] items = new Integer[]{1,2,3};
    private ArrayAdapter<Integer> adapter;

    private EditText title;
    private EditText content;
    private Spinner importance;
    private Spinner group;
    private TextView creationDate;
    private TextView lastModification;
    private boolean modified ;
    private CheckBox checkArchived;

    public static DetailsNoteFragment newInstance(Note note){
        DetailsNoteFragment fragment = new DetailsNoteFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY, note);
        fragment.setArguments(bundle);
        return fragment;

    }


    public DetailsNoteFragment() {
        // Required empty public constructor
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        modified = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mNote = getArguments().getParcelable(KEY);
        View view = inflater.inflate(R.layout.fragment_details_note, container, false);

        Button saveButton = ((Button) view.findViewById(R.id.save_button));
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        final Button deleteButton = ((Button) view.findViewById(R.id.button2));
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeNote();
            }
        });


        title = (EditText) view.findViewById(R.id.edit_title);
        content = (EditText) view.findViewById(R.id.edit_content);
        importance = (Spinner) view.findViewById(R.id.importance_edit_spinner);
        adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item, items);
        importance.setAdapter(adapter);
        creationDate = (TextView) view.findViewById(R.id.creation_date_label);
        lastModification = (TextView) view.findViewById(R.id.modification_date_label);
        checkArchived = (CheckBox) view.findViewById(R.id.checkBox_archived);
        if(mNote != null){
            title.setText(mNote.getNoteTitle());
            content.setText(mNote.getNoteContent());
            checkArchived.setChecked(mNote.isArchived());
            for(int i = 0; i < importance.getAdapter().getCount(); i++){
                if ((Integer)importance.getAdapter().getItem(i) == mNote.getImportanceLevel()){
                    importance.setSelection(i);
                    break;
                }
            }
            creationDate.setText(smp.format(mNote.getCreationDate()));
            if (mNote.getLastModificationDate() != null){
                lastModification.setText(smp.format(mNote.getLastModificationDate()));
            }
            else lastModification.setText(R.string.last_not_modified);
        }

        return view;
    }

    public void saveNote(){
        mNote.setNoteTitle(title.getText().toString());
        mNote.setNoteContent(content.getText().toString());
        mNote.setLastModicationDate();
        mNote.setArchived(checkArchived.isChecked());
        mNote.setImportanceLevel((Integer)importance.getSelectedItem());
        DBHelper helper = new DBHelper(getActivity());
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        NoteDataSource noteDataSource = new NoteDataSource(sqLiteDatabase);
        noteDataSource.update(mNote);
        helper.close();
        sqLiteDatabase.close();
        returnMain();
    }

    private void returnMain(){
        getFragmentManager().popBackStack("MAIN", 0);
    }

    public void removeNote(){
        DBHelper helper = new DBHelper(getActivity());
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        NoteDataSource noteDataSource = new NoteDataSource(sqLiteDatabase);
        noteDataSource.delete(mNote);
        helper.close();
        sqLiteDatabase.close();
        returnMain();
    }


}
