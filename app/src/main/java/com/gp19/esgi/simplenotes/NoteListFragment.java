package com.gp19.esgi.simplenotes;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.app.ListFragment;
import android.text.TextUtils;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;


import com.gp19.esgi.simplenotes.dummy.DummyContent;
import com.gp19.esgi.simplenotes.loader.SQLiteNoteDataLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p>
 * <p>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class NoteListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<Note>> {

    private static final int LOADER_ID = 1;
    private List<Note> listNotes;
    private OnFragmentInteractionListener mListener;
    private static View view;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NoteListFragment() {
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
        EndlessAdapter adapter = new EndlessAdapter(getActivity(), new ArrayList<Note>(), R.layout.row_layout);
        setListAdapter(adapter);
        getLoaderManager().initLoader(LOADER_ID, null, this);

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


    @Override
    public void onLoadFinished(Loader<List<Note>> loader, List<Note> data) {
        listNotes.addAll(data);
        ((EndlessAdapter) getListAdapter()).addAll(data);

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
                if (tmpNote.getNoteTitle().toLowerCase().contains(query.toLowerCase()) && (((tmpNote.isArchived() && ((MainActivity) getActivity()).checkedArchived)) || !tmpNote.isArchived()))
                {
                    ((EndlessAdapter) getListAdapter()).add(tmpNote);
                }
            }
        }
        else ((EndlessAdapter) getListAdapter()).addAll(listNotes);
}
}