package studentcapture.datalayer;

import java.io.FileInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import studentcapture.datalayer.database.Assignment;
import studentcapture.datalayer.database.Submission;
import studentcapture.datalayer.filesystem.FilesystemInterface;

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
     * @param assID
     * @param teacherID
     * @param studentID
     * @param grade
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/setGrade", method = RequestMethod.POST)
    public boolean setGrade(@RequestParam(value = "assID") String assID,
            				@RequestParam(value = "teacherID") String teacherID,
            				@RequestParam(value = "studentID") String studentID,
            				@RequestParam(value = "grade") String grade) {
    	
    	return false;
    }
    
    /**
     * Give feedback for a submission
     * @param assID
     * @param teacherID
     * @param studentID
     * @param feedbackVideo	Can be null
     * @param feedbackText	Can be null
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/giveFeedback", method = RequestMethod.POST)
    public boolean giveFeedback(@RequestParam(value = "assID") String assID,
            				@RequestParam(value = "teacherID") String teacherID,
            				@RequestParam(value = "studentID") String studentID/*,
            				@RequestParam(value = "feedbackVideo") video feedbackVideo,
            				@RequestParam(value = "feedbackText") text feedbackText*/){
    	
    	return false;
    }
}
