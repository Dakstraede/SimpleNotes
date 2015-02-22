package com.gp19.esgi.simplenotes.loader;

import android.content.Context;

import com.gp19.esgi.simplenotes.Note;
import com.gp19.esgi.simplenotes.database.NoteDataSource;

import java.util.List;

/**
 * Created by Mathieu on 22/02/2015.
 */
public class SQLiteNoteDataLoader extends AbstractDataLoader<List<?>> {

    private NoteDataSource mDataSource;
    private String mSelection;
    private String[] mSelectionArgs;
    private String mGroupBy;
    private String mHaving;
    private String mOrderBy;

    public SQLiteNoteDataLoader(Context context, NoteDataSource dataSource, String selection, String[] selectionArgs, String groupBy, String having, String orderBy ){
        super(context);
        mDataSource = dataSource;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mGroupBy = groupBy;
        mHaving = having;
        mOrderBy = orderBy;

    }


    public List buildList(){
        List list = mDataSource.read(mSelection, mSelectionArgs, mGroupBy, mHaving, mOrderBy);
        return list;
    }

    public void insert(Note entity){
        new InsertTask(this).execute(entity);
    }

    public void update(Note entity){
        new UpdateTask(this).execute(entity);
    }

    public void delete(Note entity){
        new DeleteTask(this).execute(entity);
    }

    private class  UpdateTask extends ContentChangingTask<Note, Void, Void>{
        UpdateTask(SQLiteNoteDataLoader loader){
            super(loader);
        }

        @Override
        protected Void doInBackground(Note... params) {
//            mDataSource.update(params[0]);
            return (null);
        }
    }

    private class InsertTask extends ContentChangingTask<Note, Void, Void>{
        InsertTask(SQLiteNoteDataLoader loader){
            super(loader);
        }

        @Override
        protected Void doInBackground(Note... params) {
            mDataSource.insert(params[0]);
            return (null);
        }


    }

    private class DeleteTask extends ContentChangingTask<Note, Void, Void>{
        DeleteTask(SQLiteNoteDataLoader loader){
            super(loader);
        }

        @Override
        protected Void doInBackground(Note... params) {
            return (null);
        }
    }




}
