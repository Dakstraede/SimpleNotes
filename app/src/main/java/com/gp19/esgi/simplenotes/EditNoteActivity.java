package com.gp19.esgi.simplenotes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


public class EditNoteActivity extends Activity {

    private EditText title;
    private EditText content;
    private Spinner importance;
    private Spinner group;
    private TextView creationDate;
    private TextView lastModification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        title = (EditText) findViewById(R.id.edit_title);
        content = (EditText) findViewById(R.id.edit_content);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final Intent intent = getIntent();
        if (intent != null && intent.hasExtra(MainActivity.MY_NOTE)){
            Note myNote = intent.getParcelableExtra(MainActivity.MY_NOTE);
            title.setText(myNote.getNoteTitle());
            content.setText(myNote.getNoteContent());
        }
    }
}
