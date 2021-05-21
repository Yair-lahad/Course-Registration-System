package bgu.spl.net.impl.messages;

import bgu.spl.net.impl.Database;
import bgu.spl.net.impl.User;

public class USERREG extends Message<String[]> {

    private String userName;
    private String password;

    public USERREG(Short opcode) {
        super(opcode);
        this.setType(false);
    }

    public void build(String[] params){
        userName = params[0];
        password = params[1];
    }

    @Override
    public void execute(Database DB, User user) {
        // check if logged in
        if(user == null) {
            short opcode = getOpcode();
            User newUser = DB.setUser(this.userName, password, opcode == 1);
            // if user was created return ack
            if (newUser == null)
                setResultType((short) 12);
        }
    }

}
