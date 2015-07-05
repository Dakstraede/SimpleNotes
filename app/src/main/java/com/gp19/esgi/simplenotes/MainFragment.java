package com.gp19.esgi.simplenotes;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
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

        View view = inflater.inflate(R.layout.main_fragment, container, false);

        getFragmentManager().beginTransaction().add(R.id.main_fragment, new NoteListFragment(), "NoteListFragment").commit();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NoteListFragment fragment = ((NoteListFragment) getFragmentManager().findFragmentByTag("NoteListFragment"));
        if (fragment != null){
            getFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }
}
