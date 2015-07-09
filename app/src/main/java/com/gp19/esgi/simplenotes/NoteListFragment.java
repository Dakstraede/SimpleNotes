package com.gp19.esgi.simplenotes;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Build;
import android.os.Bundle;
import android.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;


import com.gp19.esgi.simplenotes.database.NoteDataSource;
import com.gp19.esgi.simplenotes.loader.SQLiteNoteDataLoader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p>
 * <p>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class NoteListFragment extends ListFragment implements AdapterView.OnItemSelectedListener, LoaderManager.LoaderCallbacks<List<Note>>, SearchView.OnCloseListener {
    private static final String KEY_NOTES = "NOTES";
    private static final String SEARCH_QUERY = "SEARCH_QUERY";
    private static  final String SHOW_ARCHIVED = "ARCHIVED";
    private static final int LOADER_ID = 1;
    private List<Note> listNotes;
    private OnFragmentInteractionListener mListener;
    private NoteGroup noteGroup;
//    private static View view;
    private boolean archived;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NoteListFragment() {
    }

    public static NoteListFragment newInstance(NoteGroup noteGroupSelected){
        NoteListFragment noteListFragment = new NoteListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("NOTEGROUP", noteGroupSelected);
        noteListFragment.setArguments(bundle);
        return noteListFragment;
    }


    public static NoteListFragment newInstance(boolean showArchived){
        NoteListFragment noteListFragment = new NoteListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(SHOW_ARCHIVED, showArchived);
        noteListFragment.setArguments(bundle);
        return noteListFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.endless_list_layout, container, false);
        setHasOptionsMenu(true);
        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(SHOW_ARCHIVED))
            archived = getArguments().getBoolean(SHOW_ARCHIVED);
        else if (getArguments().containsKey("NOTEGROUP"))
            noteGroup = getArguments().getParcelable("NOTEGROUP");
        this.getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EndlessAdapter adapter;
        if (savedInstanceState != null){
            listNotes = savedInstanceState.getParcelableArrayList(KEY_NOTES);
        }
        adapter = new EndlessAdapter(getActivity(), new ArrayList<Note>(), R.layout.row_layout);
        this.setListAdapter(adapter);
        Spinner spinner = (Spinner) getActivity().findViewById(R.id.sort_spinner);
        spinner.setOnItemSelectedListener(this);
        getListView().setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            private int r;

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                if (checked) {
                    r++;
                    ((EndlessAdapter) getListAdapter()).setNewSelection(position, checked);
                } else {
                    r--;
                    ((EndlessAdapter) getListAdapter()).removeSelection(position);
                }
                mode.setTitle(r + " " + getResources().getString(R.string.num_selected_items));
            }
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                r = 0;
                MenuInflater inflater = getActivity().getMenuInflater();
                if (((MainActivity) getActivity()).lastSelected) inflater.inflate(R.menu.contextual_menu_main_archived, menu);
                else inflater.inflate(R.menu.contextual_menu_main, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.remove_item_context:
                        r = 0;
                        ((EndlessAdapter) getListAdapter()).deleteSelectedItems();
                        ((EndlessAdapter) getListAdapter()).clearSelection();
                        restartLoader();
                        mode.finish();
                    case R.id.archive_item_context:
                        r = 0;
                        ((EndlessAdapter) getListAdapter()).archiveSelectedItems(true);
                        ((EndlessAdapter) getListAdapter()).clearSelection();
                        restartLoader();
                        mode.finish();
                    case R.id.undo_archive_item_context:
                        r = 0;
                        ((EndlessAdapter) getListAdapter()).archiveSelectedItems(false);
                        ((EndlessAdapter) getListAdapter()).clearSelection();
                        restartLoader();
                        mode.finish();

                    case R.id.note_duplicate_context:
                        r = 0;
                        ((EndlessAdapter) getListAdapter()).duplicateSelectedItems();
                        ((EndlessAdapter) getListAdapter()).clearSelection();
                        restartLoader();
                        mode.finish();
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                ((EndlessAdapter) getListAdapter()).clearSelection();
            }
        });

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                getListView().setItemChecked(position, !((EndlessAdapter) getListAdapter()).isPositionChecked(position));
                return false;
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void restartLoader()
    {
        getLoaderManager().destroyLoader(LOADER_ID);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(((Note) getListAdapter().getItem(position)));
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Note selectedNote);
    }

    @Override
    public Loader<List<Note>> onCreateLoader(int i, Bundle bundle) {

        if (noteGroup != null){
            return new SQLiteNoteDataLoader(getActivity().getApplicationContext(),((MainActivity) getActivity()).noteDataSource, new String[]{String.valueOf(noteGroup.getId())}, true );
        }
        else{
            return new SQLiteNoteDataLoader(getActivity().getApplicationContext(),
                    ((MainActivity) getActivity()).noteDataSource, NoteDataSource.COLUMN_NOTE_ARCHIVE + "=?", new String[] { String.valueOf((archived) ? 1 : 0)}, null, null, null);

        }

    }

    @Override
    public void onLoadFinished(Loader<List<Note>> loader, List<Note> data) {
        this.listNotes = new ArrayList<>(data);
        ((EndlessAdapter) this.getListAdapter()).clear();
        ((EndlessAdapter) this.getListAdapter()).addAll(this.listNotes);
    }

    @Override
    public void onLoaderReset(Loader<List<Note>> loader) {
        ((EndlessAdapter) getListAdapter()).clear();
        listNotes.clear();
    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        EndlessAdapter adapter = ((EndlessAdapter) getListAdapter());
        switch (position){
            case 0:
                adapter.sort(new Comparator<Note>() {
                    @Override
                    public int compare(Note lhs, Note rhs) {
                        return lhs.getNoteTitle().toLowerCase().compareTo(rhs.getNoteTitle().toLowerCase());
                    }
                });
                break;
            case 1:
                adapter.sort(new Comparator<Note>() {
                    @Override
                    public int compare(Note lhs, Note rhs) {
                        return lhs.getCreationDate().compareTo(rhs.getCreationDate());
                    }
                });
                break;
            case 2:
                adapter.sort(new Comparator<Note>() {
                    @Override
                    public int compare(Note lhs, Note rhs) {
                        if (lhs.getLastModificationDate() == null && rhs.getLastModificationDate() == null) {
                            return 0;
                        } else if (lhs.getLastModificationDate() != null && rhs.getLastModificationDate() == null) {
                            return -1;
                        } else if (lhs.getLastModificationDate() == null && rhs.getLastModificationDate() != null) {
                            return 1;
                        } else {
                            return lhs.getLastModificationDate().compareTo(rhs.getLastModificationDate());
                        }
                    }
                });
                break;
            case 3:
                adapter.sort(new Comparator<Note>() {
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
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        menu.clear();

        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu.findItem(R.id.action_search) == null) {
            getActivity().getMenuInflater().inflate(R.menu.activity_itemlist, menu);

            MenuItem searchItem = menu.findItem(R.id.action_search);
            final SearchView searchView = (SearchView) searchItem.getActionView();
            final SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    ((EndlessAdapter) getListAdapter()).getFilter().filter(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    ((EndlessAdapter) getListAdapter()).getFilter().filter(newText);
                    return true;
                }
            };
            searchView.setOnQueryTextListener(listener);
            searchView.setQueryHint(getResources().getString(R.string.search_hint));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_NOTES, ((ArrayList<Note>) listNotes));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.add_item:
                FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();
                AddNoteFragment addNoteFragment = new AddNoteFragment();
                fragmentTransaction.replace(R.id.content_frame, addNoteFragment, "AddNoteFragment");
                fragmentTransaction.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}