package bgu.spl.net.api.bidi;

import java.io.Serializable;
import java.util.LinkedList;

public class FollowMsg extends Message {

    private boolean follow;
    private int numOfUsers;
    private LinkedList<String> userNameList;   //maybe concurrent//


    public FollowMsg(byte follow, int numOfUsers, String userNames) {
        super((short)4);
        this.follow = (follow == (byte)0);   //maybe use '\0'
        this.numOfUsers = numOfUsers;
        this.userNameList = makeStringList(userNames);

    }

    private LinkedList<String> makeStringList(String userNames) {
        LinkedList<String> userNameLIst = new LinkedList<>();
        String delimiter = "[\0]";
String [] names = userNames.split(delimiter);
       for (String name: names){
           userNameLIst.add(name);
       }
        return userNameLIst;
    }


    public LinkedList<String> getUserNameList() {
        return userNameList;
    }

    public boolean isFollow() {
        return follow;
    }
}
