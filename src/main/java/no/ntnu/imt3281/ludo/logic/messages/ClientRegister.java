package no.ntnu.imt3281.ludo.logic.messages;

public class ClientRegister extends Message {
    private String uuid;
    private String username;
    private String password;

    public ClientRegister(String action, String username, String password){
        super(action);
        this.username = username;
        this.password = password;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
