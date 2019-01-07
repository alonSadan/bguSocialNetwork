package bgu.spl.net.api.bidi;


import java.io.Serializable;

public class  Message implements Serializable { 
    private short opCode;
    public Message (short opCode){
        this.opCode = opCode;
    }

    public short getOpCode(){
        return this.opCode;
    }
}
