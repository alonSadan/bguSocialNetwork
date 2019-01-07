package bgu.spl.net.api.bidi;

import java.io.Serializable;

public class PmMsg extends Message {
    private String userName;
    private String content;

    public PmMsg(String userName, String content){
        super((short)6);
        this.content = content;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getContent() {
        return content;
    }
}
