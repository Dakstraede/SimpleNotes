package com.gp19.esgi.simplenotes.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Mathieu on 22/02/2015.
 */
public abstract class AbstractDataLoader<E extends List<?>> extends AsyncTaskLoader<E> {

    protected E mlastDataList = null;
    protected abstract E buildList();
//    protected abstract E buildListGroup();

    public AbstractDataLoader(Context context){
        super(context);
    }

    @Override
    public E loadInBackground() {
        return buildList();
    }

    @Override
    public void deliverResult(E dataList) {
        if (isReset()){
            emptyDataList(dataList);
            return;
        }
        E oldDataList = mlastDataList;
        mlastDataList = dataList;
        if (isStarted()){
            super.deliverResult(dataList);
        }
        if (oldDataList != null && oldDataList != dataList && oldDataList.size() > 0){
            emptyDataList(oldDataList);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mlastDataList != null){
            deliverResult(mlastDataList);
        }
        if (takeContentChanged() || mlastDataList == null || mlastDataList.size() == 0){
            forceLoad();
        }


    }


    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(E dataList) {
        if (dataList != null && dataList.size() > 0){
            emptyDataList(dataList);
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        if (mlastDataList != null && mlastDataList.size() > 0){
            emptyDataList(mlastDataList);
        }
        mlastDataList = null;
    }

    protected void emptyDataList(E dataList){
        if (dataList != null && dataList.size() > 0){
            for (int i = 0; i < dataList.size(); i++){
                dataList.remove(i);
            }
        }
    }
}
