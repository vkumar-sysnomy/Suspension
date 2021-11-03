package com.farthestgate.suspensions.android.ui.gallery;

/**
 * Created by Administrator on 30/06/2015.
 */
public class ListItem {

    private int id;
    private String fileName;
    private String reference;

    public ListItem(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


}
