package com.gp19.esgi.simplenotes;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.List;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;

import com.gp19.esgi.simplenotes.database.DBHelper;
import com.gp19.esgi.simplenotes.database.NoteDataSource;
import com.gp19.esgi.simplenotes.loader.SQLiteNoteDataLoader;


public class MainActivity extends Activity implements EndlessNoteListView.EndlessListener, LoaderManager.LoaderCallbacks<List<?>>{
    private static final int LOADER_ID = 1;
    private final static int ITEM_PER_REQUEST = 10;
    private SQLiteDatabase sqLiteDatabase;
    private NoteDataSource noteDataSource;
    private DBHelper helper;
    EndlessNoteListView listView;
    int mult = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new DBHelper(this);
        sqLiteDatabase = helper.getWritableDatabase();
        noteDataSource = new NoteDataSource(sqLiteDatabase);
        noteDataSource.insert(new Note("pa", "ss"));
        noteDataSource.insert(new Note("Ma note 2", "Liste d'achats"));

        listView = (EndlessNoteListView) findViewById(R.id.el);
        EndlessAdapter adapter = new EndlessAdapter(this, noteDataSource.read(), R.layout.row_layout);
        listView.setLoadingView(R.layout.loading_layout);
        listView.setAdapter(adapter);
        listView.setListener(this);

        getLoaderManager().initLoader(LOADER_ID, null, this);

    }

    @Override
    public Loader<List<?>> onCreateLoader(int id, Bundle args) {
        SQLiteNoteDataLoader loader = new SQLiteNoteDataLoader(this, noteDataSource, null, null, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<?>> loader, List<?> data) {

    }

    @Override
    public void onLoaderReset(Loader<List<?>> loader) {

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

//    private class FakeNetLoader extends AsyncTask<String, Void, List<String>> {
//        @Override
//        protected List<String> doInBackground(String... params) {
//            try {
//                Thread.sleep(4000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return createItems(mult);
//        }
//        @Override
//        protected void onPostExecute(List<String> result) {
//            super.onPostExecute(result);
//            listView.addNewData(result);
//        }
//    }
//    private List<Note> createItems(List<Note> aa) {
//        List<Note> result = new ArrayList<Note>();
//        Note ne = new Note("ss", "ds");
//        Note nn = new Note("ddd", "sdsd");
//        for (int i=0; i < ITEM_PER_REQUEST; i++) {
//            result.add();
//        }
//        return result;
//    }

    @Override
    public void loadData() {
        mult += 10;

    }

}
