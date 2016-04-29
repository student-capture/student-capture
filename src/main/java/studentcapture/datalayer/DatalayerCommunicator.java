package studentcapture.datalayer;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import studentcapture.datalayer.database.Assignment;
import studentcapture.datalayer.database.Course;
import studentcapture.datalayer.database.Submission;
import studentcapture.datalayer.filesystem.FilesystemInterface;
import studentcapture.video.VideoInfo;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by c12osn on 2016-04-22.
 * Edited by c13arm, ens13ahr
 */
@RestController
@RequestMapping(value = "DB")
public class DatalayerCommunicator {



    @Autowired
    private Submission submission;
    @Autowired
    private Assignment assignment;
    @Autowired
            private Course course;
    //@Autowired
    FilesystemInterface fsi;
    @CrossOrigin()
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "getGrade", method = RequestMethod.POST)
    public MultiValueMap getGrade(@RequestParam(value = "studentID", required = false) String studentID,
                                  @RequestParam(value = "courseCode", required = false) String courseCode,
                                  @RequestParam(value = "courseID", required = false) String courseID,
                                  @RequestParam(value = "assignmentID", required = false) String assignmentID) {

        LinkedMultiValueMap<String, Object> returnData = new LinkedMultiValueMap<>();

        returnData.add("grade", submission.getGrade(studentID, assignmentID).get("grade"));
        returnData.add("time", submission.getGrade(studentID, assignmentID).get("time"));
        returnData.add("teacher",  submission.getGrade(studentID, assignmentID).get("teacher"));


        return returnData;
    }


    /**
     *
     * @param courseID
     * @param assignmentTitle
     * @param startDate
     * @param endDate
     * @param minTime
     * @param maxTime
     * @param published
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/createAssignment", method = RequestMethod.POST)
    public int createAssignment(@RequestParam(value = "courseID") String courseID,
                                @RequestParam(value = "assignmentTitle") String assignmentTitle,
                                @RequestParam(value = "startDate") String startDate,
                                @RequestParam(value = "endDate") String endDate,
                                @RequestParam(value = "minTime") String minTime,
                                @RequestParam(value = "maxTime") String maxTime,
                                @RequestParam(value = "published") boolean published){

        //int returnResult = ass.createAssignment(courseID, assignmentTitle, startDate, endDate, minTime, maxTime, published);

        return 1234;//returnResult;
    }

    /**
     * Save grade for a submission
     * @param gradeObject Contains assID, teacherID, studentID, grade
     * @return false if any value is null or update failed. Otherwise true
     */
    @CrossOrigin
    @RequestMapping(value = "/setGrade", method = RequestMethod.POST)
    public boolean setGrade(@RequestParam(value = "gradeObject") JSONObject gradeObject) {

        String assID = gradeObject.getString("assID");
        String teacherID = gradeObject.getString("teacherID");
        String studentID = gradeObject.getString("studentID");
        String grade = gradeObject.getString("grade");
        if(assID == null || teacherID == null || studentID == null || grade == null)
            return false;

        return submission.setGrade(Integer.parseInt(assID), teacherID, studentID, grade);
    }

    /**
     * Give feedback for a submission
     * @param feedbackObject Contains assID, teacherID, studentID, feedbackVideo, feedbackText
     * @return false if specific values are null or update failed. Otherwise true
     */
    @CrossOrigin
    @RequestMapping(value = "/setFeedback", method = RequestMethod.POST)
    public boolean setFeedback(@RequestParam(value = "feedbackObject") VideoInfo feedbackObject){

        /*String assID = feedbackObject.get
        String teacherID = feedbackObject.getString("teacherID");
        String studentID = feedbackObject.getString("studentID");
        Multipartfile feedbackVideo = feedbackObject.getString("feedbackVideo");
        String feedbackText = feedbackObject.getString("feedbackText");
        if(assID == null || teacherID == null || studentID == null)
            return false;
        int feedback = 0;
    	if(feedbackVideo != null) {
            // Call to filesystem API save feedback video
            feedback++;
        }
        if(feedbackText != null) {
            // Call to filesystem API save feedback text
            feedback++;
        }
        if(feedback == 0)
            return false;
        else
            return true;*/
    }

    /**
     * Fetches information about an assignment
     * @param assID Unique identifier for the assignment
     * @return Array containing [course ID, assignment title, opening datetime, closing datetime, minimum video time, maximum video time, description]
     */
    @CrossOrigin
    @RequestMapping(value = "/getAssignmentInfo", method = RequestMethod.POST)
    public ArrayList<String> getAssignmentInfo(@RequestParam(value = "assID") int assID){

        ArrayList<String> results = assignment.getAssignmentInfo(assID);
        //Need the courseCode for the path
        String courseCode = course.getCourseCodeFromId(results.get(0));
        FileInputStream descriptionStream = fsi.getAssignmentDescription(courseCode, results.get(0), assID);
        Scanner scanner = new Scanner(descriptionStream);
        String description = "";

        //Construct description string
        while (scanner.hasNext()){
            description += scanner.nextLine() + "\n";
        }
        results.add(description);
        return results;
    }
}
