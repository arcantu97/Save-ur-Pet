package arcan.apps.saveurpet.models;

public class ComplaintModel {

    String user;
    String phone1;
    String phone2;
    String address;
    String municity;
    String description;
    String date;
    String id;
    String urlImage;
    Boolean visible;

    public ComplaintModel(String user, String phone1, String phone2, String address, String municity, String description, String date, String id, String urlImage, Boolean visible) {
        this.user = user;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.address = address;
        this.municity = municity;
        this.description = description;
        this.date = date;
        this.id = id;
        this.urlImage = urlImage;
        this.visible = visible;
    }

    public ComplaintModel() {
    }



    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMunicity() { return municity; }

    public void setMunicity(String municity) { this.municity = municity; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getUrlImage() { return urlImage; }

    public void setUrlImage(String urlImage) { this.urlImage = urlImage; }

    public Boolean getVisible() { return visible; }

    public void setVisible(Boolean visible) { this.visible = visible; }
}
