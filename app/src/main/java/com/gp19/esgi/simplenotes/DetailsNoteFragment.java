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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mNote = getArguments().getParcelable(KEY);
        View view = inflater.inflate(R.layout.fragment_details_note, container, false);
        setHasOptionsMenu(true);
        title = (EditText) view.findViewById(R.id.edit_title);
        content = (EditText) view.findViewById(R.id.edit_content);
        importance = (Spinner) view.findViewById(R.id.importance_edit_spinner);
        adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item, items);
        importance.setAdapter(adapter);
        TextView creationDate = (TextView) view.findViewById(R.id.creation_date_label);
        TextView lastModification = (TextView) view.findViewById(R.id.modification_date_label);
        if(mNote != null){
            title.setText(mNote.getNoteTitle());
            content.setText(mNote.getNoteContent());
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
        getFragmentManager().beginTransaction().replace(R.id.content_frame, NoteListFragment.newInstance(((MainActivity) getActivity()).lastSelected), "NoteListFragment").commit();
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

    private void duplicateNote(){
        DBHelper helper = new DBHelper(getActivity());
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        NoteDataSource noteDataSource = new NoteDataSource(sqLiteDatabase);
        noteDataSource.insert(new Note(mNote.getNoteTitle(), mNote.getNoteContent(), mNote.getImportanceLevel(), mNote.isArchived()));
        helper.close();
        sqLiteDatabase.close();
        returnMain();
    }

    private void attachGroup(){
        GroupFragment groupFragment = GroupFragment.newInstance(mNote);
        getFragmentManager().beginTransaction().replace(R.id.content_frame, groupFragment, "GroupFragment").addToBackStack("GROUP").commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (mNote.isArchived()){
            getActivity().getMenuInflater().inflate(R.menu.note_details_menu_archived, menu);
        }
        else getActivity().getMenuInflater().inflate(R.menu.note_details_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.remove_item:
                removeNote();
                return true;
            case R.id.archive_item:
                mNote.setArchived(true);
                saveNote();
                return true;
            case R.id.unarchive_item:
                mNote.setArchived(false);
                saveNote();
                return true;
            case R.id.done_edit:
                saveNote();
                return true;
            case R.id.note_duplicate:
                duplicateNote();
                return true;
            case R.id.note_add_group:
                attachGroup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
