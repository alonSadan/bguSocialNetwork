package bgu.spl.net.api.bidi;

import java.io.Serializable;

public class StatMsg extends Message{
    private String userName;

    public StatMsg(String userName){
       super((short)8);
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
