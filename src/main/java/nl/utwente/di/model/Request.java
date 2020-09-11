package nl.utwente.di.model;

public class Request {

    private int id;
    private Room oldRoom;
    private String oldDate;
    private String newDate;
    private int teacherID;
    private String teacherName;
    private int numberOfStudents;
    private RequestType type;
    private String notes;
    private CourseType courseType;
    private String faculty;
    private Status status;
    private String newRoom;
    private String comments;

    public Request(int id, Room oldRoom, String oldDate, String newDate, int teacherID, String name,
                   int studentsNumber, String type, String notes, String courseType, String faculty) {
        this.oldDate = oldDate;
        this.newDate = newDate;
        this.id = id;
        this.oldRoom = oldRoom;
        this.teacherID = teacherID;
        this.numberOfStudents = studentsNumber;
        this.notes = notes;
        this.courseType = CourseType.valueOf(courseType);
        this.type = RequestType.valueOf(type);
        this.faculty = faculty;
        this.teacherName = name;
        this.status = Status.pending;
        this.comments = "";
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setNewRoom(String newRoom) {
        this.newRoom = newRoom;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOldRoom(Room oldRoom) {
        this.oldRoom = oldRoom;
    }

    public void setOldDate(String oldDate) {
        this.oldDate = oldDate;
    }

    public void setNewDate(String newDate) {
        this.newDate = newDate;
    }

    public void setTeacherID(int teacherID) {
        this.teacherID = teacherID;
    }

    public void setNumberOfStudents(int numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setStatus(String status) {
        this.status = Status.valueOf(status);
    }

    public int getId() {
        return id;
    }

    public Room getOldRoom() {
        return oldRoom;
    }

    public String getOldDate() {
        return oldDate;
    }

    public String getNewDate() {
        return newDate;
    }

    public int getNumberOfStudents() {
        return numberOfStudents;
    }

    public int getTeacherID() {
        return teacherID;
    }

    public String getType() {
        return type.toString();
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getCourseType() {
        return courseType.toString();
    }

    public String getNotes() {
        return notes;
    }

    public Status getStatus() {
        return status;
    }

    public String getNewRoom() {
        return newRoom;
    }

    public String getComments() {
        return comments;
    }
}
