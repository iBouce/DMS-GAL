package model;

public class messageModel {

    private String image,username,date,message;

    public messageModel(String image, String name,String date, String message){
        this.image = image;
        this.username = name;
        this.date = date;
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public String getUserName() {
        return this.username;
    }

    public String getDate() {
        return this.date;
    }

    public String getMessage() {
        return this.message;
    }

}
