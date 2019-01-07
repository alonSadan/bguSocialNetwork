package bgu.spl.net.api.bidi;

import java.io.Serializable;

public class RegisterMsg extends Message{

    private String userName;
    private String password;

    public RegisterMsg(String userName, String password){
        super((short) 1);
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
