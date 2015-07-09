package com.gp19.esgi.simplenotes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Mathieu on 22/02/2015.
 */
public class NoteGroup implements Parcelable{

    private long id;
    private String groupName;


    private ArrayList<Note> notes;

    public NoteGroup(Parcel in){
        this.groupName = in.readString();
        this.id = in.readInt();
    }

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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.groupName);
        dest.writeLong(this.id);
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NoteGroup> CREATOR = new Creator<NoteGroup>(){
        @Override
        public NoteGroup createFromParcel(Parcel source) {
            return new NoteGroup(source);
        }

        @Override
        public NoteGroup[] newArray(int size) {
            return new NoteGroup[0];
        }
    };
}
