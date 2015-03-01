package com.gp19.esgi.simplenotes;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.gp19.esgi.simplenotes.database.DBHelper;
import com.gp19.esgi.simplenotes.database.NoteDataSource;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class EditNoteActivity extends Activity {

    private SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    private static final Integer[] items = new Integer[]{1,2,3};
    private ArrayAdapter<Integer> adapter;

    private EditText title;
    private EditText content;
    private Spinner importance;
    private Spinner group;
    private TextView creationDate;
    private TextView lastModification;
    private boolean modified ;
    private Note myNote;
    private CheckBox checkArchived;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        title = (EditText) findViewById(R.id.edit_title);
        content = (EditText) findViewById(R.id.edit_content);
        importance = (Spinner) findViewById(R.id.importance_edit_spinner);
        adapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_dropdown_item, items);
        importance.setAdapter(adapter);
        creationDate = (TextView) findViewById(R.id.creation_date_label);
        lastModification = (TextView) findViewById(R.id.modification_date_label);
        checkArchived = (CheckBox) findViewById(R.id.checkBox_archived);
        modified = false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        final Intent intent = getIntent();
        if (intent != null && intent.hasExtra(MainActivity.MY_NOTE)){
            myNote = intent.getParcelableExtra(MainActivity.MY_NOTE);
            title.setText(myNote.getNoteTitle());
            content.setText(myNote.getNoteContent());
            if (myNote.isArchived()){
                checkArchived.setChecked(true);
            }
            else checkArchived.setChecked(false);
            for (int i = 0; i < importance.getAdapter().getCount(); i++){
                if ((Integer)importance.getAdapter().getItem(i) == myNote.getImportanceLevel()){
                    importance.setSelection(i);
                    break;
                }
            }
            creationDate.setText(smp.format(myNote.getCreationDate()));
            if (myNote.getLastModicationDate() != null){
                lastModification.setText(smp.format(myNote.getLastModicationDate()));
            }
            else lastModification.setText("Not modified yet");
        }
    }

   public void saveNote(View view){
       myNote.setNoteTitle(title.getText().toString());
       myNote.setNoteContent(content.getText().toString());
       myNote.setLastModicationDate();
       if (checkArchived.isChecked()){
           myNote.setArchived(true);
       }
       else myNote.setArchived(false);
       myNote.setImportanceLevel((Integer)importance.getSelectedItem());
       DBHelper helper = new DBHelper(this);
       SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
       NoteDataSource noteDataSource = new NoteDataSource(sqLiteDatabase);
       noteDataSource.update(myNote);
       helper.close();
       sqLiteDatabase.close();
       Intent intent = new Intent(this, MainActivity.class);
       startActivity(intent);
       finish();
   }


    public void removeNote(View view){
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        NoteDataSource noteDataSource = new NoteDataSource(sqLiteDatabase);
        noteDataSource.delete(myNote);
        helper.close();
        sqLiteDatabase.close();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
