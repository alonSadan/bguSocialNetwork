package bgu.spl.net.api.bidi;

import java.io.Serializable;

public class AckMsg extends Message {


    protected short msgRelatedOpcode;
    // add more things for each kind of message, the "optional" field//

    public AckMsg(short msgRelatedOpcode ){
        super((short) 10);
        this.msgRelatedOpcode = msgRelatedOpcode;
    }
    public short getMsgRelatedOpcode() {
        return msgRelatedOpcode;
    }
}
