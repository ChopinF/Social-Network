package proiectmap.socialmap.domain;

public enum Status {
    ACTIVE("Active"),
    PENDING("Pending");

    private final String value;

    Status(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }

    public static Status fromString(String status){
        for(Status s : Status.values()){
            if(s.getValue().equalsIgnoreCase(status)){
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + status);
    }
}
