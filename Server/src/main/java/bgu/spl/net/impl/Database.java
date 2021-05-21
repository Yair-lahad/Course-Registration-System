package bgu.spl.net.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Passive object representing the Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
public class Database {
	private ConcurrentHashMap<Integer, Course> courses;
	private ConcurrentHashMap<String, User> users;
	private List<Integer> coursesOrder;

	//to prevent user from creating new Database
	private Database() {
		this.courses = new ConcurrentHashMap<>();
		this.users = new ConcurrentHashMap<>();
		this.coursesOrder= new ArrayList<>();
		initialize("./Courses.txt");
	}

	private static class singleton {
		private static Database instance = new Database();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Database getInstance() {
		return singleton.instance;
	}



	/**
	 * loades the courses from the file path specified 
	 * into the Database, returns true if successful.
	 */
	public boolean initialize(String coursesFilePath) {
		// parsing rows from courses file into the data structure
		File f = new File(coursesFilePath);
		try (Scanner s = new Scanner(f)) {
			// read the text from the file in the given path
			int order=1;
			while (s.hasNextLine()) {
				String data = s.nextLine();
				String regex = "\\|";
				String[] dataS = data.split(regex);
				Course currentC = new Course(dataS,order);
				order = order + 1;
				// create course for each line, and put in the right places
				courses.putIfAbsent(currentC.getId(),currentC);
				// set Correct order for courses
				if(!coursesOrder.contains(currentC.getId()))
					coursesOrder.add(currentC.getId());

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public User getUser(String msg){
		return users.get(msg);
	}

	private static class CourseComparator implements Comparator<Course> {
		@Override
		public int compare(Course course, Course course1) {
			return course.getOrder()-course1.getOrder();
		}
	}

	/*public ArrayList<Integer> getUserCourses(User user){
		ArrayList<Integer> output=new ArrayList<>();
		synchronized (user){
			ConcurrentHashMap<Integer,Integer> tmp= user.getCourses();
			return tmp.sort(()-> getOrder()-course1.getOrder());
		}
	}*/

	public ArrayList<Integer> getCoursesOrder(User user) {
		// order according to courses txt file
		ArrayList<Integer> output=new ArrayList<>();
		synchronized (user) {
			for (int c : coursesOrder) {
				if (user.isCourseReg(c))
					output.add(c);
			}
		}
		return output;
	}

	public ArrayList<Integer> getkdamsOrder(int courseNum) {
		// returns kdams for course in order of courses txt file
		ArrayList<Integer> output = new ArrayList<>();
		ArrayList<Integer> kdams = courses.get(courseNum).getKdams();
		int size = kdams.size();
		for(int c: coursesOrder){
			if (kdams.contains(c)){
				output.add(c);
				size = size-1 ;
			}
			if (size == 0) break;
		}
		return output;
	}

	public Course getCourse(int courseNum){
		return courses.get(courseNum);
	}

	public User setUser(String userName, String password, boolean isAdmin) {
		synchronized (userName) {
			return users.putIfAbsent(userName, new User(userName, password, isAdmin));
		}
	}
	public User loginUser(String username){
		User currU=users.get(username);
		synchronized (username){
			if(!currU.isLoggedIn()){
				currU.setLoggedIn();
				return currU;
			}
		}
		return null;
	}
	public boolean takeSeat(Integer coursenum){
		Course currC= courses.get(coursenum);
		synchronized (coursenum){
			if(currC.getNumOfSeatAv()>0){
				currC.takeSeat();
				return true;
			}
		}
		return false;
	}

	public boolean releaseSeat(String studentName,Integer coursenum){
		Course currC= courses.get(coursenum);
		synchronized (coursenum){
			if(currC.getNumOfSeatAv()<currC.getMaxNumOfSeats()){
				currC.releaseSeat();
				currC.unregStudent(studentName);
				return true;
			}
		}
		return false;
	}
}
