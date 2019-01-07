package bgu.spl.net.srv;

import bgu.spl.net.api.bidi.User;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class DataBase {

    private ConcurrentHashMap<String,User> usersByName;
    private ConcurrentHashMap<Integer,User> usersByConnectionId;
    private LinkedList <String> registrationQueue;
   // private final Object queueLock;

    public DataBase (){
        this.usersByName = new ConcurrentHashMap<>();
        this.usersByConnectionId = new ConcurrentHashMap<>();
        this.registrationQueue = new LinkedList<>();
    }


    public ConcurrentHashMap<String,User> getUsersByName() {
        return usersByName;
    }

    public void registerUser (User user){
       synchronized (registrationQueue) {
           this.registrationQueue.add(user.getName());
           this.usersByName.put(user.getName(), user);
       }
    }

    public void logInUser (User user, int connectionId){
        this.usersByConnectionId.put(connectionId, user);
        user.setLogged(true);
    }
    public void logoutUser(int connectionId){
        this.usersByConnectionId.get(connectionId).setLogged(false);
        this.usersByConnectionId.remove(connectionId,this.usersByConnectionId.get(connectionId) );
    }
    public ConcurrentHashMap<Integer, User> getUsersByConnectionId() {
        return usersByConnectionId;
    }

    public LinkedList<String> getRegistrationQueue() {
        return registrationQueue;
    }

}
