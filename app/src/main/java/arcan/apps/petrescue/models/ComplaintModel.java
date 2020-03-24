package arcan.apps.petrescue.models;

public class ComplaintModel {

    String comment;
    String date;
    String user;

    public ComplaintModel() {
    }

    public ComplaintModel(String comment, String date, String user) {
        this.comment = comment;
        this.date = date;
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
