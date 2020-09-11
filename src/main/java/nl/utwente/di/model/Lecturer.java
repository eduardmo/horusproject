package nl.utwente.di.model;

public class Lecturer {
    private int teacherId;
    private String name;
    private String email;
    private String password;
    private boolean isTimetabler;

    public Lecturer(int teacherId, String name, String email) {
        this.teacherId = teacherId;
        this.name = name;
        this.email = email;
        this.isTimetabler = false;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public int getTeacherId() {
        return teacherId;
    }
    
    public String getPassword() {
    	return password;
    }

    public boolean isTimetabler() {
        return isTimetabler;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }
    
    public void setPassowrd(String password) {
    	this.password = password;
    }

    public void setTimetabler(boolean timetabler) {
        isTimetabler = timetabler;
    }
}
