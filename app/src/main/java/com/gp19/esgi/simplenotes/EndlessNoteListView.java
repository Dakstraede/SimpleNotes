package com.gp19.esgi.simplenotes;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.List;


public class EndlessNoteListView extends ListView implements AbsListView.OnScrollListener{

    private View footer;
    private boolean isLoading;
    private EndlessAdapter adapter;
    private EndlessListener listener;

    public EndlessNoteListView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        this.setOnScrollListener(this);
    }

    public EndlessNoteListView(Context context, AttributeSet attrs){
        super(context, attrs);
        this.setOnScrollListener(this);
    }

    public EndlessNoteListView(Context context){
        super(context);
        this.setOnScrollListener(this);
    }

    public void setListener(EndlessListener listener){
        this.listener = listener;
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (listener != null)
        {
            if (getAdapter() == null)
            {
                return;
            }
            int l = visibleItemCount + firstVisibleItem;
            if (l >= totalItemCount && !isLoading){
                this.addFooterView(footer);
                isLoading = true;
                listener.loadData();
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    public void setLoadingView(int resId){
        LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footer = inflater.inflate(resId, null);
        this.addFooterView(footer);
    }

    public void setAdapter(EndlessAdapter adapter){
        super.setAdapter(adapter);
        this.adapter = adapter;
        this.removeFooterView(footer);
    }

    public void removeFooter(){
        this.removeFooterView(footer);
    }

    public void addNewData(List<?> data){
        this.removeFooterView(footer);
        adapter.addAll(data);
        adapter.sort();
        adapter.notifyDataSetChanged();
        isLoading = false;
    }

    public EndlessListener setListener(){
        return listener;
    }

    public static interface EndlessListener{
        public void loadData();
    }
}
