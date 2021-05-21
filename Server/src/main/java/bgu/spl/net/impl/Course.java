package bgu.spl.net.impl;
import java.util.ArrayList;
import java.util.Collections;

public class Course {
    private int id;
    private String name;
    private ArrayList<Integer> kdams;
    private int maxNumOfSeats;
    private int numOfSeatAv;
    private ArrayList<String> regStudents;
    private int order;


    public Course(String[] courseInfo,int order) {
        this.id = Integer.parseInt(courseInfo[0]);
        this.name = courseInfo[1];
        String dataC = courseInfo[2];
        // update kdam courses if exists
        this.kdams = new ArrayList<>();
        if(dataC.length()>2) {
            dataC = dataC.substring(1, dataC.length() - 1);
            String[] kdams = dataC.split(",");
            for (String s : kdams) {
                this.kdams.add(Integer.parseInt(s));
            }
        }
        this.maxNumOfSeats = Integer.parseInt(courseInfo[3]);
        this.numOfSeatAv = maxNumOfSeats;
        regStudents = new ArrayList<>();
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Integer> getKdams() {
        return kdams;
    }

    public int getMaxNumOfSeats() {
        return maxNumOfSeats;
    }

    public int getNumOfSeatAv() {
        return numOfSeatAv;
    }

    public void releaseSeat() {
        this.numOfSeatAv = numOfSeatAv+1;
    }

    public void regStudent(String studentName){
        synchronized (regStudents){
            regStudents.add(studentName);
        }
    }

    public void unregStudent(String studentName){
        synchronized (regStudents) {
            regStudents.remove(studentName);
        }
    }

    public ArrayList<String> orderRegStudent(){
        synchronized (regStudents){
            Collections.sort(regStudents);
        }
        return regStudents;
    }

    public void takeSeat() { this.numOfSeatAv = numOfSeatAv-1;}
}
