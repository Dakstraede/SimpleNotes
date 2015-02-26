package com.gp19.esgi.simplenotes;

import java.util.ArrayList;

/**
 * Created by Mathieu on 22/02/2015.
 */
public class NoteGroup {

    private long id;
    private String groupName;


    private ArrayList<Note> notes;

    public NoteGroup(String groupName) {
        this.groupName = groupName;
    }

    public NoteGroup(long id, String groupName) {
        this.id = id;
        this.groupName = groupName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes;
    }
}
