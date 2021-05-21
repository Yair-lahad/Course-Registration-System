package bgu.spl.net.impl.messages;


import bgu.spl.net.impl.Database;
import bgu.spl.net.impl.User;

public class LOGIN extends Message<String[]>{
    String username;
    String password;
    User loggedinUser;

    public LOGIN(Short opcode) {
        super(opcode);
        this.setType(false);
    }

    @Override
    public void build(String[] params) {
        username = params[0];
        password = params[1];
    }

    @Override
    public void execute(Database DB, User user) {
        User currU=DB.getUser(username);
        // log in only if registerd and password is correct
        if (user == null && currU!=null && currU.getPassword().equals(password)) {
            loggedinUser=DB.loginUser(username);
            if(loggedinUser!=null)
                setResultType((short) 12);
        }
    }
    public User getLoggedInUser(){
        return loggedinUser;
    }

}
