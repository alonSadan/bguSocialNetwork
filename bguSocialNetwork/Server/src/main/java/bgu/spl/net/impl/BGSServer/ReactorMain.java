package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.*;
import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.Server;

public class ReactorMain {

    public static void main(String args[]){
        DataBase dataBase =  new DataBase();
        Server.reactor(Integer.parseInt(args[0]),Integer.parseInt(args[1]), ()->new BidiMessagingProtocolImpl(dataBase),()-> new MessageEncoderDecoderImpl(){
        }).serve();

    }
}
