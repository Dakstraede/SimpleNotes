package com.gp19.esgi.simplenotes;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;


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
public class NoteListFragment extends ListFragment implements AdapterView.OnItemSelectedListener, LoaderManager.LoaderCallbacks<List<Note>>, SearchView.OnQueryTextListener,SearchView.OnCloseListener {

    private static final int LOADER_ID = 1;
    private List<Note> listNotes;
    private OnFragmentInteractionListener mListener;
    private static View view;
    private boolean checked;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NoteListFragment() {
    }

    public void updateListonCheckStatusChange(boolean checked)
    {
        ((EndlessAdapter) getListAdapter()).clear();
        this.checked = checked;
        addData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.endless_list_layout, container, false);
        } catch (InflateException e) {
        }
        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listNotes = new ArrayList<>();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EndlessAdapter adapter = new EndlessAdapter(getActivity().getBaseContext(), new ArrayList<Note>(), R.layout.row_layout);
        setListAdapter(adapter);
        getLoaderManager().initLoader(LOADER_ID, null, this);
        SearchView searchView = (SearchView) getActivity().findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        Spinner spinner = (Spinner) getActivity().findViewById(R.id.sort_spinner);
        spinner.setOnItemSelectedListener(this);
        CheckBox checkBox = (CheckBox) getActivity().findViewById(R.id.checkBox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    updateListonCheckStatusChange(isChecked);
                }
            });
    }

    @Override
    public void onStart() {
        super.onStart();
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
        return new SQLiteNoteDataLoader(getActivity().getApplicationContext(), ((MainActivity) getActivity()).noteDataSource, null, null, null, null, null);
    }
    private void addData(){
        EndlessAdapter adapter = (EndlessAdapter) getListAdapter();
        for (Note tmpNote : listNotes)
        {
            if (tmpNote.isArchived() && this.checked || !tmpNote.isArchived())
            {
                adapter.add(tmpNote);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((EndlessAdapter) getListAdapter()).clear();
        addData();
        ((EndlessAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onLoadFinished(Loader<List<Note>> loader, List<Note> data) {
        listNotes.clear();
        ((EndlessAdapter) getListAdapter()).clear();
        listNotes.addAll(data);
        addData();
    }

    @Override
    public void onLoaderReset(Loader<List<Note>> loader) {
        ((EndlessAdapter) getListAdapter()).clear();
        listNotes.clear();
    }

    public void displayResult(String query){
        ((EndlessAdapter) getListAdapter()).clear();
        if (!TextUtils.isEmpty(query)){
            for (Note tmpNote : listNotes)
            {
                if (tmpNote.getNoteTitle().toLowerCase().contains(query.toLowerCase()) && (((tmpNote.isArchived() && this.checked)) || !tmpNote.isArchived()))
                {
                    ((EndlessAdapter) getListAdapter()).add(tmpNote);
                }
            }
        }
        else addData();
        ((EndlessAdapter) getListAdapter()).notifyDataSetChanged();
}

    @Override
    public boolean onClose() {
        return false;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        displayResult(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        displayResult(newText);
        return true;
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
}