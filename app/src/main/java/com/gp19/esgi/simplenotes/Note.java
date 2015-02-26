package com.gp19.esgi.simplenotes;


import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class Note {

    public static final DateFormat sdf = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());


    private String noteTitle;
    private String noteContent;
    private long id;
    private boolean isArchived;
    private Date creationDate;
    private Date lastModicationDate;
    private int importanceLevel;

    public Note(){}

    public Note(String noteTitle, String noteContent)
    { // For a new one
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.creationDate = new Date(System.currentTimeMillis());
        this.importanceLevel = 0;
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

    public Date getLastModicationDate() {
        return lastModicationDate;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
        this.setLastModicationDate();
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
        this.setLastModicationDate();
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setArchived(boolean isArchived) {
        this.isArchived = isArchived;
        this.setLastModicationDate();
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
        this.setLastModicationDate();
    }
}
