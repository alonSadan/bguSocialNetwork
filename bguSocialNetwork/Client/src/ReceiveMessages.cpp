#include "ReceiveMessages.h"
#include "connectionHandler.h"
#include <mutex>
using namespace std;



//t constructor locks the mutex while destructor (out of scope) unlocks it   //t std::lock_guard<std::mutex> lock(_mutex);

ReceiveMessages::ReceiveMessages(ConnectionHandler &connectionHandler, std::mutex &mutex,
                                 std::atomic<bool> &terminate) : ch(connectionHandler), _mutex(mutex), _terminate(terminate) {

}

void ReceiveMessages::run() {
    char answer [2];
    while (!_terminate.load()) {
        ch.getBytes(answer , 2);
        process(answer);
    }
}



void ReceiveMessages::process(char* ans){
    short opCode = bytesToShort(ans);
    switch (opCode) {
        case 9 : {
            string opCodeString = "NOTIFICATION ";
            char NotificationType[1];
            ch.getBytes(NotificationType, 1);
            short Notification_Type = bytesToShort(NotificationType);
            string NotificationToPrint;
            if (Notification_Type == (short)0){
                NotificationToPrint = "PM";
            }
            else {NotificationToPrint = "Public";}
            string PostingUser;
            ch.getLine(PostingUser);
            PostingUser.resize(PostingUser.length() - 1);
            string content;
            ch.getLine(content);
            content.resize(content.length() - 1);
            cout << opCodeString + " " + NotificationToPrint + " " + PostingUser + " " + content<< endl;
            break;
        }

        case 10: {
            string opCodeString = "ACK";
            char MessageOpCode[2];
            ch.getBytes(MessageOpCode, 2);
            short MessageOP = bytesToShort(MessageOpCode);
            string toprint = opCodeString + " " + std::to_string(MessageOP);
            switch (MessageOP) {
                case 3: {
                    ch.close();
                    _terminate.store(true);
                    break;
                }
                case 4: {
                    char NumOfUsers [2];
                    ch.getBytes(NumOfUsers , 2);
                    int NumUsers = bytesToShort(NumOfUsers);
                    toprint += " " + to_string(NumUsers);
                    for(int i = 0 ; i < NumUsers ; i++ ){
                        string userName;
                        ch.getLine(userName);
                        userName.resize(userName.length() - 1);
                        toprint += " " + userName;
                    }
                    break;
                }
                case 7 : {
                    char NumOfUsers [2];
                    ch.getBytes(NumOfUsers , 2);
                    short NumUsers = bytesToShort(NumOfUsers);
                    toprint += " " + to_string(NumUsers);
                    for(int i = 0 ; i < NumUsers ; i++ ){
                        string userName;
                        ch.getLine(userName);
                        userName.resize(userName.length() - 1);
                        toprint += " " + userName;
                    }
                    break;
                }
                case 8 : {
                    char NumPosts[2];
                    ch.getBytes(NumPosts, 2);
                    short Num_posts = bytesToShort(NumPosts);
                    char Numfollowers[2];
                    ch.getBytes(Numfollowers, 2);
                    short Num_followers = bytesToShort(Numfollowers);
                    char NumFollowing[2];
                    ch.getBytes(NumFollowing, 2);
                    short Num_following = bytesToShort(NumFollowing);

                    toprint = opCodeString + " " + std::to_string(MessageOP) + " " +to_string(Num_posts)+ " " + to_string(Num_followers)
                            + " " + to_string(Num_following);
                    break;
                }
                default:break;
            }
            cout << toprint<< endl;
            break;
        }
        case 11 : {
            char MessageOpCode[2];
            ch.getBytes(MessageOpCode, 2);
            int MessageOP = bytesToShort(MessageOpCode);
            cout<< "ERROR " + std::to_string(MessageOP) << endl;
        }
        default:break;
    }
}

short ReceiveMessages::bytesToShort(char *bytesArr) {
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}








