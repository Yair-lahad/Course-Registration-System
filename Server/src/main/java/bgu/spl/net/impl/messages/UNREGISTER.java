package bgu.spl.net.impl.messages;

import bgu.spl.net.impl.Course;
import bgu.spl.net.impl.Database;
import bgu.spl.net.impl.User;

public class UNREGISTER extends Message<Short>{

    private int courseNum;

    public UNREGISTER(Short opcode) {
        super(opcode);
        this.setType(true);
    }

    @Override
    public void build(Short params) {
        courseNum = params;
    }

    @Override
    public void execute(Database DB, User user) {
        // unregister from course only allowed for Students who are registered to current course
        if (!user.isAdmin() & user.isCourseReg(courseNum)) {
            Course currCourse = DB.getCourse(courseNum);
            if(currCourse!=null){
                //only if seat successfully released we can unregister
                if(DB.releaseSeat(user.getUsername(),courseNum)){
                    // delete the course from the user courses list
                    user.unregCourse(courseNum);
                    setResultType((short) 12);
                }
            }
        }
    }
}


