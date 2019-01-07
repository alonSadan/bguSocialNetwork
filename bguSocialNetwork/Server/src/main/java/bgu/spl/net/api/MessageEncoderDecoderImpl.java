package bgu.spl.net.api;

import bgu.spl.net.api.bidi.*;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {


    private final ByteBuffer opCodeBuffer = ByteBuffer.allocate(2);
    private final ByteBuffer messageBuffer = ByteBuffer.allocate(1 << 10);
    private final ByteBuffer followBuffer = ByteBuffer.allocate(3);
    private int zeroByteCounter = 0;

    @Override
    public Message decodeNextByte(byte nextByte) {

        if (!opCodeBuffer.hasRemaining()) {
            opCodeBuffer.flip();   // make sure we are clearing the messageBuffer before start working with it//
            return popMessage(opCodeBuffer.getShort(), nextByte);
        }

        opCodeBuffer.put(nextByte);
        if(!opCodeBuffer.hasRemaining()){
            opCodeBuffer.flip();
            short s = opCodeBuffer.getShort();
            if(s == (short) 3){
                opCodeBuffer.clear();
                return new LogoutMsg();
            }
            if(s == (short) 7){
                opCodeBuffer.clear();
                return new UserListMsg();
            }

        }
        return null; //not a opCode yet
    }

    @Override
    public byte[] encode(Message message) {
        messageBuffer.clear();
        switch (message.getOpCode()) {
            case 9:
                byte zeroByte = (byte) 0;   //TODO this is how you create zeroByte?

                messageBuffer.put(shortToBytes(message.getOpCode()));
                messageBuffer.put(((NotificationMsg) message).getType());
                messageBuffer.put(((NotificationMsg) message).getPostingUser().getBytes());
                messageBuffer.put(zeroByte);
                messageBuffer.put(((NotificationMsg) message).getContent().getBytes());
                messageBuffer.put(zeroByte);
                break;
            case 11:
                messageBuffer.put(shortToBytes(message.getOpCode()));
                messageBuffer.put(shortToBytes(((ErrorMsg) message).getMsgRelatedOpcode()));
                break;
            case 10:
                if (message instanceof AckMsg) {
                    messageBuffer.put(shortToBytes(message.getOpCode()));
                    messageBuffer.put(shortToBytes(((AckMsg) message).getMsgRelatedOpcode()));
                    break;
                }
                if (message instanceof UserListAckMsg) {
                    messageBuffer.put(shortToBytes(message.getOpCode()));
                    messageBuffer.put(shortToBytes(((UserListAckMsg) message).getMsgRelatedOpcode()));
                    messageBuffer.put(shortToBytes(((UserListAckMsg) message).getNumOfusers()));
                    messageBuffer.put(((UserListAckMsg) message).getUsersList().getBytes());
                    break;
                } else {
                    if (message instanceof StatAckMsg) {
                        messageBuffer.put(shortToBytes(message.getOpCode()));
                        messageBuffer.put(shortToBytes(((StatAckMsg) message).getMsgRelatedOpcode()));
                        messageBuffer.put(shortToBytes(((StatAckMsg) message).getNumOfPosts()));
                        messageBuffer.put(shortToBytes(((StatAckMsg) message).getNumOfFollowers()));
                        messageBuffer.put(shortToBytes(((StatAckMsg) message).getNumOfFollowing()));
                    } else {
                        if (message instanceof FollowAckMsg) {
                            messageBuffer.put(shortToBytes(message.getOpCode()));
                            messageBuffer.put(shortToBytes(((FollowAckMsg) message).getMsgRelatedOpcode()));
                            messageBuffer.put(shortToBytes(((FollowAckMsg) message).getNumOfusers()));
                            messageBuffer.put(((FollowAckMsg) message).getUserNameList().getBytes());
                        }
                    }
                }

        }
        messageBuffer.flip();
        byte [] toReturn= new byte[messageBuffer.limit()];
        messageBuffer.get(toReturn);
        messageBuffer.clear();
        return toReturn;
    }

    public byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        return bytesArr;
    }

    private Message popMessage(short opCode, byte nextByte) {

        switch (opCode) {
            case 1:
                return popRegisterLoginMsg(opCode, nextByte);
            case 2:
                return popRegisterLoginMsg(opCode, nextByte);
            case 4:
                    if(followBuffer.hasRemaining()){
                        followBuffer.put(nextByte);
                        return null;
                    }
                return popFollowMsg(nextByte);
            case 5:
                return popPostMsg(nextByte);
            case 6:
                return popPmMsg(nextByte);

            case 8:
                return popStatMsg(nextByte);
        }
        return null;
    }

    private Message popStatMsg(byte nextByte) {
        if (nextByte != '\0') {
            messageBuffer.put(nextByte);
            return null;
        }
        messageBuffer.flip();
        String userName = "";
        while (messageBuffer.hasRemaining()) {
            userName += (char)messageBuffer.get();
        }
        messageBuffer.clear();
        opCodeBuffer.clear();
        return new StatMsg(userName);
    }

    private Message popPmMsg(byte nextByte) {
        if (nextByte == '\0') {
            ++zeroByteCounter;
            if (zeroByteCounter == 2) {   //we are counting the zero bytes to determine what we are reading
                String userName = "";
                String content = "";
                zeroByteCounter = 0;
                messageBuffer.flip();
                while (messageBuffer.hasRemaining()) {
                    char c = (char)messageBuffer.get();
                    if (c != '\0') {
                        switch (zeroByteCounter) {
                            case 0:
                                userName += c;
                                break;
                            case 1:
                                content += c;
                                break;
                        }
                    } else { // we are reading zero//
                        ++zeroByteCounter;
                    }
                }
                zeroByteCounter = 0;
                messageBuffer.clear();
                opCodeBuffer.clear();
                return new PmMsg(userName, content);
            }
        }
        messageBuffer.put(nextByte); // next byte is not zero//
        return null;
    }

    private Message popPostMsg(byte nextByte) {
        if (nextByte != '\0') {
            messageBuffer.put(nextByte);
            return null;
        }
        messageBuffer.flip();
        String content = "";
        while (messageBuffer.hasRemaining()) {
            content += (char)messageBuffer.get();
        }
        messageBuffer.clear();
        opCodeBuffer.clear();
        return new PostMsg(content);

    }

    private Message popFollowMsg(byte nextByte) {

        if (nextByte == '\0') {
            ++zeroByteCounter;
            if (zeroByteCounter == followBuffer.getShort(1)) {  //not suppose to increment position
                String userNames = "";
                messageBuffer.flip();  //limit = position, position = 0//
                followBuffer.flip();
                byte followUnfollow = followBuffer.get(); // move position = 1//
                short numOfUsers = followBuffer.getShort(); // move position = 3
                while (messageBuffer.hasRemaining()) {   //position != limit
                    userNames += (char)messageBuffer.get(); //and do position ++//
                }
                followBuffer.clear();
                messageBuffer.clear();
                opCodeBuffer.clear();
                zeroByteCounter = 0;
                return new FollowMsg(followUnfollow, numOfUsers, userNames);
            }
        }
        messageBuffer.put(nextByte); //message is not complete
        return null;
    }

    private Message popRegisterLoginMsg(short opCode, byte nextByte) {

        if (nextByte == '\0') {
            ++zeroByteCounter;
            if (zeroByteCounter == 2) {
                String userName = "";
                String password = "";
                zeroByteCounter = 0;
                messageBuffer.flip();
                while (messageBuffer.hasRemaining()) {
                    if (messageBuffer.get() != '\0') {
                        messageBuffer.position(messageBuffer.position() - 1); // we didnt read \0 so we need to return the position back/
                        char c = (char) messageBuffer.get();
                        switch (zeroByteCounter) {
                            case 0:
                                userName += c;
                                break;
                            case 1:
                                password += c;
                                break;
                        }
                    } else { // we are reading zero//
                        ++zeroByteCounter;
                    }
                }
                zeroByteCounter = 0;
                if (opCode == 1) {
                    messageBuffer.clear();
                    opCodeBuffer.clear();
                    return new RegisterMsg(userName, password);
                }
                if (opCode == 2) {
                    messageBuffer.clear();
                    opCodeBuffer.clear();
                    return new LoginMsg(userName, password);
                }

            }
            messageBuffer.put(nextByte); // next byte is not zero//
            return null;
        }
        messageBuffer.put(nextByte); // next byte is not zero//
        return null;
    }
}



