package arcan.apps.petrescue.models;

public class Pet {

    private String petName;
    private String petImageURL;
    private String adoptBy;
    private String adoptDate;
    private String visitDate;
    private Long entryDate;
    private Long deathDate;
    private Boolean adopted;
    private Boolean rescued;
    private Boolean requestAdoption;
    private Boolean requestRescue;
    private Boolean NonRequested;


    public Pet() {
    }

    public Pet(String petName, String petImageURL, String adoptBy, String adoptDate, String visitDate, Long entryDate, Long deathDate, Boolean adopted, Boolean rescued, Boolean requestAdoption, Boolean requestRescue, Boolean NonRequested) {
        this.petName = petName;
        this.petImageURL = petImageURL;
        this.adoptBy = adoptBy;
        this.adoptDate = adoptDate;
        this.visitDate = visitDate;
        this.entryDate = entryDate;
        this.deathDate = deathDate;
        this.adopted = adopted;
        this.rescued = rescued;
        this.requestAdoption = requestAdoption;
        this.requestRescue = requestRescue;
        this.NonRequested = NonRequested;
    }

    public String getPetName() { return petName; }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetImageURL() {
        return petImageURL;
    }

    public void setPetImageURL(String petImageURL) {
        this.petImageURL = petImageURL;
    }

    public String getAdoptBy() { return adoptBy; }

    public void setAdoptBy(String adoptBy) { this.adoptBy = adoptBy; }

    public String getAdoptDate() { return adoptDate; }

    public void setAdoptDate(String adoptDate) { this.adoptDate = adoptDate; }

    public String getVisitDate() { return visitDate; }

    public void setVisitDate(String visitDate) { this.visitDate = visitDate; }

    public Long getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Long entryDate) {
        this.entryDate = entryDate;
    }

    public Long getDeathDate() { return deathDate; }

    public void setDeathDate(Long deathDate) { this.deathDate = deathDate; }

    public Boolean getAdopted() {
        return adopted;
    }

    public void setAdopted(Boolean adopted) {
        this.adopted = adopted;
    }

    public Boolean getRescued() {
        return rescued;
    }

    public void setRescued(Boolean rescued) {
        this.rescued = rescued;
    }

    public Boolean getRequestAdoption() {
        return requestAdoption;
    }

    public void setRequestAdoption(Boolean requestAdoption) { this.requestAdoption = requestAdoption; }

    public Boolean getRequestRescue() {
        return requestRescue;
    }

    public void setRequestRescue(Boolean requestRescue) {
        this.requestRescue = requestRescue;
    }

    public Boolean getNonRequested() { return NonRequested; }

    public void setNonRequested(Boolean nonRequested) { NonRequested = nonRequested; }


}
