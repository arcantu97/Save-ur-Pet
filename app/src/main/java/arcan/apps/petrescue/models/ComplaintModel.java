package arcan.apps.petrescue.models;

public class ComplaintModel {

    String user;
    String phone1;
    String phone2;
    String address;
    String description;
    String date;
    String id;


    public ComplaintModel(String user, String phone1, String phone2, String address, String description, String date, String id) {
        this.user = user;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.address = address;
        this.description = description;
        this.date = date;
        this.id = id;
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
}
