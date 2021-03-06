package studentcapture.submission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import studentcapture.datalayer.filesystem.FilesystemConstants;
import studentcapture.datalayer.filesystem.FilesystemInterface;
import studentcapture.model.Grade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class SubmissionDAO {

	// This template should be used to send queries to the database
	@Autowired
	protected JdbcTemplate databaseConnection;

	/**
	 * Add a new submission for an assignment
	 *
	 * @param submission the submission to be added
	 * @param studentConsent if a student want their submission to be viewed by others
     * @return True if everything went well, otherwise false.
     */
	boolean addSubmission(Submission submission, Boolean studentConsent) {
		String sql = "INSERT INTO Submission (assignmentId, studentId, SubmissionDate, studentpublishconsent, status)" +
					" VALUES  (?,?,?,?,?)";
		java.util.Date date = new java.util.Date(System.currentTimeMillis());
		java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
		timestamp.setNanos(0);
		int rowsAffected;
		try {
			rowsAffected = databaseConnection.update(sql, submission.getAssignmentID(),
                                                            submission.getStudentID(),
                                                            timestamp,
                                                            studentConsent,
                                                            submission.getStatus());
		} catch (DataAccessException e) {
			return false;
		}
		if(submission.getStudentVideo() != null) {
				FilesystemInterface.storeStudentVideo(submission, submission.getStudentVideo());
        }
		return rowsAffected == 1;
	}



    /**
     * Patch a submission for an assignment
     *
     * @param submission the submission to patch
     * @return True if everything went well, otherwise false
	 * @throws DataAccessException If the data could'nt be saved
	 */
    boolean patchSubmission(Submission submission) throws DataAccessException {
		String feedback = submission.getFeedback();
		if (feedback != null && !feedback.isEmpty()) {
			String path = FilesystemInterface.generatePath(submission) + FilesystemConstants.FEEDBACK_TEXT_FILENAME;
			FilesystemInterface.printTextToFile(feedback, path);
		}

        String sql = "UPDATE Submission SET ";
        ArrayList<Object> sqlparams = new ArrayList<>();
        if (submission.getPublishStudentSubmission() != null) {
            sql += "publishstudentsubmission = ?,";
            sqlparams.add(submission.getPublishStudentSubmission());
        }
        if (submission.getStatus() != null) {
            sql += "status = ?,";
            sqlparams.add(submission.getStatus());
        }
        if (sqlparams.isEmpty()) {
            return false; // Nothing to patch
        }
        sql = sql.substring(0,sql.length()-1);

        sql += " WHERE assignmentid = ? AND studentid = ?";
        sqlparams.add(submission.getAssignmentID());
        sqlparams.add(submission.getStudentID());

		return databaseConnection.update(sql, sqlparams.toArray()) == 1;

    }

    /**
     * Make the feedback visible for the student
     * @param submission Submission object
     * @return True if a row was changed, otherwise false
	 * @throws IllegalAccessError Cant publish feedback if not a teacher.
     */
    boolean publishFeedback(Submission submission, boolean publish)
														throws IllegalAccessException {
        /* Publishing feedback without a grade is not possible, returns false */
        Grade grade = submission.getGrade();
        System.out.println("GRADE: " + grade);
        if (grade == null) {
			return false;
		}

		/* If a person that is not a teacher tries to set a grade, return false */
        if (!checkIfTeacher(submission.getCourseID(), submission.getGrade().getTeacherID())) {
			throw new IllegalAccessException("Cant set grade, user not a teacher");
		}

        String publishFeedback  = "UPDATE Submission SET publishFeedback = ? WHERE (AssignmentID = ?) AND (StudentID = ?);";
        int updatedRows = databaseConnection.update(publishFeedback, publish, submission.getAssignmentID(), submission.getStudentID());

        return updatedRows == 1;
    }

	/**
	 * Add a grade for a submission
	 *
	 * @param submission Submission object
	 * @return True if a row was changed, otherwise false
	 * @throws IllegalAccessError Cant set grade if not a teacher.
	 */
	boolean setGrade(Submission submission, Integer userId) throws IllegalAccessException,DataIntegrityViolationException {
		Grade grade = submission.getGrade();
        /* If a person that is not a teacher tries to set a grade, return false */

        if (!checkIfTeacher(submission.getCourseID(), userId)){
			throw new IllegalAccessException("Cant set grade, user not a teacher");
		}

        String setGrade  = "UPDATE Submission SET Grade = ?, TeacherID = ?, PublishStudentSubmission = ?" +
				" WHERE (AssignmentID = ?) AND (StudentID = ?);";
		int updatedRows = databaseConnection.update(setGrade, grade.getGrade(),
																grade.getTeacherID(),
																submission.getPublishStudentSubmission(),
																submission.getAssignmentID(),
																submission.getStudentID());
		return updatedRows == 1;
	}



	/**
	 * Remove a submission
	 *
	 * @param submission
	 * @return True if everything went well, otherwise false
	 */
	boolean removeSubmission(Submission submission) {
		String removeSubmissionStatement = "DELETE FROM "
				+ "Submission WHERE (AssignmentId=? AND StudentId=?)";
		boolean result;

		try {
			int rowsAffected = databaseConnection.update(removeSubmissionStatement,
					submission.getAssignmentID(), submission.getStudentID());
			result = rowsAffected == 1;
		} catch (DataAccessException e) {
			result = false;
		}

		return result;
	}

    /**
     * Get all ungraded submissions for an assignment
     *
     * @param assignmentID The assignment to get submissions for
     * @return A list of ungraded submissions for the assignment
     */
    public List<Submission> getAllUngraded(int assignmentID) {

		List<Submission> submissions;
		String getAllSubmissionsStatement = "SELECT * FROM Submission, Users WHERE AssignmentId = ? " +
				"AND studentid = userid AND Grade is NULL";
		try {
			submissions = databaseConnection.query(getAllSubmissionsStatement, new SubmissionRowMapper(), assignmentID);
		} catch (DataAccessException e) {
			return null;
		}
		return submissions;
    }

	/**
	 * Get all submissions for an assignment
	 * @param assignmentID The assignment to get submissions for
	 * @return A list of submissions for the assignment
	 */
    List<Submission> getAllSubmissions(int assignmentID) {
    	List<Submission> submissions;
		String getAllSubmissionsStatement = "SELECT * FROM Submission, Users WHERE AssignmentId = ? AND studentid = userid";
		try {
			submissions = databaseConnection.query(getAllSubmissionsStatement, new SubmissionRowMapper(), assignmentID);
		} catch (DataAccessException e) {
			return null;
		}
    	return submissions;
    }
    
    /**
     * Gets an entire submission with the name of the teacher, if the teacher
	 * exists.
     * 
     * @param assignmentID	The assignmentId that the submission is connected
	 *                      to.
     * @param userID		The studentId that the submission is connected to.
     * @return				The submission with the teacher name.
     */
    public Optional<Submission> getSubmission(int assignmentID, int userID) {
    	Submission result;
        String getStudentSubmission =
				"SELECT * FROM Submission WHERE AssignmentId=? AND StudentId=?";
		String getTeacherName =
				"SELECT concat(FirstName, ' ', LastName) FROM Users WHERE UserId=?";

		try {
	        result = databaseConnection.queryForObject(
					getStudentSubmission, new SubmissionRowMapper(), assignmentID, userID);
			if (result.getGrade().getTeacherID() != null) {
				result.setTeacherName(databaseConnection.queryForObject(getTeacherName, new Object[]{result.getGrade().getTeacherID()}, String.class));
			} else if (result.getGrade().getTeacherID() == null) {
				result.setTeacherName(null);
			}

	    } catch (IncorrectResultSizeDataAccessException e){
			//TODO
		    return Optional.empty();
		} catch (DataAccessException e1){
			//TODO
			return Optional.empty();
		}
		result.setCourseID(getCourseIDFromAssignmentID(result.getAssignmentID()));
		result.setFeedback(FilesystemInterface.getFeedbackText(result));
        return Optional.of(result);
	}

	/**
	 * Get a teacher's submitted feedback video for a specific student.
	 * @param submission the submission which the video should be linked to.
     * @return An input stream contained within a HTTP response entity.
     */
	ResponseEntity<InputStreamResource> getVideo(Submission submission, String fileName) {
		Integer courseID = getCourseIDFromAssignmentID(submission.getAssignmentID());
		if(courseID == null){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}else{
			submission.setCourseID(courseID);
			String path = FilesystemInterface.generatePath(submission) + fileName;
			return FilesystemInterface.getVideo(path);
		}
	}


    /**
     * Adds a feedback video to a submission.
     *
     * @param submission the submission which to add the video to.
     * @param feedbackVideo the feedback video.
     * @return true if it succeeds, otherwise false.
     */
	boolean setFeedbackVideo(Submission submission, MultipartFile feedbackVideo) {
		return FilesystemInterface.storeFeedbackVideo(submission, feedbackVideo);
	}

	/**
	 * Retrieves the course id from an assignment by querying the database.
     * Returns null if something went wrong.
     *
	 * @param assignmentID the id of the assignment.
	 * @return the course id.
     */
	public Integer getCourseIDFromAssignmentID(int assignmentID){
		try{
			String getCourseId = "SELECT CourseId FROM Assignment WHERE AssignmentId=?";
			return databaseConnection.queryForObject(getCourseId, new Object[]{assignmentID}, Integer.class);
		}catch(Exception e){
			return null;
		}
	}

    /**
     * Checks if the user is a teacher.
     * @param courseID The ID of the course.
     * @param userId The user's ID.
     * @return True if the user is a teacher, otherwise throw exception with appropriate message.
     */
    private boolean checkIfTeacher(Integer courseID, Integer userId){
        String checkIfTeacherExist = "SELECT COUNT(*) " +
                "FROM participant " +
                "WHERE" +
                " (UserID = ?) " +
                "AND (CourseID = ?) " +
                "AND ( upper(Function) = upper('Teacher') )";
        System.err.println(userId);
        int rows = databaseConnection.queryForInt(checkIfTeacherExist,
                userId,
                courseID);

		return rows == 1;
	}

}

