package com.gp19.esgi.simplenotes;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.SearchView;
import android.widget.Spinner;

import java.util.Comparator;
import java.util.List;
import com.gp19.esgi.simplenotes.database.DBHelper;
import com.gp19.esgi.simplenotes.database.NoteDataSource;


public class MainActivity extends Activity implements NoteListFragment.OnFragmentInteractionListener, SearchView.OnQueryTextListener,SearchView.OnCloseListener, AdapterView.OnItemSelectedListener{
    public final static int ITEM_PER_REQUEST = 7;
    public final static String MY_NOTE = "MY_NOTE";
    private SQLiteDatabase sqLiteDatabase;
    public NoteDataSource noteDataSource;
    private DBHelper helper;
    public int last;
    private List<Note> l;
    public boolean checkedArchived;
    public int nbarch = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new DBHelper(this);
        sqLiteDatabase = helper.getWritableDatabase();
        noteDataSource = new NoteDataSource(sqLiteDatabase);


        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        Spinner spinner = (Spinner) findViewById(R.id.sort_spinner);
        spinner.setOnItemSelectedListener(this);

        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
//        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                checkedArchived = isChecked;
//                last = 0;
//                ((NoteFragment) getFragmentManager().findFragmentByTag("NoteListFragment")).getAdapter().clear();
////                ((NoteFragment) getFragmentManager().findFragmentByTag("NoteListFragment")).loadData();
//            }
//        });

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
//                intent.putExtra(MY_NOTE, (Note) adapter.getItem(position));
//                startActivity(intent);
//            }
//        });

        FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();
        NoteListFragment noteListFragment = new NoteListFragment();
        fragmentTransaction.add(R.id.rootLayout, noteListFragment, "NoteListFragment");
        fragmentTransaction.commit();
    }


    @Override
    public boolean onClose() {
        return false;
    }




    @Override
    public boolean onQueryTextSubmit(String query) {
        ((NoteListFragment) getFragmentManager().findFragmentByTag("NoteListFragment")).displayResult(query);
//        if (listView.setListener() != null)
//        {
//            listView.setListener(null);
//        }


        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ((NoteListFragment) getFragmentManager().findFragmentByTag("NoteListFragment")).displayResult(newText);
//        if (listView.setListener() != null)
//        {
//            listView.setListener(null);
//        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.close();
        sqLiteDatabase.close();
        noteDataSource = null;
        helper = null;
        sqLiteDatabase = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        helper.close();
        sqLiteDatabase.close();
    }

    public void addNote(View view) {
        Intent intent = new Intent(this, AddNoteActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        EndlessAdapter listAdapter = ((EndlessAdapter) ((NoteListFragment) getFragmentManager().findFragmentByTag("NoteListFragment")).getListAdapter());
        switch (position){
            case 0:
                listAdapter.sort(new Comparator<Note>() {
                    @Override
                    public int compare(Note lhs, Note rhs) {
                        return lhs.getNoteTitle().toLowerCase().compareTo(rhs.getNoteTitle().toLowerCase());
                    }
                });
                break;
            case 1:
                listAdapter.sort(new Comparator<Note>() {
                    @Override
                    public int compare(Note lhs, Note rhs) {
                        return lhs.getCreationDate().compareTo(rhs.getCreationDate());
                    }
                });
                break;
            case 2:
                listAdapter.sort(new Comparator<Note>() {
                    @Override
                    public int compare(Note lhs, Note rhs) {
                        if (lhs.getLastModicationDate() == null && rhs.getLastModicationDate() == null) {
                            return 0;
                        } else if (lhs.getLastModicationDate() != null && rhs.getLastModicationDate() == null) {
                            return -1;
                        } else if (lhs.getLastModicationDate() == null && rhs.getLastModicationDate() != null) {
                            return 1;
                        } else {
                            return lhs.getLastModicationDate().compareTo(rhs.getLastModicationDate());
                        }
                    }
                });
                break;
            case 3:
                listAdapter.sort(new Comparator<Note>() {
                    @Override
                    public int compare(Note lhs, Note rhs) {
                        if (lhs.getImportanceLevel() < rhs.getImportanceLevel()) {
                            return 1;
                        } else return -1;
                    }
                });
                break;
            }
        }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onFragmentInteraction(String id) {
        Log.i("AAAAAAAAA", "BBBBBBBB");
    }
}
