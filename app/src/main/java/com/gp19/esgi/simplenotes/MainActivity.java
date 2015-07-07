package com.gp19.esgi.simplenotes;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gp19.esgi.simplenotes.database.DBHelper;
import com.gp19.esgi.simplenotes.database.NoteDataSource;


public class MainActivity extends Activity implements NoteListFragment.OnFragmentInteractionListener{
    private SQLiteDatabase sqLiteDatabase;
    public NoteDataSource noteDataSource;
    private DBHelper helper;
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    public boolean lastSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();

        String[] items = getResources().getStringArray(R.array.drawer_item_list);
        mDrawerLayout = ((DrawerLayout) findViewById(R.id.drawer_layout));
        mDrawerList = ((ListView) findViewById(R.id.left_drawer));
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, items));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
//        getActionBar().setDisplayHomeAsUpEnabled(false);
        helper = new DBHelper(this);
        sqLiteDatabase = helper.getWritableDatabase();
        noteDataSource = new NoteDataSource(sqLiteDatabase);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.archived_check,  /* "open drawer" description for accessibility */
                R.string.archived_check  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if(savedInstanceState == null)
        {
            selectItem(0);
        }

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
        fragmentTransaction.replace(R.id.content_frame, detailsNoteFragment, "NoteDetailsFragment");
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().findFragmentByTag("NoteDetailsFragment") != null || getFragmentManager().findFragmentByTag("AddNoteFragment") != null) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, NoteListFragment.newInstance(lastSelected), "NoteListFragment").commit();
        } else super.onBackPressed();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if (menu.findItem(R.id.action_search) != null) menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return false;
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position)
    {
        switch (position){
            case 0:
                lastSelected = false;
                NoteListFragment noteListFragment = NoteListFragment.newInstance(lastSelected);
                getFragmentManager().beginTransaction().replace(R.id.content_frame, noteListFragment, "NoteListFragment").commit();
                break;
            case 1:
                lastSelected = true;
                NoteListFragment noteListFragmentA = NoteListFragment.newInstance(lastSelected);
                getFragmentManager().beginTransaction().replace(R.id.content_frame, noteListFragmentA, "NoteListFragment").commit();
                break;
        }
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
}
