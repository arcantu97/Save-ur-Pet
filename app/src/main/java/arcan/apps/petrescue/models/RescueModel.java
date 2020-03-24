package arcan.apps.petrescue.models;

public class RescueModel {

    String personName;
    String personAddress;
    String uidRequest;
    String phoneContact1;
    String phoneContact2;
    String visitDate;
    String requestDate;
    String petName;
    String petImageURL;
    Boolean requestRescue;
    Boolean Rescued;

    public RescueModel() {
    }

    public RescueModel(String personName, String personAddress, String uidRequest, String phoneContact1, String phoneContact2, String visitDate, String requestDate, String petName, String petImageURL, Boolean requestRescue, Boolean rescued) {
        this.personName = personName;
        this.personAddress = personAddress;
        this.uidRequest = uidRequest;
        this.phoneContact1 = phoneContact1;
        this.phoneContact2 = phoneContact2;
        this.visitDate = visitDate;
        this.requestDate = requestDate;
        this.petName = petName;
        this.petImageURL = petImageURL;
        this.requestRescue = requestRescue;
        Rescued = rescued;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonAddress() {
        return personAddress;
    }

    public void setPersonAddress(String personAddress) {
        this.personAddress = personAddress;
    }

    public String getUidRequest() {
        return uidRequest;
    }

    public void setUidRequest(String uidRequest) {
        this.uidRequest = uidRequest;
    }

    public String getPhoneContact1() {
        return phoneContact1;
    }

    public void setPhoneContact1(String phoneContact1) {
        this.phoneContact1 = phoneContact1;
    }

    public String getPhoneContact2() {
        return phoneContact2;
    }

    public void setPhoneContact2(String phoneContact2) {
        this.phoneContact2 = phoneContact2;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetImageURL() {
        return petImageURL;
    }

    public void setPetImageURL(String petImageURL) {
        this.petImageURL = petImageURL;
    }

    public Boolean getRequestRescue() {
        return requestRescue;
    }

    public void setRequestRescue(Boolean requestRescue) {
        this.requestRescue = requestRescue;
    }

    public Boolean getRescued() {
        return Rescued;
    }

    public void setRescued(Boolean rescued) {
        Rescued = rescued;
    }

}
