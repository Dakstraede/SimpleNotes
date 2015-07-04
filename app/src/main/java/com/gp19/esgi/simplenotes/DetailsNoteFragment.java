package com.gp19.esgi.simplenotes;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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
    private Note note;

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

        title = (EditText) view.findViewById(R.id.edit_title);
        content = (EditText) view.findViewById(R.id.edit_content);
        importance = (Spinner) view.findViewById(R.id.importance_edit_spinner);
        adapter = new ArrayAdapter<Integer>(getActivity(),android.R.layout.simple_spinner_dropdown_item, items);
        importance.setAdapter(adapter);
        creationDate = (TextView) view.findViewById(R.id.creation_date_label);
        lastModification = (TextView) view.findViewById(R.id.modification_date_label);
        checkArchived = (CheckBox) view.findViewById(R.id.checkBox_archived);

        title.setText(mNote.getNoteTitle());
        return view;
    }


}
