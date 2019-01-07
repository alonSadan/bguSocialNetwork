package bgu.spl.net.api.bidi;

public class StatAckMsg extends Message {
    private short numOfPosts;
    private short numOfFollowers;
    private short msgRelatedOpcode;



    private short numOfFollowing;

    public StatAckMsg(short msgRelatedOpcode , short numOfPosts, short numOfFollowers, short numOfFollowing) {
        super((short)10);
        this.numOfFollowers= numOfFollowers;
        this.numOfFollowing = numOfFollowing;
        this.numOfPosts = numOfPosts;
        this.msgRelatedOpcode = msgRelatedOpcode;

    }
    public short getNumOfPosts() {
        return numOfPosts;
    }

    public short getNumOfFollowers() {
        return numOfFollowers;
    }

    public short getNumOfFollowing() {
        return numOfFollowing;
    }

    public short getMsgRelatedOpcode() {
        return msgRelatedOpcode;
    }
}
