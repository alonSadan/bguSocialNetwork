package bgu.spl.net.api.bidi;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class User {

    private String name;
    private String password;
    private ConcurrentHashMap<String, User> followingList;  //followed by this user//
    private ConcurrentHashMap<String, User> followersList; // users who follow current user//
    private boolean logged = false;
    private LinkedList<Message> sentMessages;
    private ConcurrentLinkedDeque <Message> notReadMessageQueue;   //not sure we need concurrent//


    public User (String name, String password ){
        this.name = name;
        this.password = password;
        this.followingList = new ConcurrentHashMap<>();    //not sure we need concurrent//
        this.followersList = new ConcurrentHashMap<>();   //not sure we need concurrent//
        this.sentMessages = new LinkedList<>();
        this.notReadMessageQueue = new ConcurrentLinkedDeque<>();
    }

    public String getName() {
        return name;
    }


    public String getPassword() {
        return password;
    }

    public ConcurrentHashMap<String, User> getFollowingList() {
        return followingList;
    }

    public ConcurrentHashMap<String, User> getFollowersList() {
        return followersList;
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }
    public boolean isFollowing (String userName){
        return this.getFollowingList().containsKey(userName);
    }
    public boolean isFollowedBy (String userName){
        return this.followersList.containsKey(userName);
    }

    public LinkedList<Message> getSentMessages() {
        return sentMessages;
    }

    public ConcurrentLinkedDeque <Message> getNotReadNotificationQueue() {
        return notReadMessageQueue;
    }

    public int getNumOfPosts() {
        int numOfPosts = 0;
        for (Message msg: sentMessages){
            if (msg instanceof PostMsg){
                ++numOfPosts;
            }
        }
        return numOfPosts;
    }
}
