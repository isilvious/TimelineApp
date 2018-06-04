package com.example.isaac.timeline;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Isaac on 3/20/2017.
 */

public class Post implements Parcelable {

    private String dateTime;
    private String content;
    private int _id;
    private String uid; //Uid defind by users email, not their username!
    private double lat; //Latitutde
    private double lon; //Logitude

    public Post(String dateTime, String content, String uid, int _id, double lat, double lon){
        this.dateTime = dateTime;
        this.content = content;
        this.uid = uid;
        this._id = _id;
        this.lat = lat;
        this.lon = lon;
    }

    public Post(String dateTime, String content, String uid, double lat, double lon){
        this.dateTime = dateTime;
        this.content = content;
        this.uid = uid;
        _id = -1; //used so that LocalDB creates the _id instead of some other class
        this.lat = lat;
        this.lon = lon;
    }

    public int describeContents(){
        return 0;
    }

    //Add the data in class fields to the Parcel object dest
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(dateTime);
        dest.writeString(content);
        dest.writeString(uid);
        dest.writeInt(_id);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
    }

    //CREATOR creates an instance of Post from a Parcel
    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>(){
        public Post createFromParcel(Parcel in){
            return new Post(in);
        }

        public Post[] newArray(int size){
            return  new Post[size];
        }
    };

    //A private sonstuctor that creates an instance of Post from a Parcel
    private Post(Parcel in){
        //MUST BE LISTED IN SAME ORDER AS IN writeToParcel()!
        dateTime = in.readString();
        content = in.readString();
        uid = in.readString();
        _id = in.readInt();
        lat = in.readDouble();
        lon = in.readDouble();
    }

    public String getDate() {
        return dateTime;
    }

    public void setDate(String date) {
        this.dateTime = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUid(){
        return uid;
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    public int get_id(){
        return _id;
    }

    public void set_id(int _id){
        this._id = _id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
