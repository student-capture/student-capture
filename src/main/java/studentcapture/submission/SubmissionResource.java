package studentcapture.submission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studentcapture.assignment.AssignmentDAO;
import studentcapture.course.CourseDAO;

import java.util.List;

/**
 * Class:       SubmissionResource
 * <p>
 * Author:      Erik Moström
 * cs-user:     erikm
 * Date:        5/17/16
 */

@CrossOrigin
@RestController
@RequestMapping(value = "assignments/{assignmentID}/submissions/")
public class SubmissionResource {
    @Autowired
    SubmissionDAO DAO;

    @RequestMapping(value = "{studentID}", method = RequestMethod.GET)
    public ResponseEntity<Submission> getSpecificSubmission(@PathVariable("assignmentID") int assignmentID,
                                                            @PathVariable("studentID") int studentID){
        //TODO fix unity in DAO API

        Submission body = DAO.getSubmission(assignmentID, studentID).get();
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Submission>> getAllSubmissions(@PathVariable("assignmentID") int assignmentID){
        //TODO check permissions
        List<Submission> body = DAO.getAllSubmissions(assignmentID).get();
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @RequestMapping(value = "{studentID}", method = RequestMethod.PATCH)
    public HttpStatus markSubmission(@PathVariable("assignmentID") int assignmentID,
                                     @PathVariable("studentID") int studentID,
                                     @RequestBody Submission submission){
        /*Validation of Submission
        * if sent by a student: send to a method which only stores the information a student has permission to change (i.e not grade)
        * if sent by a teacher: send to a method which only stores the information a teacher has permission to change (i.e not the answer but the grade)
        *
        * validate the Submission.studentID against studentID and permissions*/
        submission.setAssignmentID(assignmentID);
        submission.setStudentID(studentID);
        if (DAO.patchSubmission(submission)) {
            return HttpStatus.OK;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }


    @RequestMapping(value = "{studentID}", method = RequestMethod.PUT)
    public HttpStatus storeSubmission(@PathVariable("assignmentID") int assignmentID,
                                      @PathVariable("studentID") int studentID,
                                      @RequestBody Submission updatedSubmission){

        HttpStatus returnStatus;

        updatedSubmission.setStudentID(studentID);
        updatedSubmission.setAssignmentID(assignmentID);
        returnStatus = DAO.addSubmission(updatedSubmission, true) ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;


        /*Validation of Submission
        * Should be sent by a student, might have to validate that the student didnt set the grade himself.
        * However this should probably be handled somewhere else
        * validate the Submission.studentID against studentID and permissions*/

        System.out.println("WORKS");
        System.out.println(updatedSubmission.toString());
        System.out.println("DONE");
        return returnStatus;
    }

    @RequestMapping(value = "{studentID}", method = RequestMethod.DELETE)
    public HttpStatus deleteSubmission(@PathVariable("assignmentID") String assignment,
                                       @PathVariable("studentID") String studentID){
        /*Check permission*/
        return HttpStatus.NOT_IMPLEMENTED;
    }
}
