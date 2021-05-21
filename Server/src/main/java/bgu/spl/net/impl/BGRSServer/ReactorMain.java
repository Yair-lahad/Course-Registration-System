package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.impl.CourseRegEncoderDecoder;
import bgu.spl.net.impl.CourseRegProtocol;
import bgu.spl.net.srv.Reactor;


public class ReactorMain {
    public static void main(String[] args) {

        Reactor server = new Reactor(
                Integer.parseInt(args[1]),   // number of working threads
                Integer.decode(args[0]).intValue(),  //port
                () -> new CourseRegProtocol(),
                () -> new CourseRegEncoderDecoder());
        server.serve();
    }

}
