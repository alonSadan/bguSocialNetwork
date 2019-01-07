package bgu.spl.net.api.bidi;

import java.io.Serializable;

public class NotificationMsg extends Message {
    private boolean privateMessage;
    private String postingUser;
    private String content;
    private byte type;

    public NotificationMsg( String postingUser, String content , byte type){
        super((short) 9);
        this.postingUser = postingUser;
        this.content = content;
        this.type = type;
        privateMessage = (type == (byte) 0);
    }
    public boolean isPrivateMessage() {
        return privateMessage;
    }

    public String getPostingUser() {
        return postingUser;
    }

    public String getContent() {
        return content;
    }

    public byte getType() {
        return type;
    }
}
