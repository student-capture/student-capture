package studentcapture.course.participant;

import java.util.Map;

/**
 * 
 *
 */
public class Participant {
    private Integer userId;
    private Integer courseId;
    private String function;
    
    public Participant() {
    }

	public Participant(int userID,int courseID,String role){
		userId = userID;
		courseId = courseID;
		function = role;
	}
    
    public Participant(Map<String, Object> map) {
    	parseMap(map);
    }
    
    /**
     * Parses values from a map of database elements into class members.
     * 
     * @param map		map of database elements
     */
	private void parseMap(Map<String, Object> map) {
    	userId = (Integer) map.get("UserId");
		courseId = (Integer) map.get("CourseId");
		function = (String) map.get("Function");
    }
    
	/**
	 * @return the userId
	 */
	public Integer getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	/**
	 * @return the courseId
	 */
	public Integer getCourseId() {
		return courseId;
	}
	/**
	 * @param courseId the courseId to set
	 */
	public void setCourseId(Integer courseId) {
		this.courseId = courseId;
	}
	/**
	 * @return the function
	 */
	public String getFunction() {
		return function;
	}
	/**
	 * @param function the function to set
	 */
	public void setFunction(String function) {
		this.function = function;
	}
}
