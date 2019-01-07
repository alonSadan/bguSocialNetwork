package bgu.spl.net.api.bidi;

import java.util.LinkedList;
import java.util.Set;

public class UserListAckMsg extends Message {
    private short numOfusers;
    private String usersList;

    public short getMsgRelatedOpcode() {
        return msgRelatedOpcode;
    }

    protected short msgRelatedOpcode;


    public UserListAckMsg(short msgRelatedOpcode, short numOfUsers, LinkedList<String> users) {
        super((short)10);
        this.numOfusers = numOfUsers;
        this.usersList = getUsersList(users);
        this.msgRelatedOpcode = msgRelatedOpcode;


    }

    private String getUsersList(LinkedList<String> users) {
        String namesList = "";
        for (int i = 0 ; i<users.size(); ++i){
             namesList +=  users.get(i) + "\0";
        }
        return namesList;
    }

    public String getUsersList() {
        return usersList;
    }

    public short getNumOfusers() {
        return numOfusers;
    }
}
