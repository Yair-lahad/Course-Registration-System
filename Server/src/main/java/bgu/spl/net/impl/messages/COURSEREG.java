package bgu.spl.net.impl.messages;

import bgu.spl.net.impl.Course;
import bgu.spl.net.impl.Database;
import bgu.spl.net.impl.User;
import java.util.ArrayList;

public class COURSEREG extends Message<Short> {

    private int courseNum;

    public COURSEREG(Short opcode) {
        super(opcode);
        this.setType(true);
    }

    @Override
    public void build(Short params) {
        this.courseNum = params;
    }


    @Override
    public void execute(Database DB,User user) {
        //register only allowed to Studends who is not registered to this course
        if (!user.isAdmin() && !user.isCourseReg(courseNum)) {
            Course currCourse = DB.getCourse(courseNum);
            if (currCourse != null) {
                //check kdams
                int count = 0;
                ArrayList<Integer> Kdams = currCourse.getKdams();
                for (Integer i : Kdams) {
                    if (!user.isCourseReg(i))
                        break;
                    count++;
                }
                // register to course if all kdams are registered
                if (count == Kdams.size()) {
                    // if there is available seat, register
                    if (DB.takeSeat(courseNum)) {
                        user.regCourse(courseNum);
                        // updating the course's students
                        DB.getCourse(courseNum).regStudent(user.getUsername());
                        setResultType((short) 12);
                    }
                }
            }
        }
    }
}

