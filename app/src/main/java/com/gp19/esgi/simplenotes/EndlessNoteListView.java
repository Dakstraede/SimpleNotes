package com.gp19.esgi.simplenotes;

import android.content.Context;
import android.util.AttributeSet;

import android.widget.ListView;


public class EndlessNoteListView extends ListView{
    public EndlessNoteListView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }
    public EndlessNoteListView(Context context, AttributeSet attrs){
        super(context, attrs);
    }
}