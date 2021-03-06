package studentcapture.assignment;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Optional;

/**
 * Created by victor on 2016-04-28.
 * RestController for assignments.
 */
@RestController
@RequestMapping("/assignments")
public class AssignmentResource {

    @Autowired
    private AssignmentDAO assignmentDAO;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    AssignmentErrorInfo handleFailedRequest(HttpServletRequest req, Exception ex) {
        //Nested exceptions. The exception thrown by this app is in the third level.
        return new AssignmentErrorInfo(ex.getCause().getCause().getMessage());
    }

    @RequestMapping(method = RequestMethod.POST)
    public int createAssignment(@RequestBody AssignmentModel assignment) throws IOException {
        return assignmentDAO.createAssignment(assignment);
    }

    @RequestMapping(value = "/{assignmentID}/video", method = RequestMethod.POST)
    public void addAssignmentVideo(@RequestParam("video") MultipartFile video,
                                   @RequestParam("courseID") Integer courseID,
                                   @PathVariable("assignmentID") String assignmentID) {
        assignmentDAO.addAssignmentVideo(video, courseID, assignmentID);
    }

    /**
     * Update an assignments information (not video).
     * To update the assignment video, simply use
     * addAssignmentVideo above (smelly, should be a PUT method).
     *
     * @param assignment An assignment, including its assignmentID, to update to.
     */
    @RequestMapping(method = RequestMethod.PUT)
    public void updateAssignment(@RequestBody AssignmentModel assignment) throws Exception {
        assignmentDAO.updateAssignment(assignment);
    }

    @RequestMapping(value = "/{assignmentID}", method = RequestMethod.DELETE)
    public void deleteAssignment(@PathVariable("assignmentID") int assignmentID,
                                 @RequestParam("courseID") int courseID) throws IOException {
        AssignmentModel assignment = new AssignmentModel();
        assignment.setAssignmentID(assignmentID);
        assignment.setCourseID(courseID);
        assignmentDAO.removeAssignment(assignment);
    }

    /**
     * Gets the video question corresponding to the specified assignment.
     *
     * @param assignmentID Unique assignment identifier.
     * @return The video and Http status OK
     * or Http status NOT_FOUND.
     */
    @RequestMapping(value = "/{assignmentID}/video", method = RequestMethod.GET, produces = "video/webm")
    public ResponseEntity<InputStreamResource> getAssignmentVideo(
            @PathVariable("assignmentID") int assignmentID) throws NotFoundException, IOException {

        Optional<AssignmentModel> assignment = assignmentDAO.getAssignmentModel(assignmentID);

        try {
            if (assignmentDAO.hasAccess(assignment.get()) && assignmentDAO.canDoAssignment(assignment.get())) {
                return new ResponseEntity<>(assignmentDAO.getAssignmentVideo(assignmentID).getBody(), HttpStatus.OK);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    /**
     * Gets the assignment model corresponding to the specified assignment.
     *
     * IF the time is outside the interval where you can do the assignment
     * the recap will not be sent. This is a fast "fix" for a bigger problem.
     *
     * @param assignmentID Unique assignment identifier.
     * @return The assignment and Http status OK
     * or Http status NOT_FOUND.
     */
    @RequestMapping(value = "/{assignmentID}", method = RequestMethod.GET)
    public ResponseEntity<AssignmentModel> getAssignment(@PathVariable("assignmentID") int assignmentID, HttpSession session)
            throws NotFoundException, IOException {
        Optional<AssignmentModel> assignment = assignmentDAO.getAssignmentModel(assignmentID);

        if (assignment.isPresent()) {
            try {
                AssignmentModel assignmentModel = assignment.get();
                if (assignmentDAO.hasAccess(assignmentModel)) {
                    if(assignmentDAO.canDoAssignment(assignmentModel)) {
                        return new ResponseEntity<>(assignmentModel, HttpStatus.OK);
                    } else {
                        // "remove" the recap
                        assignmentModel.setRecap("");
                        return new ResponseEntity<>(assignmentModel, HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
