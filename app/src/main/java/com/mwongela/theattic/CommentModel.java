package com.mwongela.theattic;

public class CommentModel {
    private String displayName, profilePhoto, comment, time, date;
    //create a constructor

    public CommentModel(String displayName, String profilePhoto, String comment, String time, String date) {

        this.comment=comment;
        this.displayName = displayName;
        this.profilePhoto=profilePhoto;
        this.time=time;
        this.date=date;
    }
    //requires an empty constructor
    public CommentModel() {
    }
    // setters
        public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

      public void setTime(String time){
        this.time=time;
    }
    public void setDate(String date){
        this.date=date;
    }
    //getters
    public String getDisplayName() {
        return displayName;
    }

    public String getComment() {
        return comment;
    }


    public String getProfilePhoto()
    {
        return profilePhoto;
    }
    public String getTime(){
        return time;
    }
    public String getDate(){
        return date;
    }

}
