package com.gp19.esgi.simplenotes;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.gp19.esgi.simplenotes.loader.SQLiteNoteGroupLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class GroupFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<NoteGroup>>{
    private final int LOADER_ID = 3;
    private OnFragmentInteractionListener mListener;
    private static final String NOTE = "NOTE";
    public Note currentNote;
    private NoteGroupAttachAdapter adapter;
    private View header;
    private EditText editText;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GroupFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_attach_group, menu);
        MenuItem newGroupItem = menu.findItem(R.id.action_new_group);
        getActivity().getActionBar().setDisplayShowCustomEnabled(true);
        editText= (EditText) newGroupItem.getActionView();
        editText.setPadding(70,0,22,0);
        editText.setHint("New group");
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                    if (s.length() > 0){

                        if (getListView().getHeaderViewsCount() == 0){
                            getListView().addHeaderView(header);
                            header.setClickable(true);
                            header.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ((MainActivity) getActivity()).noteDataSource.insert(new NoteGroup(String.valueOf(s)));
                                    getListView().removeHeaderView(header);
                                    editText.setText(null);
                                    editText.clearFocus();
                                    getLoaderManager().restartLoader(LOADER_ID, null, ((GroupFragment) getFragmentManager().findFragmentByTag("GroupFragment")));
                                    getLoaderManager().restartLoader(2, null, ((MainActivity) getActivity()));
                                }
                            });

                        }
                        TextView t = ((TextView) header.findViewById(R.id.textView2));
                        t.setText(getResources().getString(R.string.create_label) + " : '" + s + "'");
                    }
                    else {
                        if (getListView().getHeaderViewsCount() > 0){
                            getListView().removeHeaderView(header);
                        }
                    }
                }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public static GroupFragment newInstance(Note selectedNote){
        GroupFragment groupFragment = new GroupFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(NOTE, selectedNote);
        groupFragment.setArguments(bundle);
        return groupFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.group_list_selection_layout, container, false);
        setHasOptionsMenu(true);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        adapter = new NoteGroupAttachAdapter(getActivity(), R.layout.group_row_layout, new ArrayList<NoteGroup>(), ((MainActivity) getActivity()).noteDataSource.read(currentNote));
        this.setListAdapter(adapter);
        getLoaderManager().initLoader(LOADER_ID, null, this);
        header = getListView().inflate(getActivity().getBaseContext(), R.layout.create_new_layout, null);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(NOTE)) this.currentNote = getArguments().getParcelable(NOTE);
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
        CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox2);
        checkBox.setChecked(!checkBox.isChecked());
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(currentNote, adapter.getItem(position), checkBox.isChecked());
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Note currentNote, NoteGroup noteGroup, boolean checked);
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
        return new SQLiteNoteGroupLoader(getActivity(), ((MainActivity) getActivity()).noteDataSource, null,null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<List<NoteGroup>> loader, List<NoteGroup> data) {
        adapter.clear();
        adapter.addAll(data);
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

    private void returnDetails(){
        getFragmentManager().beginTransaction().replace(R.id.content_frame, DetailsNoteFragment.newInstance(currentNote), "NoteDetailsFragment").commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.done_attach:
                returnDetails();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
