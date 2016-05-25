/**
 * Created by sejiaw on 2016-05-10.
 */


/**
 * Creates the back button used by the GUI.
 * Makes it possible for the teacher to go back to the previous page.
 */
//Creates the BackButton

var BackButton = React.createClass({
    // Upon clicking on the button, it should go back to the previous GUI window.
    /**
     * Render TeacherViewSubmission when back button is pressed
     */
    onClick: function() {
        ReactDOM.render(<TeacherViewSubmission courseId={this.props.idArray[0].courseID} assignmentId={this.props.idArray[0].assignmentID}/>,document.getElementById('courseContent'));

    },
    // Render function for BackButton
    render: function() {

        return(
            <button id="backbutton" onClick={this.onClick}>Back</button>

        );
    }
});

window.BackButton = BackButton;