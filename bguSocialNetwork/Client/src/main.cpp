#include <iostream>
#include <stdlib.h>
#include <mutex>
#include <thread>
#include <vector>
#include "connectionHandler.h"
#include "ReceiveMessages.h"
using namespace std;

// is for my comments
//t is for the original comments

//my main is in charge of sending messages and one more thread is in charge of receiving messages



//t This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
void shortToBytes(short num, char *bytesArr) {
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}



int main(int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
    std::mutex mutex;
    ConnectionHandler connectionHandler(host, port);
    std::atomic<bool> terminate{};
    terminate.store(false);
    ReceiveMessages receiveMessages(connectionHandler, mutex, terminate);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    std::thread th1(&ReceiveMessages::run, &receiveMessages);

    while (!terminate.load()) {

        std::string line;
        std::string firstWord;
        getline(std::cin, line);
        std::istringstream str(line);
        str >> firstWord;

        if (firstWord == "REGISTER") {

            char opCode[2];
            shortToBytes((short) 1, opCode);
            if(!connectionHandler.sendBytes(opCode, 2)){
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
            str >> firstWord;
            if(!connectionHandler.sendLine(firstWord)){
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
            str >> firstWord;
            if(!connectionHandler.sendLine(firstWord)){
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }

        }
        if (firstWord == "LOGIN") {

            char opCode[2];
            shortToBytes((short) 2, opCode);
            if(!connectionHandler.sendBytes(opCode, 2)){
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
            str >> firstWord;
            if(!connectionHandler.sendLine(firstWord)){
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
            str >> firstWord;
            if(!connectionHandler.sendLine(firstWord)){
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }

        }
        if (firstWord == "LOGOUT") {
            short op = 3;
            char opcode[2];
            shortToBytes(op, opcode);
            if (!connectionHandler.sendBytes(opcode, 2)) {
                std::cout << "already logged out\n" << std::endl;
                break;
            }
            //break;
        }
        if (firstWord == "FOLLOW") {
            short followUnfollow;
            char follow_unfollow[2];
            short numOfUsers;
            std::string nextUser;
            char numberOfUsers[2];
            short op = 4;
            char opcode[2];
            shortToBytes(op, opcode);
            if(!connectionHandler.sendBytes(opcode, 2)){
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
            str >> followUnfollow;
            shortToBytes(followUnfollow,follow_unfollow);
            char fff[1];
            fff[0] = follow_unfollow[1];
            if(!connectionHandler.sendBytes(fff, 1)){
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
            str >> numOfUsers;
            shortToBytes(numOfUsers, numberOfUsers);

            if (!connectionHandler.sendBytes(numberOfUsers,2)) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }

            while (str.good() && str >> nextUser ){
                if(!connectionHandler.sendLine(nextUser)){
                    std::cout << "Disconnected. Exiting...\n" << std::endl;
                    break;
                }
            }
        }
        if (firstWord == "POST") {
            short op = 5;
            char opcode[2];
            shortToBytes(op, opcode);
            std::string tosend = line.substr(5);

            if(!connectionHandler.sendBytes(opcode, 2)){
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }

            if (!connectionHandler.sendLine(tosend)) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
        }
        if (firstWord == "PM") {
           std::string userName;
            short op = 6;
            char opcode[2];
            shortToBytes(op, opcode);
            if(!connectionHandler.sendBytes(opcode, 2)){
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
            str >> userName;
            if (!connectionHandler.sendLine(userName)) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
                std::string content = line.substr(userName.length() + 4);
            if (!connectionHandler.sendLine(content)) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
        }
        if (firstWord == "USERLIST") {
            short op = 7;
            char opcode[2];
            shortToBytes(op, opcode);
            if (!connectionHandler.sendBytes(opcode, 2)) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
        }
        if (firstWord == "STAT") {

            short op = 8;
            char opcode[2];
            shortToBytes(op, opcode);
            std::string userName;
            str >> userName;

            if (!connectionHandler.sendBytes(opcode, 2)) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
            if (!connectionHandler.sendLine(userName)) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }

        }
    }
    th1.join();
    return 0;
}


//std::vector<char> mergeArrIntoVector(string op, string username, string password){
//    vector<char> merged;
//    merged.emplace_back(op);
//    merged.emplace_back(username);
//    merged.emplace_back(password);
//    return merged;
//}

//t connectionHandler.sendLine(line) appends '\0' to the message. Therefor we send len+1 bytes.

//t We can use one of three options to read data from the server:
//t 1. Read a fixed number of characters
//t 2. Read a line (up to the newline character using the getline() buffered reader
//t 3. Read up to the null character

//t Get back an answer: by using the expected number of bytes (len bytes + newline delimiter)
//t We could also use: connectionHandler.getline(answer) and then get the answer without the newline char at the end

