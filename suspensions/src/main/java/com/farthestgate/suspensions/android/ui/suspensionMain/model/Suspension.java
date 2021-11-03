package com.farthestgate.suspensions.android.ui.suspensionMain.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Transient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Suspension implements Parcelable, Comparable<Suspension> {

    public Suspension(){}


    @Expose
    @SerializedName("orderId")
    private String suspensionNumber;
    @Expose
    @SerializedName("streetName")
    private String location;
    @Expose
    @SerializedName("suspensionReason")
    private String reason = "";
    @Expose
    @SerializedName("startDate")
    private String start;
    @Expose
    @SerializedName("endDate")
    private String end;
    @Expose
    @SerializedName("bayNumber")
    private String baysNumber;

    @Expose
    @SerializedName("statusId")
    private String statusId;

    @Expose
    @SerializedName("addInstContractor")
    private String additionalInstructions = "";

    @Transient
    private int photoCount;
    @Transient
    private boolean isPutUp;
    private List<JSONObject> noteList = new ArrayList<>();
    private List<JSONObject> vrmList = new ArrayList();

    public String getSuspensionNumber() {
        return suspensionNumber;
    }

    public void setSuspensionNumber(String suspensionNumber) {
        this.suspensionNumber = suspensionNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getBaysNumber() {
        return baysNumber;
    }

    public void setBaysNumber(String baysNumber) {
        this.baysNumber = baysNumber;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public int getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(int photoCount) {
        this.photoCount = photoCount;
    }

    public boolean isPutUp() {
        return isPutUp;
    }

    public void setPutUp(boolean putUp) {
        isPutUp = putUp;
    }

    public List<JSONObject> getNotes() {
        return noteList;
    }

    public void setNotes(String note) {
        try {
            if(!note.isEmpty())
            this.noteList.add(new JSONObject().put("note", note));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<JSONObject> getVrmList() {
        return vrmList;
    }

    public void setVrmList(List<JSONObject> vrmList) {
        this.vrmList.addAll(vrmList);
    }

    public String getAdditionalInstructions() {
        return additionalInstructions;
    }

    public void setAdditionalInstructions(String additionalInstructions) {
        this.additionalInstructions = additionalInstructions;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.suspensionNumber);
        dest.writeString(this.location);
        dest.writeString(this.reason);
        dest.writeString(this.start);
        dest.writeString(this.end);
        dest.writeString(this.baysNumber);
        dest.writeString(this.statusId);
        dest.writeString(this.additionalInstructions);
        dest.writeInt(this.photoCount);
        dest.writeByte(this.isPutUp ? (byte) 1 : (byte) 0);
        dest.writeList(this.noteList);
        dest.writeList(this.vrmList);
    }

    protected Suspension(Parcel in) {
        this.suspensionNumber = in.readString();
        this.location = in.readString();
        this.reason = in.readString();
        this.start = in.readString();
        this.end = in.readString();
        this.baysNumber = in.readString();
        this.statusId = in.readString();
        this.additionalInstructions = in.readString();
        this.photoCount = in.readInt();
        this.isPutUp = in.readByte() != 0;
        this.noteList = new ArrayList<JSONObject>();
        in.readList(this.noteList, JSONObject.class.getClassLoader());
        this.vrmList = new ArrayList<JSONObject>();
        in.readList(this.vrmList, JSONObject.class.getClassLoader());
    }

    public static final Creator<Suspension> CREATOR = new Creator<Suspension>() {
        @Override
        public Suspension createFromParcel(Parcel source) {
            return new Suspension(source);
        }

        @Override
        public Suspension[] newArray(int size) {
            return new Suspension[size];
        }
    };

    @Override
    public int compareTo(@NonNull Suspension o) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return dateFormat.parse(getEnd()).compareTo(dateFormat.parse(o.getEnd()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
