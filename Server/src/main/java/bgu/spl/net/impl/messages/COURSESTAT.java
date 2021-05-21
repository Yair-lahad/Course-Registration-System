package bgu.spl.net.impl.messages;

import bgu.spl.net.impl.Course;
import bgu.spl.net.impl.Database;
import bgu.spl.net.impl.User;
import java.util.ArrayList;


public class COURSESTAT extends Message<Short> {

    private int courseNum;

    public COURSESTAT(Short opcode) {
        super(opcode);
        this.setType(true);
    }

    @Override
    public void build(Short params) {
        courseNum = params;
    }

    @Override
    public void execute(Database DB, User user) {
        if (!user.isAdmin())
            setResult("needs admin permissions");
        else {
            Course course = DB.getCourse(courseNum);
            if (course == null)
                setResult("no such course");
            else {
                ArrayList<String> studentsList = course.orderRegStudent();
                setResult("\nCourse: (" + courseNum + ") " + course.getName() + "\n" +
                        "Seats Available: " + course.getNumOfSeatAv() + "/" + course.getMaxNumOfSeats() + "\n" +
                        "Students Registered: " + studentsList.toString().replaceAll(" ","")); //ordered alphabetically
                setResultType((short) (12));
            }
        }
    }
}
