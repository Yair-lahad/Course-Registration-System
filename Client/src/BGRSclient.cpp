#include "../include/connectionHandler.h"
#include <stdlib.h>
#include <thread>

int main (int argc, char *argv[]) {
    std::string host = argv[1];
    short port = atoi(argv[2]);
    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    // thread which handles keyboard input and send message to server
    std::thread th1(&ConnectionHandler::readFromKeyboard, &connectionHandler);

    // main thread send to server and prints to user
    while (1){
        std::string answer;
        if (!connectionHandler.getLine(answer)) {
            break;
        }
        std::cout << answer << std::endl;
    }
    th1.join();
    return 0;
}










