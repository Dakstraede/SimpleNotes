package com.gp19.esgi.simplenotes.loader;

import android.content.Loader;
import android.os.AsyncTask;

/**
 * Created by Mathieu on 22/02/2015.
 */
public abstract class ContentChangingTask<T1, T2, T3> extends AsyncTask<T1, T2, T3> {
    private Loader<?> loader = null;

    ContentChangingTask(Loader<?> loader){
        this.loader = loader;
    }

    @Override
    protected void onPostExecute(T3 t3) {
        loader.onContentChanged();
    }
}
