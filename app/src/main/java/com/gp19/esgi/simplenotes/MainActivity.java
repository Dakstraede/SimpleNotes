package com.gp19.esgi.simplenotes;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.List;
import android.os.AsyncTask;
import android.view.View;


public class MainActivity extends Activity implements EndlessNoteListView.EndlessListener{

    private final static int ITEM_PER_REQUEST = 10;
    EndlessNoteListView listView;
    int mult = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (EndlessNoteListView) findViewById(R.id.el);
        EndlessAdapter adapter = new EndlessAdapter(this, createItems(mult), R.layout.row_layout);
        listView.setLoadingView(R.layout.loading_layout);
        listView.setAdapter(adapter);
        listView.setListener(this);

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
    private List<String> createItems(int mult) {
        List<String> result = new ArrayList<String>();
        for (int i=0; i < ITEM_PER_REQUEST; i++) {
            result.add("Item " + (i * mult));
        }
        return result;
    }

//    @Override
    public void loadData() {
        mult += 10;
//        // We load more data here
//        FakeNetLoader fl = new FakeNetLoader();
//        fl.execute(new String[]{});
    }

}
