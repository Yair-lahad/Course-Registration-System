package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.impl.CourseRegEncoderDecoder;
import bgu.spl.net.impl.CourseRegProtocol;
import bgu.spl.net.srv.Server;


public class TPCMain {
    public static void main(String[] args) {
        Server server = Server.threadPerClient(Integer.decode(args[0]).intValue(),  //port
                () -> new CourseRegProtocol(),
                () -> new CourseRegEncoderDecoder());
        server.serve();
    }

}
