package bgu.spl.net.impl.messages;

import bgu.spl.net.impl.Database;
import bgu.spl.net.impl.User;

public class ISREGISTERED extends Message<Short>{

    private int courseNum;

    public ISREGISTERED(Short opcode) {
        super(opcode);
        this.setType(true);
    }

    @Override
    public void build(Short params) {
        courseNum = params;
    }

    @Override
    public void execute(Database DB, User user) {
        boolean registered = DB.getUser(user.getUsername()).isCourseReg(courseNum);
        if (registered)
            setResult("\nREGISTERED");
        else { setResult("\nNOT REGISTERED");}
        setResultType((short)12);
    }
}
