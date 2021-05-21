package bgu.spl.net.impl.messages;

import bgu.spl.net.impl.Database;
import bgu.spl.net.impl.User;

public class MYCOURSES extends Message<String>{

    public MYCOURSES(Short opcode) {
        super(opcode);
        this.setType(false);
    }

    @Override
    public void build(String params) {}

    @Override
    public void execute(Database DB, User user) {
        // returns student specific registered courses
        if(!user.isAdmin()){
            setResult("\n"+DB.getCoursesOrder(user).toString().replaceAll(" ",""));
            setResultType((short)12);
        }
    }
}
