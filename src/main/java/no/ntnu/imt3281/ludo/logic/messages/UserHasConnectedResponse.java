package no.ntnu.imt3281.ludo.logic.messages;

public class UserHasConnectedResponse extends Message{
    public String userid;

    public UserHasConnectedResponse(String action){super(action);}

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserid() {
        return userid;
    }
}
