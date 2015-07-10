package com.gp19.esgi.simplenotes;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;

public class Note implements Parcelable {
    private String noteTitle;
    private String noteContent;
    private long id;
    private boolean isArchived;
    private Date creationDate;
    private Date lastModicationDate;
    private int importanceLevel;

    public Note(){}

    private Note(Parcel in){
        this.noteTitle = in.readString();
        this.noteContent = in.readString();
        this.id = in.readLong();
        this.isArchived = in.readByte() != 0;
        this.creationDate = new Date(in.readLong());
        if (in.readInt() == 1) {
            this.lastModicationDate = new Date(in.readLong()); // default classloader
        }
        else {
            this.lastModicationDate = null;
        }

        this.importanceLevel = in.readInt();
    }

    /***
     * For a duplicate
     * @param noteTitle
     * @param noteContent
     * @param importanceLevel
     * @param isArchived
     */
    public Note(String noteTitle, String noteContent, int importanceLevel, boolean isArchived){
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.importanceLevel = importanceLevel;
        this.isArchived = isArchived;
        this.creationDate = new Date(System.currentTimeMillis());
        this.lastModicationDate = null;
    }

    public Note(String noteTitle, String noteContent, int importanceLevel)
    { // For a new one
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.creationDate = new Date(System.currentTimeMillis());
        this.importanceLevel = importanceLevel;
        this.lastModicationDate = null;
        this.isArchived = false;
    }

    public Note(String noteTitle, String noteContent, int id, boolean isArchived, Date creationDate, Date lastModicationDate, int importanceLevel)
    { //For an existing one taken from DB
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.id = id;
        this.isArchived = isArchived;
        this.creationDate = creationDate;
        if (lastModicationDate != null){
            this.lastModicationDate = lastModicationDate;
        }
        else this.lastModicationDate = null;
        this.importanceLevel = importanceLevel;

    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public long getId() {
        return id;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getLastModificationDate() {
        return lastModicationDate;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setArchived(boolean isArchived) {
        this.isArchived = isArchived;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setLastModicationDate(Date lastModicationDate) {
        this.lastModicationDate = lastModicationDate;
    }

    public void setLastModicationDate() {
        this.lastModicationDate = new Date(System.currentTimeMillis());
    }

    public int getImportanceLevel() {
        return importanceLevel;
    }

    public void setImportanceLevel(int importanceLevel) {
        this.importanceLevel = importanceLevel;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.noteTitle);
        dest.writeString(this.noteContent);
        dest.writeLong(this.id);
        dest.writeByte((byte) (this.isArchived ? 1 : 0));
        dest.writeLong(this.creationDate.getTime());
        if (this.lastModicationDate != null) {
            dest.writeInt(1);
            dest.writeLong(this.lastModicationDate.getTime());
        } else {
            dest.writeInt(0);
        }
        dest.writeInt(this.importanceLevel);
    }

    public static final Creator<Note> CREATOR = new Creator<Note>(){
        @Override
        public Note createFromParcel(Parcel source) {
            return new Note(source);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[0];
        }
    };
}
