package bgu.spl.net.impl.messages;


import bgu.spl.net.impl.Database;
import bgu.spl.net.impl.User;

public class STUDENTSTAT extends Message<String[]> {

    String username;

    public STUDENTSTAT(Short opcode) {
        super(opcode);
        this.setType(false);
    }

    @Override
    public void build(String[] params) {
        username = params[0];
    }

    @Override
    public void execute(Database DB, User user) {
        // admin message to receive info about a specific student
        if(user.isAdmin()){
            setResult("\nStudent: " + username + "\n" +
                    "Courses: " + DB.getCoursesOrder(DB.getUser(username)).toString().replaceAll(" ",""));
            setResultType((short)12);
        }
    }
}
