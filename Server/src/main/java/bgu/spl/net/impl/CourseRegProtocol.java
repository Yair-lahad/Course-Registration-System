package bgu.spl.net.impl;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.messages.LOGIN;
import bgu.spl.net.impl.messages.LOGOUT;
import bgu.spl.net.impl.messages.Message;
import bgu.spl.net.impl.messages.USERREG;

public class CourseRegProtocol implements MessagingProtocol<Message> {
    private Database DB;
    // holds the current user logged in
    private User currentUser;
    private boolean shouldTerminate;

    public CourseRegProtocol(){

        DB = Database.getInstance();
        currentUser=null;
        shouldTerminate = false;
    }

    public Message process(Message msg) {
        // commands with login pre-condition will fail
        if (currentUser == null && !(msg instanceof LOGIN) && !(msg instanceof USERREG))
            return msg;
        else{
            msg.execute(DB, currentUser);
            if(msg instanceof LOGIN) {
                if (((LOGIN) msg).getLoggedInUser() != null)
                    currentUser = ((LOGIN) msg).getLoggedInUser();
            }
            else if (msg instanceof LOGOUT) {
                currentUser = null;
                if(msg.getResultType()==(short) 12){
                    shouldTerminate = true;
                }
            }
        }
        return msg;
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

}
