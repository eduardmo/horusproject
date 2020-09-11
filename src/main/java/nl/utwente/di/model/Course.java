package nl.utwente.di.model;

public class Course {

    private String courseID;
    private String name;
    private String module;
    private CourseType type;
    private Room location;
    private String startDate;
    private String endDate;

    public Course(String courseID, String name, String module, String type) {
        this.courseID = courseID;
        this.name = name;
        this.module = module;
        this.type = CourseType.valueOf(type);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public void setType(String type) {
        this.type = CourseType.valueOf(type);
    }

    public String getName() {
        return name;
    }

    public String getCourseID() {
        return courseID;
    }

    public String getType() {
        return type.toString();
    }

    public String getModule() {
        return module;
    }


}
