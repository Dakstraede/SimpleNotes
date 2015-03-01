package com.gp19.esgi.simplenotes;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.gp19.esgi.simplenotes.database.DBHelper;
import com.gp19.esgi.simplenotes.database.NoteDataSource;


public class AddNoteActivity extends ActionBarActivity {

    private static final Integer[] items = new Integer[]{1,2,3};
    private ArrayAdapter<Integer> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        Spinner s = (Spinner) findViewById(R.id.spinner_importance);
        adapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_dropdown_item, items);
        s.setAdapter(adapter);
    }
    public void saveNote(View view){

        EditText titleZone = (EditText) findViewById(R.id.note_edit);
        String titleText =  titleZone.getText().toString();

        EditText contentZone = (EditText) findViewById(R.id.note_content);
        String contentText = contentZone.getText().toString();

        Spinner s = (Spinner) findViewById(R.id.spinner_importance);
        int imp = adapter.getItem(s.getSelectedItemPosition());

        DBHelper helper = new DBHelper(this);
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        NoteDataSource noteDataSource = new NoteDataSource(sqLiteDatabase);
        noteDataSource.insert(new Note(titleText, contentText, imp));
        helper.close();
        sqLiteDatabase.close();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
