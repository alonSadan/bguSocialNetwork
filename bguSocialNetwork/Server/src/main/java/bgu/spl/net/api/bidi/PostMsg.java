package bgu.spl.net.api.bidi;

import java.util.LinkedList;

public class PostMsg extends Message {
    private String content;
    private LinkedList<String> taggedUsers;

   public PostMsg(String content){
        super((short)5);
        this.content = content;
        this.taggedUsers = findTaggedUsers(content);

   }

    private LinkedList<String> findTaggedUsers(String content) {
       LinkedList<String> taggedUsers = new LinkedList<>();
        String delimiter = "[ ]+";
        String [] words = content.split(delimiter);
        for (String word: words){
            if(word.charAt(0) == '@'){
                taggedUsers.add(word.substring(1));
            }
        }
       return taggedUsers;
    }

    public LinkedList<String> getTaggedUsers() {
        return taggedUsers;
    }

    public String getContent() {
        return content;
    }
}
