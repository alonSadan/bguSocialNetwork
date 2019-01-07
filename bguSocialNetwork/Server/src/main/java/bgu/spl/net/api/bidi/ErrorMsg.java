package bgu.spl.net.api.bidi;

import java.io.Serializable;

public class ErrorMsg extends Message  {
    private short msgRelatedOpcode;

    public ErrorMsg(short msgRelatedOpcode){

        super((short) 11);

        this.msgRelatedOpcode = msgRelatedOpcode;
    }

    public short getMsgRelatedOpcode() {
        return msgRelatedOpcode;
    }
}
