package bgu.spl.net.api.bidi;

import java.io.Serializable;

public class UserListMsg extends Message {

    public UserListMsg (){
        super((short)7);
    }

}
