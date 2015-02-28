package com.gp19.esgi.simplenotes;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.gp19.esgi.simplenotes.database.DBHelper;
import com.gp19.esgi.simplenotes.database.NoteDataSource;
import com.gp19.esgi.simplenotes.loader.SQLiteNoteDataLoader;


public class MainActivity extends Activity implements EndlessNoteListView.EndlessListener, SearchView.OnQueryTextListener,SearchView.OnCloseListener, LoaderManager.LoaderCallbacks<List<?>>, AdapterView.OnItemSelectedListener{
    private static final int LOADER_ID = 1;
    private final static int ITEM_PER_REQUEST = 4;
    private SQLiteDatabase sqLiteDatabase;
    private NoteDataSource noteDataSource;
    private DBHelper helper;
    private SearchView searchView;
    EndlessNoteListView listView;
    private EndlessAdapter adapter;
    private int last;
    private List<Note> l;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        l = new ArrayList<Note>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new DBHelper(this);
        sqLiteDatabase = helper.getWritableDatabase();
        noteDataSource = new NoteDataSource(sqLiteDatabase);
        listView = (EndlessNoteListView) findViewById(R.id.el);
        adapter = new EndlessAdapter(this, new ArrayList<Note>(), R.layout.row_layout);
        listView.setLoadingView(R.layout.loading_layout);
        listView.setAdapter(adapter);
        listView.setListener(this);
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        spinner = (Spinner) findViewById(R.id.sort_spinner);
        spinner.setOnItemSelectedListener(this);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<List<?>> onCreateLoader(int id, Bundle args) {
        SQLiteNoteDataLoader loader = new SQLiteNoteDataLoader(this, noteDataSource, null, null, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<?>> loader, List<?> data) {
        adapter.clear();
        l = new ArrayList(data);
        last = 0;
    }

    @Override
    public void onLoaderReset(Loader<List<?>> loader) {
        l.clear();
        adapter.clear();
        last = 0;

    }

    @Override
    public boolean onClose() {
        if (listView.setListener() == null)
        {
            listView.setListener(this);
        }
        return false;
    }

    private void displayResult(String query){
        List<Note> newList = new ArrayList<Note>();
        if (!TextUtils.isEmpty(query)){

            for (Note note : l)
            {
                if (note.getNoteTitle().toLowerCase().contains(query.toLowerCase()))
                {
                    newList.add(note);
                }
            }
            last = 0;
            adapter.clear();
            listView.addNewData(newList);
        }
        else{
            last = 0;
            adapter.clear();
            loadData();
        }
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        if (listView.setListener() != null)
        {
            listView.setListener(null);
        }
        displayResult(query);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (listView.setListener() != null)
        {
            listView.setListener(null);
        }
        displayResult(newText);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void loadData() {
        if (adapter.getCount() < l.size())
        {
            List<Note> li = new ArrayList<Note>();
            int i;
            for(i = last; i < last + ITEM_PER_REQUEST -1 && i < l.size(); i++)
            {
                li.add(l.get(i));
            }
            last = i;
            listView.addNewData(li);
        }
        else listView.removeFooter();
    }

    public void addNote(View view) {
        Intent intent = new Intent(this, AddNoteActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                adapter.setComparator(new Comparator<Note>() {
                    @Override
                    public int compare(Note lhs, Note rhs) {
                        return lhs.getNoteTitle().toLowerCase().compareTo(rhs.getNoteTitle().toLowerCase());
                    }
                });
                break;
            case 1:
                adapter.setComparator(new Comparator<Note>() {
                    @Override
                    public int compare(Note lhs, Note rhs) {
                        return lhs.getCreationDate().compareTo(rhs.getCreationDate());
                    }
                });
                break;
            case 2:
                adapter.setComparator(new Comparator<Note>() {
                    @Override
                    public int compare(Note lhs, Note rhs) {
                        return lhs.getLastModicationDate().compareTo(rhs.getLastModicationDate());
                    }
                });
                break;
            case 3:
                adapter.setComparator(new Comparator<Note>() {
                    @Override
                    public int compare(Note lhs, Note rhs) {
                        if (lhs.getImportanceLevel() < rhs.getImportanceLevel()){
                            return 1;
                        }
                        else return -1;
                    }
                });
                break;
            }
        }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
