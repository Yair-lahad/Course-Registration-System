package bgu.spl.net.impl.messages;

import bgu.spl.net.impl.Database;
import bgu.spl.net.impl.User;


public class LOGOUT extends Message<String>{

    public LOGOUT(Short opcode) {
        super(opcode);
        this.setType(false);
    }

    @Override
    public void build(String params) {}

    @Override
    public void execute(Database DB, User user) {
        // we entered this message only if we were logged in
        user.setLoggedOut();
        setResultType((short)12);
    }
}
