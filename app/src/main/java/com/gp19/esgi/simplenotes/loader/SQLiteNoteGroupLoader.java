package com.gp19.esgi.simplenotes.loader;

import android.content.Context;

import com.gp19.esgi.simplenotes.Note;
import com.gp19.esgi.simplenotes.NoteGroup;
import com.gp19.esgi.simplenotes.database.NoteDataSource;

import java.util.List;

/**
 * Created by Mathieu on 22/02/2015.
 */
public class SQLiteNoteGroupLoader extends AbstractDataLoader<List<NoteGroup>> {

    private NoteDataSource mDataSource;
    private String mSelection;
    private String[] mSelectionArgs;
    private String mGroupBy;
    private String mHaving;
    private String mOrderBy;

    public SQLiteNoteGroupLoader(Context context, NoteDataSource dataSource, String selection, String[] selectionArgs, String groupBy, String having, String orderBy ){
        super(context);
        mDataSource = dataSource;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mGroupBy = groupBy;
        mHaving = having;
        mOrderBy = orderBy;

    }

//    @Override
//    protected List<?> buildListGroup() {
//        List list = mDataSource.read()
//    }

    public List<NoteGroup> buildList(){
        return mDataSource.readGroups(mSelection, mSelectionArgs, mGroupBy, mHaving, mOrderBy);
    }

    public void insert(NoteGroup entity){
        new InsertTask(this).execute(entity);
    }

    public void update(NoteGroup entity){
        new UpdateTask(this).execute(entity);
    }

    public void delete(NoteGroup entity){
        new DeleteTask(this).execute(entity);
    }

    private class  UpdateTask extends ContentChangingTask<NoteGroup, Void, Void>{
        UpdateTask(SQLiteNoteGroupLoader loader){
            super(loader);
        }

        @Override
        protected Void doInBackground(NoteGroup... params) {
//            mDataSource.update(params[0]);
            return (null);
        }
    }

    private class InsertTask extends ContentChangingTask<NoteGroup, Void, Void>{
        InsertTask(SQLiteNoteGroupLoader loader){
            super(loader);
        }

        @Override
        protected Void doInBackground(NoteGroup... params) {
            mDataSource.insert(params[0]);
            return (null);
        }


    }

    private class DeleteTask extends ContentChangingTask<NoteGroup, Void, Void>{
        DeleteTask(SQLiteNoteGroupLoader loader){
            super(loader);
        }

        @Override
        protected Void doInBackground(NoteGroup... params) {
            return (null);
        }
    }




}
