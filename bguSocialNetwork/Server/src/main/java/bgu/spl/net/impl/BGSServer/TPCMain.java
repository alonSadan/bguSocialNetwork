package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.*;
import bgu.spl.net.srv.BaseServer;
import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.Reactor;
import bgu.spl.net.srv.Server;
import bgu.spl.net.api.*;


public class TPCMain {

    public static void main(String args[]){
        DataBase dataBase =  new DataBase();
        Server.threadPerClient(Integer.parseInt(args[0]), ()->new BidiMessagingProtocolImpl(dataBase),()-> new MessageEncoderDecoderImpl(){
        }).serve();
    }
}
