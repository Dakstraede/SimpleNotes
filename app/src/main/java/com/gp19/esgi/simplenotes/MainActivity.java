package com.gp19.esgi.simplenotes;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import com.gp19.esgi.simplenotes.database.DBHelper;
import com.gp19.esgi.simplenotes.database.NoteDataSource;


public class MainActivity extends Activity implements NoteListFragment.OnFragmentInteractionListener{
    private SQLiteDatabase sqLiteDatabase;
    public NoteDataSource noteDataSource;
    private DBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setDisplayHomeAsUpEnabled(false);
        helper = new DBHelper(this);
        sqLiteDatabase = helper.getWritableDatabase();
        noteDataSource = new NoteDataSource(sqLiteDatabase);
        if (findViewById(R.id.rootLayout) != null)
        {
            if (savedInstanceState != null){
                return;
            }
        }
        FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();
        MainFragment mainFragment = new MainFragment();
        fragmentTransaction.addToBackStack("MAIN");
        fragmentTransaction.add(R.id.rootLayout, mainFragment, "MainFragment");
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    @Override
    public void onFragmentInteraction(Note selectedNote) {
        FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();
        DetailsNoteFragment detailsNoteFragment = DetailsNoteFragment.newInstance(selectedNote);
        fragmentTransaction.replace(R.id.rootLayout, detailsNoteFragment, "NoteDetailsFragment");
        fragmentTransaction.addToBackStack("A_B");
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().findFragmentByTag("MainFragment") != null)
        {
            getFragmentManager().popBackStackImmediate("A_B", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        else super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_action_bar, menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayShowHomeEnabled(false);
        }

        return true;
    }
}
