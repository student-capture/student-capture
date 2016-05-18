CREATE TABLE IF NOT EXISTS Users (
    UserId       SERIAL         PRIMARY KEY,
    UserName     VARCHAR(64)    UNIQUE,
    FirstName    VARCHAR(64)    NOT NULL,
    LastName     VARCHAR(64)    NOT NULL,
    Email        VARCHAR(128)   NOT NULL,
    Pswd         VARCHAR(64)    NOT NULL
    );

CREATE TABLE IF NOT EXISTS Course (
    CourseId             SERIAL         PRIMARY KEY,
    Year                 INT            NOT NULL,
    Term                 VARCHAR(8)     NOT NULL,
    CourseName           VARCHAR(64)    NOT NULL,
    CourseDescription    VARCHAR(1024),
    Active               BOOLEAN        NOT NULL
    );

CREATE TABLE IF NOT EXISTS Participant (
    UserId       INT           references Users(UserId),
    CourseId     VARCHAR(10)   references Course(CourseId),
    Function     VARCHAR(64)   NOT NULL,
    PRIMARY KEY (UserId, CourseId)
    );

CREATE TABLE IF NOT EXISTS Assignment (
    AssignmentId                SERIAL         PRIMARY KEY,
    CourseId                    VARCHAR(10)    references Course(CourseId),
    Title                       VARCHAR(64)    NOT NULL,
    StartDate                   timestamp      NOT NULL,
    EndDate                     timestamp      NOT NULL,
    MinTime                     INT            NOT NULL,
    MaxTime                     INT            NOT NULL,
    Published                   timestamp,
    GradeScale                  VARCHAR(64)    NOT NULL
    );

CREATE TABLE IF NOT EXISTS Submission (
    AssignmentId                INT          references Assignment(AssignmentId),
    StudentId                   INT          references Users(UserId),
    StudentPublishConsent       Boolean ,
    Status                      VARCHAR(32), --Should be 'blank', 'answer' or 'no answer'
    SubmissionDate              timestamp    NOT NULL,
    Grade                       VARCHAR (3),
    TeacherId                   INT          references Users(UserId),
    PublishStudentSubmission    Boolean ,
    PublishFeedback             Boolean ,
    PRIMARY KEY (AssignmentId, StudentId)
    );
CREATE TABLE IF NOT EXISTS Config (
    UserId                      INT         references Users(UserId),
    Language                    VARCHAR(64) NOT NULL,
    EmailNotify                 Boolean,
    TextSize                    INT         NOT NULL,
    PRIMARY KEY (UserId)
    );
