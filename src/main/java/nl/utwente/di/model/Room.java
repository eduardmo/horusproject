package nl.utwente.di.model;

public class Room {
    private String roomNumber;
    private String building;
    private String shortRoomNumber;
    private String trivialName;
    private float area;
    private int capacityTimetable;
    private int capacityLecture;
    private int capacityWork;
    private int capacityExam;
    private int capacityReal;
    private int firstRowHandicapped;
    private int handicapped;
    private String furniture;
    private Gps coordinates;
    private int floorNumber;

    public Room(String roomNumber, String building, String shortRoomNumber, String trivialName, float area,
                int capacityTimetable, int capacityLecture, int capacityWork, int capacityExam, int capacityReal,
                int firstRowHandicapped, int handicapped, String furniture, String coordinates, int floorNumber) {
        this.roomNumber = roomNumber;
        this.building = building;
        this.shortRoomNumber = shortRoomNumber;
        this.trivialName = trivialName;
        this.area = area;
        this.capacityTimetable = capacityTimetable;
        this.capacityLecture = capacityLecture;
        this.capacityWork = capacityWork;
        this.capacityExam = capacityExam;
        this.capacityReal = capacityReal;
        this.firstRowHandicapped = firstRowHandicapped;
        this.handicapped = handicapped;
        this.furniture = furniture;
        String[] splitted = coordinates.split(",");
        this.coordinates = new Gps(Float.parseFloat(splitted[0]), Float.parseFloat(splitted[1]));
        this.floorNumber = floorNumber;
    }

    public String getBuilding() {
        return building;
    }

    public float getArea() {
        return area;
    }

    public int getCapacityExam() {
        return capacityExam;
    }

    public int getCapacityLecture() {
        return capacityLecture;
    }

    public int getCapacityReal() {
        return capacityReal;
    }

    public int getCapacityTimetable() {
        return capacityTimetable;
    }

    public int getCapacityWork() {
        return capacityWork;
    }

    public Gps getCoordinates() {
        return coordinates;
    }

    public int getFirstRowHandicapped() {
        return firstRowHandicapped;
    }

    public int getHandicapped() {
        return handicapped;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public String getFurniture() {
        return furniture;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getShortRoomNumber() {
        return shortRoomNumber;
    }

    public String getTrivialName() {
        return trivialName;
    }

    public void setArea(float area) {
        this.area = area;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public void setCapacityExam(int capacityExam) {
        this.capacityExam = capacityExam;
    }

    public void setCapacityLecture(int capacityLecture) {
        this.capacityLecture = capacityLecture;
    }

    public void setCapacityReal(int capacityReal) {
        this.capacityReal = capacityReal;
    }

    public void setCapacityTimetable(int capacityTimetable) {
        this.capacityTimetable = capacityTimetable;
    }

    public void setCapacityWork(int capacityWork) {
        this.capacityWork = capacityWork;
    }

    public void setCoordinates(Gps coordinates) {
        this.coordinates = coordinates;
    }

    public void setFirstRowHandicapped(int firstRowHandicapped) {
        this.firstRowHandicapped = firstRowHandicapped;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public void setFurniture(String furniture) {
        this.furniture = furniture;
    }

    public void setHandicapped(int handicapped) {
        this.handicapped = handicapped;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setShortRoomNumber(String shortRoomNumber) {
        this.shortRoomNumber = shortRoomNumber;
    }

    public void setTrivialName(String trivialName) {
        this.trivialName = trivialName;
    }
}

