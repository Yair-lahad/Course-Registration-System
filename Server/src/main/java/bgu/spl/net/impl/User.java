package bgu.spl.net.impl;

import java.util.concurrent.ConcurrentHashMap;

public class User {
    private String username;
    private String password;
    private boolean loggedIn;
    private ConcurrentHashMap<Integer,Integer> courses;
    private boolean admin;

    // this class holds a user object for course registration system
    public User(String username, String password, boolean admin) {
        this.username = username;
        this.password = password;
        loggedIn = false;
        courses = new ConcurrentHashMap<>();
        this.admin = admin;
    }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
    public void setLoggedIn(){
        loggedIn=true;
    }
    public void setLoggedOut(){
        loggedIn=false;
    }

    public boolean isCourseReg(Integer courseNum){
        synchronized (this){
            return courses.containsKey(courseNum);
        }
    }

    public void regCourse(Integer courseNum){
        synchronized (this){
            courses.putIfAbsent(courseNum,courseNum);
        }
    }

    public void unregCourse(Integer courseNum){
        synchronized (this) {
            courses.remove(courseNum);
        }
    }
    public ConcurrentHashMap<Integer, Integer> getCourses() {
        return courses;
    }

}
