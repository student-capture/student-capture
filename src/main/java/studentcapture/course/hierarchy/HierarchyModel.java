package studentcapture.course.hierarchy;

import studentcapture.assignment.AssignmentModel;
import studentcapture.course.CourseModel;
import studentcapture.submission.Submission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to collect all relevant data in a users course-assignment
 * hierarchy. A course-assignment hierarchy is a container of all 
 * information required to display the main page.
 *
 */
public class HierarchyModel {
	private int userId;
	private String firstName;
	private String lastName;
	private Boolean isTeacher;
	private Map<Integer, CoursePackage> teacherCourses;
	private Map<Integer, CoursePackage> studentCourses;
	private List<CoursePackage> teacherCoursesList;
	private List<CoursePackage> studentCoursesList;

	public HierarchyModel() {
		teacherCourses = new HashMap<>();
		studentCourses = new HashMap<>();
		teacherCoursesList = null;
		studentCoursesList = null;
	}
	
	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the teacherCourses
	 */
	public Map<Integer, CoursePackage> getTeacherCourses() {
		return teacherCourses;
	}

	/**
	 * @param teacherCourses the teacherCourses to set
	 */
	public void setTeacherCourses(Map<Integer, CoursePackage> teacherCourses) {
		this.teacherCourses = teacherCourses;
	}

	/**
	 * @return the studentCourses
	 */
	public Map<Integer, CoursePackage> getStudentCourses() {
		return studentCourses;
	}

	/**
	 * @param studentCourses the studentCourses to set
	 */
	public void setStudentCourses(Map<Integer, CoursePackage> studentCourses) {
		this.studentCourses = studentCourses;
	}

	/**
	 * @return the teacherCoursesList
	 */
	public List<CoursePackage> getTeacherCoursesList() {
		return teacherCoursesList;
	}

	/**
	 * @param teacherCoursesList the teacherCoursesList to set
	 */
	public void setTeacherCoursesList(List<CoursePackage> teacherCoursesList) {
		this.teacherCoursesList = teacherCoursesList;
	}

	/**
	 * @return the studentCoursesList
	 */
	public List<CoursePackage> getStudentCoursesList() {
		return studentCoursesList;
	}

	/**
	 * @param studentCoursesList the studentCoursesList to set
	 */
	public void setStudentCoursesList(List<CoursePackage> studentCoursesList) {
		this.studentCoursesList = studentCoursesList;
	}
	
    /**
	 * @return the isTeacher
	 */
	public Boolean getIsTeacher() {
		return isTeacher;
	}

	/**
	 * @param isTeacher the isTeacher to set
	 */
	public void setIsTeacher(Boolean isTeacher) {
		this.isTeacher = isTeacher;
	}
	
	public void moveMapsToLists() {
		teacherCoursesList = new ArrayList<>(teacherCourses.values());
		teacherCoursesList.forEach(CoursePackage::moveMapsToLists);
		
		studentCoursesList = new ArrayList<>(studentCourses.values());
		studentCoursesList.forEach(CoursePackage::moveMapsToLists);
		
		teacherCourses = null;
		studentCourses = null;
	}

	/**
     * Used to collect data related to a course in a users course-assignment
     * hierarchy.
     *
     */
    public static class CoursePackage {
    	private CourseModel course;
    	private Map<Integer, AssignmentPackage> assignments;
		private List<AssignmentPackage> assignmentsList;

    	public CoursePackage() {
    		assignments = new HashMap<>();
    		assignmentsList = null;
    	}

		public void moveMapsToLists() {
			assignmentsList = new ArrayList<>(assignments.values());
			assignmentsList.forEach(AssignmentPackage::moveMapsToLists);
			
			assignments = null;
		}

		/**
		 * @return the course
		 */
		public CourseModel getCourse() {
			return course;
		}

		/**
		 * @param course the course to set
		 */
		public void setCourse(CourseModel course) {
			this.course = course;
		}
		
		/**
		 * @return the assignments
		 */
		public Map<Integer, AssignmentPackage> getAssignments() {
			return assignments;
		}

		/**
		 * @param assignments the assignments to set
		 */
		public void setAssignments(Map<Integer, AssignmentPackage> assignments) {
			this.assignments = assignments;
		}

		/**
		 * @return the assignmentsList
		 */
		public List<AssignmentPackage> getAssignmentsList() {
			return assignmentsList;
		}

		/**
		 * @param assignmentsList the assignmentsList to set
		 */
		public void setAssignmentsList(List<AssignmentPackage> assignmentsList) {
			this.assignmentsList = assignmentsList;
		}
    }

    /**
     * Used to collect data related to an assignment in a users
     * course-assignment hierarchy.
     *
     */
    public static class AssignmentPackage {
    	private AssignmentModel assignment = null;
    	private Map<Integer, Submission> submissions = null;
    	private List<Submission> submissionsList = null;
		
    	/**
		 * @return the assignment
		 */
		public AssignmentModel getAssignment() {
			return assignment;
		}

		/**
		 * @param assignment the assignment to set
		 */
		public void setAssignment(AssignmentModel assignment) {
			this.assignment = assignment;
		}

		/**
		 * @return the submissions
		 */
		public Map<Integer, Submission> getSubmissions() {
			return submissions;
		}

		/**
		 * @param submissions the submissions to set
		 */
		public void setSubmissions(Map<Integer, Submission> submissions) {
			this.submissions = submissions;
		}

		/**
		 * @return the submissionsList
		 */
		public List<Submission> getSubmissionsList() {
			return submissionsList;
		}

		/**
		 * @param submissionsList the submissionsList to set
		 */
		public void setSubmissionsList(List<Submission> submissionsList) {
			this.submissionsList = submissionsList;
		}

		public void moveMapsToLists() {
    		if(submissions!=null) {
    			submissionsList = new ArrayList<>(submissions.values());
    		
    			submissions = null;
    		}
		}
    }
}
