package com.gp19.esgi.simplenotes;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.gp19.esgi.simplenotes.database.DBHelper;
import com.gp19.esgi.simplenotes.database.NoteDataSource;
import com.gp19.esgi.simplenotes.loader.SQLiteNoteGroupLoader;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements NoteListFragment.OnFragmentInteractionListener, GroupFragment.OnFragmentInteractionListener, LoaderManager.LoaderCallbacks<List<NoteGroup>>{
    private static final int LOADER_ID = 2;
    private SQLiteDatabase sqLiteDatabase;
    public NoteDataSource noteDataSource;
    private DBHelper helper;
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private DrawerGroupAdapter groupArrayAdapter;
    public boolean lastSelected;
    private ListView groupList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();
        groupList = ((ListView) findViewById(R.id.left_group_drawer));
        groupArrayAdapter = new DrawerGroupAdapter(this, R.layout.drawer_list_item, new ArrayList<NoteGroup>());
        groupList.setAdapter(groupArrayAdapter);
        groupList.setOnItemClickListener(new DrawerGroupItemClickListener());
        helper = new DBHelper(this);
        sqLiteDatabase = helper.getWritableDatabase();
        noteDataSource = new NoteDataSource(sqLiteDatabase);
        String[] items = getResources().getStringArray(R.array.drawer_item_list);
        mDrawerLayout = ((DrawerLayout) findViewById(R.id.drawer_layout));
        mDrawerList = ((ListView) findViewById(R.id.left_drawer));
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, items));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        this.getLoaderManager().initLoader(LOADER_ID, null, this);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
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
        helper = new DBHelper(this);
        sqLiteDatabase = helper.getWritableDatabase();
        noteDataSource = new NoteDataSource(sqLiteDatabase);
        getLoaderManager().restartLoader(LOADER_ID, null, this);
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
    public void onFragmentInteraction(Note selectedNote) {
        FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();
        DetailsNoteFragment detailsNoteFragment = DetailsNoteFragment.newInstance(selectedNote);
        fragmentTransaction.replace(R.id.content_frame, detailsNoteFragment, "NoteDetailsFragment");
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Note currentNote, NoteGroup group, boolean checked) {
        if (checked) noteDataSource.insert(group, currentNote);
        else noteDataSource.delete(group, currentNote);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().findFragmentByTag("GroupFragment").isVisible())
        {
            getFragmentManager().popBackStack("GROUP", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        else if (getFragmentManager().findFragmentByTag("NoteDetailsFragment") != null || getFragmentManager().findFragmentByTag("AddNoteFragment") != null) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, NoteListFragment.newInstance(lastSelected), "NoteListFragment").commit();
        }
        else super.onBackPressed();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(findViewById(R.id.layout_sub_drawer));
        if (menu.findItem(R.id.action_search) != null) menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item);
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

    private class DrawerGroupItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectGroupItem(position);
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectGroupItem(int position)
    {
        NoteListFragment noteListFragment = NoteListFragment.newInstance(groupArrayAdapter.getItem(position));
        getFragmentManager().beginTransaction().replace(R.id.content_frame, noteListFragment, "NoteListFragment").commit();
        mDrawerList.setItemChecked(mDrawerList.getCheckedItemPosition(), false);
        groupList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(findViewById(R.id.layout_sub_drawer));

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
        if (groupList.getCheckedItemCount() > 0){
            groupList.setItemChecked(groupList.getCheckedItemPosition(), false);
        }
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(findViewById(R.id.layout_sub_drawer));
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<List<NoteGroup>> onCreateLoader(int id, Bundle args) {
        return new SQLiteNoteGroupLoader(this, noteDataSource, null, null, null, null,null);
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.  See {@link FragmentManager#beginTransaction()
     * FragmentManager.openTransaction()} for further discussion on this.
     * <p/>
     * <p>This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     * <p/>
     * <ul>
     * <li> <p>The Loader will monitor for changes to the data, and report
     * them to you through new calls here.  You should not monitor the
     * data yourself.  For example, if the data is a {@link Cursor}
     * and you place it in a {@link CursorAdapter}, use
     * the {@link CursorAdapter#CursorAdapter(Context,
     * Cursor, int)} constructor <em>without</em> passing
     * in either {@link CursorAdapter#FLAG_AUTO_REQUERY}
     * or {@link CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER}
     * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
     * from doing its own observing of the Cursor, which is not needed since
     * when a change happens you will get a new Cursor throw another call
     * here.
     * <li> The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a {@link Cursor} from a {@link CursorLoader},
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * {@link CursorAdapter}, you should use the
     * {@link CursorAdapter#swapCursor(Cursor)}
     * method so that the old Cursor is not closed.
     * </ul>
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<List<NoteGroup>> loader, List<NoteGroup> data) {
        groupArrayAdapter.clear();
        groupArrayAdapter.addAll(data);
        groupArrayAdapter.notifyDataSetChanged();
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<List<NoteGroup>> loader) {
    }
}
