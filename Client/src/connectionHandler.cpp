#include "../include/connectionHandler.h"

using boost::asio::ip::tcp;
using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;
using std::vector;
using namespace std;


ConnectionHandler::ConnectionHandler(string host, short port): host_(host), port_(port), io_service_(), socket_(io_service_) {
}
    
ConnectionHandler::~ConnectionHandler() {
    close();
}
 
bool ConnectionHandler::connect() {
    std::cout << "Starting connect to " 
        << host_ << ":" << port_ << std::endl;
    try {
		tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
		boost::system::error_code error;
		socket_.connect(endpoint, error);
		if (error)
			throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

void ConnectionHandler::readFromKeyboard() {
    while (1) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line = string(buf);
        if (!sendLine(line)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
    }
}

bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp ) {
			tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
			tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::getLine(std::string& line) {
    return getFrameAscii(line, '\0');
}

bool ConnectionHandler::sendLine(std::string& line) {
    return sendFrameAscii(line, '\0');
}
 

bool ConnectionHandler::getFrameAscii(std::string& frame, char delimiter) {
    char ch;
    int tmp = 0;
    short opcode;
    short opcodeMessage;
    bool stop = false;
    // Stop when we encounter the null character.
    // Notice that the null character is not appended to the frame string.
    try {
	do{
	    // handles message according to opcode type
		if(!getBytes(&ch, 1))
			return false;
        char opcodeAr[2];
        char opcodeMessageAr[2];
		if (tmp < 2)
            opcodeAr[tmp] = ch;
		if ((tmp > 1) & (tmp < 4))
		    opcodeMessageAr[tmp-2] = ch;
		if (tmp == 3){
            opcode = bytesToShort(opcodeAr);
            opcodeMessage = bytesToShort(opcodeMessageAr);
            if (opcode == (short)12)
                frame.append("ACK ");
            else {
                frame.append("ERROR ");
                stop = true;
            }
            frame.append(std::to_string(opcodeMessage));
		}
		if(tmp > 3){
            if (ch!='\0')
                frame.append(1, ch);
		}
        tmp = tmp+1;
	}while ((!stop) & ((tmp < 5)| (delimiter != ch)));
    } catch (std::exception& e) {
	std::cerr << "recv failed2 (Error: " << e.what() << ')' << std::endl;
	return false;
    }
    return true;
}


bool ConnectionHandler::sendFrameAscii(const std::string& frame, char delimiter) {
    std::map<string, vector<short>> opcodes{
            {"ADMINREG",     {1, 0,1}},
            {"STUDENTREG",   {2, 0,1}},
            {"LOGIN",        {3, 0,1}},
            {"LOGOUT",       {4, 0,0}},
            {"COURSEREG",    {5, 1,0}},
            {"KDAMCHECK",    {6, 1,0}},
            {"COURSESTAT",   {7, 1,0}},
            {"STUDENTSTAT",  {8, 0,1}},
            {"ISREGISTERED", {9, 1,0}},
            {"UNREGISTER",  {10, 1,0}},
            {"MYCOURSES",   {11, 0,0}}
    };
    // convert message name to short with correct opcode
    string separator = " ";
    string token1 = frame.substr(0, frame.find(separator));
    string token2 = frame.substr(frame.find(separator) + 1, frame.length());
    short opcode = opcodes.find(token1)->second[0];
    bool result;
    if ((opcodes.find(token1)->second[1]) == (short)1) {
        char message[4];
        shortToBytes(opcode, message, 0);
        short courseNum = stoi(token2);
        shortToBytes(courseNum, message, 2);
        result = sendBytes(message, 4);
    } else {
        //converts rest of the message to bytes
        int a = token2.length() + 2;
        if ((opcode == 4) | (opcode == 11)){
            a = 2;
        }
        char message[a];
        shortToBytes(opcode, message, 0);
        int j = 2;
        if ((opcode != 4) & (opcode != 11)) {
            for (char i : token2) {
                if (i == ' ') {
                    message[j] = '\0';
                } else { message[j] = i; }
                j = j + 1;
            }
        }
        result = sendBytes(message, a);
        if (opcodes.find(token1)->second[2] == 1){ return sendBytes(&delimiter, 1);}
    }
    return result;
}


// Close down the connection properly.
void ConnectionHandler::close() {
    try{
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }
}

void ConnectionHandler::shortToBytes(short num, char* bytesArr, int ind) {
    bytesArr[ind] = ((num >> 8) & 0xFF);
    bytesArr[ind+1] = (num & 0xFF);
}

short ConnectionHandler::bytesToShort(char* bytesArr) {
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}
