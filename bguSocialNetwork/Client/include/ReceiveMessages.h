
#ifndef CLIENT_RECIVEMESSAGES_H
#define CLIENT_RECIVEMESSAGES_H


#include <mutex>
#include "connectionHandler.h"

class ReceiveMessages {
private:
    ConnectionHandler &ch;
    std::mutex &_mutex;
    std::atomic<bool> &_terminate;
public:
    ReceiveMessages(ConnectionHandler &connectionHandler, std::mutex &mutex, std::atomic<bool> &terminate);
    void run();
    void process(char* ans );
    short bytesToShort(char* bytesArr);

};



#endif //CLIENT_RECIVEMESSAGES_H
