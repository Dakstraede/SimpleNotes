package com.gp19.esgi.simplenotes.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.gp19.esgi.simplenotes.Note;
import com.gp19.esgi.simplenotes.NoteGroup;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NoteDataSource {

    protected SQLiteDatabase mDatabase;
    public static final DateFormat sdf = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());

    public static final String TABLE_NOTE = "Note";
    public static final String TABLE_GROUP = "Category";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOTE_TITLE = "noteTitle";
    public static final String COLUMN_NOTE_CONTENT = "noteContent";
    public static final String COLUMN_NOTE_CREATION = "creationDate";
    public static final String COLUMN_NOTE_MODIFICATION = "lastModificationDate";
    public static final String COLUMN_NOTE_ARCHIVE = "isArchived";
    public static final String COLUMN_NOTE_IMPORTANCE = "importanceLevel";
    public static final String COLUMN_CATEGORY_NAME = "categoryName";
    public static final String TABLE_LINK = "Linked";
    public static final String COLUMN_LINK_GROUP_ID = "groupId";
    public static final String COLUMN_LINK_NOTE_ID = "noteId";



    public static final String CREATE_NOTE_TABLE = "CREATE TABLE " + TABLE_NOTE +" ( " + COLUMN_ID + "  INTEGER NOT NULL " +
            " PRIMARY KEY AUTOINCREMENT, " +
            " " + COLUMN_NOTE_TITLE + " TEXT " +
            " COLLATE BINARY," +
            " " + COLUMN_NOTE_CONTENT + " TEXT COLLATE BINARY " +
            " NOT NULL, " +
            " " + COLUMN_NOTE_ARCHIVE + " BOOLEAN NOT NULL " +
            " DEFAULT(0) " +
            " COLLATE BINARY, " +
            " " + COLUMN_NOTE_CREATION + " DATETIME NOT NULL " +
            " DEFAULT(datetime('now') ) " +
            " COLLATE BINARY, " +
            " " + COLUMN_NOTE_MODIFICATION + " DATETIME COLLATE BINARY, " +
            " " + COLUMN_NOTE_IMPORTANCE + " INTEGER NOT NULL " +
            " DEFAULT(0) " +
            "COLLATE BINARY);";

    public static final String CREATE_TABLE_CATEGORY = String.format("CREATE TABLE %s ( %s  INTEGER NOT NULL   PRIMARY KEY AUTOINCREMENT,  %s TEXT UNIQUE  COLLATE BINARY);", TABLE_GROUP, COLUMN_ID, COLUMN_CATEGORY_NAME);

    public static final String CREATE_LINK_TABLE = String.format("CREATE TABLE %s ( %s INTEGER NOT NULL   COLLATE BINARY,  %s INTEGER NOT NULL   COLLATE BINARY,  CONSTRAINT PK_GROUP PRIMARY KEY( %s ASC,  %s ASC), FOREIGN KEY(%s) REFERENCES %s(id) ON DELETE CASCADE, FOREIGN KEY(%s) REFERENCES %s(id) ON DELETE CASCADE);",
            TABLE_LINK, COLUMN_LINK_GROUP_ID, COLUMN_LINK_NOTE_ID, COLUMN_LINK_GROUP_ID, COLUMN_LINK_NOTE_ID, COLUMN_LINK_GROUP_ID, TABLE_GROUP, COLUMN_LINK_NOTE_ID, TABLE_NOTE);

    public NoteDataSource(SQLiteDatabase database){
        this.mDatabase = database;
    }

    public boolean insert(Note entity){
        if (entity == null){
            return false;
        }
        long  result = mDatabase.insert(TABLE_NOTE, null, generateContentValuesFromObject(entity));
        if (result != -1){
            entity.setId(result);
        }
        return result != -1;
    }

    public boolean insert(NoteGroup group, Note note){
        if (group == null || note == null){
            return false;
        }
        else {

            long result = mDatabase.insert(TABLE_LINK, null, generateContentValuesFromObject(group, note));
            return result != 1;
        }
    }

    public boolean insert(NoteGroup entity){
        if (entity == null){
            return false;
        }
        long result = mDatabase.insert(TABLE_GROUP, null, generateContentValuesFromObject(entity));
        if (result != -1){
            entity.setId(result);
        }
        return result != -1;
    }

    public boolean delete(Note entity){
        if (entity == null){
            return false;
        }
        int result = mDatabase.delete(TABLE_NOTE, COLUMN_ID + " = " + entity.getId(), null);
        mDatabase.delete(TABLE_LINK, COLUMN_LINK_NOTE_ID + " = " + entity.getId(), null);
        return result != 0;
    }

    public boolean delete(NoteGroup noteGroup, Note note){
        if (noteGroup == null || note == null) return false;
        else {
            int result = mDatabase.delete(TABLE_LINK, COLUMN_LINK_GROUP_ID + " = ? AND " + COLUMN_LINK_NOTE_ID + " = ?", new String[]{String.valueOf(noteGroup.getId()), String.valueOf(note.getId())});
            return result != 0;
        }
    }

    public boolean delete(NoteGroup entity){
        if (entity == null){
            return false;
        }
        int result = mDatabase.delete(TABLE_GROUP, COLUMN_ID + " = " + entity.getId(), null);
        mDatabase.delete(TABLE_LINK, COLUMN_LINK_GROUP_ID + " = " + entity.getId(), null);
        return result != 0;
    }

    public boolean update(Note entity){
        if (entity == null){
            return false;
        }
        int result = mDatabase.update(TABLE_NOTE, generateContentValuesFromObject(entity), COLUMN_ID + " = " + entity.getId(),null);
        return result != 0;
    }

    public boolean update(NoteGroup entity){
        if(entity == null){
            return false;
        }
        int result = mDatabase.update(TABLE_GROUP, generateContentValuesFromObject(entity), COLUMN_ID + " = " + entity.getId(), null);
        return result != 0;
    }

    public List<Long> read(Note note)
    {
        Cursor cursor = mDatabase.query(TABLE_LINK, new String[]{COLUMN_LINK_GROUP_ID}, COLUMN_LINK_NOTE_ID + "=?", new String[]{String.valueOf(note.getId())}, null, null, null);
        List<Long> groupsId = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                groupsId.add(cursor.getLong(cursor.getColumnIndex(COLUMN_LINK_GROUP_ID)));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return groupsId;
    }




    public List<Note> read(){
        Cursor cursor = mDatabase.query(TABLE_NOTE, getAllColumns(), null, null, null, null, null);
        List<Note> notes = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                notes.add(generateObjectFromCursor(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return notes;
    }

    public List<NoteGroup> readGroups(String selction, String[] selectionArgs, String groupBy, String having, String orderBy){
        Cursor cursor = mDatabase.query(TABLE_GROUP, getAllColumnsGroup(), selction, selectionArgs, groupBy, having, orderBy);
        List<NoteGroup> noteGroups = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                noteGroups.add(generateGroupFromCursor(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return noteGroups;
    }



    public List<Note> read(String[] selectionArgs)
    {
        Cursor cursor = mDatabase.rawQuery("SELECT N.id, noteTitle, noteContent, creationDate, lastModificationDate, isArchived, importanceLevel FROM Note AS N " +
        " INNER JOIN Linked AS L ON N.id=L.noteId WHERE L.groupId=?",selectionArgs);
        List<Note> notes = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                notes.add(generateObjectFromCursor(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return notes;
    }

    public List<Note> read(String selction, String[] selectionArgs, String groupBy, String having, String orderBy){

        Cursor cursor = mDatabase.query(TABLE_NOTE, getAllColumns(), selction, selectionArgs, groupBy, having, orderBy);
        List<Note> notes = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                notes.add(generateObjectFromCursor(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return notes;
    }

    public String[] getAllColumns(){
        return new String[] {COLUMN_ID, COLUMN_NOTE_TITLE, COLUMN_NOTE_CONTENT, COLUMN_NOTE_ARCHIVE, COLUMN_NOTE_CREATION, COLUMN_NOTE_MODIFICATION, COLUMN_NOTE_IMPORTANCE};
    }

    public String[] getAllColumnsGroup() {
        return new String[] {COLUMN_ID, COLUMN_CATEGORY_NAME};
    }

    public String[] getAllColumnsLink(){
        return new String[] {COLUMN_LINK_GROUP_ID, COLUMN_LINK_NOTE_ID};
    }

    public NoteGroup generateGroupFromCursor(Cursor cursor){
        if (cursor == null){
            return null;
        }

        return new NoteGroup(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)), cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));
    }

    public Note generateObjectFromCursor(Cursor cursor){
        if (cursor == null){
            return null;
        }
        Note note = new Note();
        note.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
        note.setNoteTitle(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TITLE)));
        note.setNoteContent(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_CONTENT)));
        int tmp_arch = cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_ARCHIVE));
        if (tmp_arch == 0)
            note.setArchived(false);
        else note.setArchived(true);

        java.util.Date dt1 = null;
        try {
            dt1 = sdf.parse(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_CREATION)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        note.setCreationDate(dt1);

        java.util.Date dt2 = null;

        if (cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_MODIFICATION)) != null) {
            try {
                dt2 = sdf.parse(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_MODIFICATION)));
                note.setLastModicationDate(dt2);
            } catch (ParseException e) {
                note.setLastModicationDate(null);
            }
        }



        note.setImportanceLevel(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_IMPORTANCE)));

        return note;
    }


    public ContentValues generateContentValuesFromObject(NoteGroup noteGroup, Note note){
        if (noteGroup == null || note == null){
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_LINK_GROUP_ID, noteGroup.getId());
        values.put(COLUMN_LINK_NOTE_ID, note.getId());
        return values;
    }


    public ContentValues generateContentValuesFromObject(NoteGroup noteGroup){
        if (noteGroup == null){
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, noteGroup.getGroupName());
        return values;
    }

    public ContentValues generateContentValuesFromObject(Note note){
        if (note == null){
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, note.getNoteTitle());
        values.put(COLUMN_NOTE_CONTENT, note.getNoteContent());
        values.put(COLUMN_NOTE_ARCHIVE, note.isArchived());
        values.put(COLUMN_NOTE_CREATION, sdf.format(note.getCreationDate()));
        if (note.getLastModificationDate() != null){
            values.put(COLUMN_NOTE_MODIFICATION, sdf.format(note.getLastModificationDate()));
        }
        values.put(COLUMN_NOTE_CREATION, sdf.format(note.getCreationDate()));
        values.put(COLUMN_NOTE_IMPORTANCE, note.getImportanceLevel());
        return values;
    }
}
