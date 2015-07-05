package com.gp19.esgi.simplenotes;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment{

//    private static View view;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        getFragmentManager().beginTransaction().add(R.id.main_fragment, new NoteListFragment(), "NoteListFragment").commit();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NoteListFragment fragment = ((NoteListFragment) getFragmentManager().findFragmentByTag("NoteListFragment"));
        if (fragment != null && !getActivity().isDestroyed()) {
            FragmentManager fr = getFragmentManager();
            if (fr != null) {

                fr.beginTransaction().remove(fragment).commit();
            }
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_itemlist, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.add_item:
                FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();
                AddNoteFragment addNoteFragment = new AddNoteFragment();
                fragmentTransaction.replace(R.id.rootLayout, addNoteFragment, "AddNoteFragment");
                fragmentTransaction.addToBackStack("ADDNOTE");
                fragmentTransaction.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
