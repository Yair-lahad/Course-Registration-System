package bgu.spl.net.impl.messages;


import bgu.spl.net.impl.Course;
import bgu.spl.net.impl.Database;
import bgu.spl.net.impl.User;

public class KDAMCHECK extends Message<Short> {
    private int courseNum;

    public KDAMCHECK(Short opcode) {
        super(opcode);
        this.setType(true);
    }

    @Override
    public void build(Short params) {
        courseNum = params;
    }

    @Override
    public void execute(Database DB, User user) {
        Course course = DB.getCourse(courseNum);
        // check kdams only if student (admin not allowed)
        if (!user.isAdmin() & course != null) {
            setResult("\n"+DB.getkdamsOrder(courseNum).toString().replaceAll(" ",""));
            setResultType((short)12);
        }
    }
}
