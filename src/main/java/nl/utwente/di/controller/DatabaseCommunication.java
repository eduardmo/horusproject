package nl.utwente.di.controller;

import nl.utwente.di.model.Lecturer;
import nl.utwente.di.model.Request;
import nl.utwente.di.model.Room;
import nl.utwente.di.model.Status;

import javax.xml.ws.Response;
import java.sql.*;
import java.util.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class DatabaseCommunication {

    private static final String URL = "jdbc:postgresql://farm09.ewi.utwente.nl:7054/docker";

    /**
     * Establishes the connection to the database.
     * @return the connection.
     */
    public static Connection connect() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(URL, "docker", "YCPP2vGfS");
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            return conn;

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns a map with all the rooms from the database.
     * @return room map.
     */
    public static Map<String, Room> getRooms() throws SQLException {
        Map<String, Room> rooms = new HashMap<>();
        String sql = "SELECT * FROM room";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = connect();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            ResultSet result = pstmt.executeQuery();
            conn.commit();
            while(result.next()) {
                String roomNumber = result.getString(1);
                String building = result.getString(2);
                String shortRoomNumber = result.getString(3);
                String trivialName = result.getString(4);
                float area = result.getFloat(5);
                int capacityTimetable = result.getInt(6);
                int capacityLecture = result.getInt(7);
                int capacityWork = result.getInt(8);
                int capacityExam = result.getInt(9);
                int capacityReal = result.getInt(10);
                int firstRowHandicapped = result.getInt(11);
                int handicapped = result.getInt(12);
                String furniture = result.getString(13);
                String coordinates = result.getString(14);
                int floornumber = result.getInt(15);
                Room room = new Room(roomNumber, building, shortRoomNumber, trivialName, area,
                        capacityTimetable, capacityLecture, capacityWork, capacityExam, capacityReal,
                        firstRowHandicapped, handicapped, furniture, coordinates, floornumber);
                rooms.put(roomNumber, room);
            }
            return rooms;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                conn.rollback();
            }
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }
            conn.setAutoCommit(true);
        }
        conn.close();
        return rooms;
    }

    /**
     * This method takes a resultSet and from it constructs a list with all the requests from the database.
     * @param resultSet resulted from the query.
     * @return list of requests.
     * @throws SQLException
     */
    private static List<Request> createRequestList(ResultSet resultSet) throws SQLException {
        List<Request> requests = new ArrayList<>();
        Map<String, Room> rooms = getRooms();
        while (resultSet.next()) {
            int id = resultSet.getInt(1);
            Room oldRoom = rooms.get(resultSet.getString(2));
            String oldDate = resultSet.getString(3);
            String newDate = resultSet.getString(4);
            int teacherID = resultSet.getInt(5);
            String name = resultSet.getString(6);
            int numberOfStrudents = resultSet.getInt(7);
            String type = resultSet.getString(8);
            String notes = resultSet.getString(9);
            String courseType = resultSet.getString(10);
            String faculty = resultSet.getString(11);
            String status = resultSet.getString(12);
            String newRoom = resultSet.getString(13);
            String comments = resultSet.getString(14);
            Request request = new Request(id, oldRoom, oldDate, newDate, teacherID, name,
                    numberOfStrudents, type, notes, courseType, faculty);
            request.setStatus(status);
            request.setNewRoom(newRoom);
            request.setComments(comments);
            requests.add(request);
        }

        return requests;
    }

    /**
     * Returns a list with all the requests ordered descending.
     * @return the request list.
     */
    public static List<Request> getRequests() {
        String sql = "SELECT * FROM request ORDER BY id DESC;";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = connect();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            ResultSet result = pstmt.executeQuery();
            conn.commit();
            return createRequestList(result);
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds a new request to the database.
     * @param request object with the datas from the request.
     */
    public static void addNewRequest(Request request) {
        String sql = "INSERT INTO request(oldroom, olddate, newdate, teacherid, teachername, numberofstudents, requesttype, notes, coursetype, faculty, status, newroom, comms)" +
                " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = connect();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, request.getOldRoom().getRoomNumber());
            pstmt.setString(2, request.getOldDate());
            pstmt.setString(3, request.getNewDate());
            pstmt.setInt(4, request.getTeacherID());
            pstmt.setString(5, request.getTeacherName());
            pstmt.setInt(6, request.getNumberOfStudents());
            pstmt.setString(7, request.getType());
            pstmt.setString(8, request.getNotes());
            pstmt.setString(9, request.getCourseType());
            pstmt.setString(10, request.getFaculty());
            pstmt.setString(11, request.getStatus().toString());
            pstmt.setString(12, request.getNewRoom());
            pstmt.setString(13, request.getComments());
            pstmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds to the table the id of the request and id of the user who issued the request if the status of the request changes.
     * @param requestID of the request.
     * @param teacherID of the teacher.
     */
    public static void addNewRequest(int requestID, String teacherID) {
        String sql = "INSERT INTO new_req VALUES(?, ?);";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = connect();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, requestID);
            pstmt.setString(2, teacherID);
            pstmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to return an int value, mostly used for when returning a count.
     * @param sql which is going to be executed.
     * @return 0 if there was a problem with the connection or the querry returned nothing,
     * otherwise it returns the value.
     */
    private static int getInt(String sql) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = connect();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            ResultSet resultSet = pstmt.executeQuery();
            conn.commit();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Same thing aa above but now it return an integer value of an entry in a table.
     * @param sql which is going to be executed.
     * @param id of the entry.
     * @return 0 if there was a problem with the connection or the querry returned nothing,
     * otherwise it returns the value.
     */
    private static int getInt(String sql, int id) {
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = connect();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet resultSet = pstmt.executeQuery();
            conn.commit();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Returns the last id of table request or the id that is next if there is a discontinuation between ids.
     * @return the id.
     */
    public static int getId() {
        String sql = "SELECT id FROM request t WHERE NOT EXISTS" +
                "(SELECT id FROM request WHERE id = t.id + 1) LIMIT 1;";
        return getInt(sql);
    }

    /**
     * Returns a user from the database based on the email
     * @param id, email of the user
     * @return lecturer object
     */
    public static Lecturer getUSer(String id) {
        String sql = "SELECT * FROM users  WHERE email = ?;";
        PreparedStatement pstmt = null;
        Connection conn = null;
        Lecturer l;
        try {
            conn = connect();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            ResultSet resultSet = pstmt.executeQuery();
            conn.commit();
            if (resultSet.next()) {
                l = new Lecturer(resultSet.getInt("user_id"), resultSet.getString("staff_name"), resultSet.getString("email"));
                l.setPassowrd(resultSet.getString("password"));
                l.setTimetabler(resultSet.getBoolean("is_timetabler"));
                return l;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks if there exists an entry in a table in the database.
     * @param sql which is going to be executed.
     * @param check the value we check for.
     * @return true if the value exists, false otherwise.
     */
    public static boolean check(String sql, String check) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = connect();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, check);
            ResultSet resultSet = pstmt.executeQuery();
            conn.commit();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if a user exists in the database
     * @param lecturerid, email of the user
     * @return true if the user exists, false otherwise.
     */
    public static boolean checkExistingUser(String lecturerid) {
        String sql = "SELECT * FROM users WHERE email = ?;";
        return check(sql, lecturerid);
    }

    /**
     * Adds a new user to the database.
     * @param lecturer object which contains all the data of the user.
     */
    public static void addNewUser(Lecturer lecturer) {
        String sql = "INSERT INTO users VALUES(?, ?, ?, ?, ?)";
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = connect();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, lecturer.getTeacherId());
            pstmt.setString(2, lecturer.getEmail());
            pstmt.setString(3, lecturer.getPassword());
            pstmt.setString(4, lecturer.getName());
            pstmt.setBoolean(5, lecturer.isTimetabler());
            pstmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sql = "INSERT INTO favourites(id) VALUES (?);";
        try {
            conn = connect();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, lecturer.getTeacherId());
            pstmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the room is valid.
     * @param roomNr, number of the room.
     * @return true if the room is valid, false otherwise.
     */
    public static boolean checkValidRoom(String roomNr) {
        String sql = "SELECT FROM room WHERE room_number = ?;";
        return check(sql, roomNr);
    }

    /**
     * Returns all the request that have pending as status.
     * @return request list.
     */
    public static int getPendingRequests() {
        String sql = "SELECT count(*) FROM request WHERE status = 'pending';";
        return getInt(sql);
    }

    /**
     * Returns the requests that have pending as status of a certain teacher.
     * @param teacherID of the teacher.
     * @return requests list.
     */
    public static int getPendingRequests(int teacherID) {
        String sql = "SELECT count(*) FROM request WHERE status = 'pending' AND teacherid = ?;";
        return getInt(sql, teacherID);
    }

    /**
     * Returns all the accpeted requests of the teacher.
     * @param teacherID of the teacher.
     * @return requests list.
     */
    public static int getAcceptedRequests(int teacherID) {
        String sql = "SELECT count(*) FROM request WHERE status = 'accepted' AND teacherid = ?;";
        return getInt(sql, teacherID);
    }

    /**
     * Returns all the cancelled requests of the teacher.
     * @param teacherID of the teacher.
     * @return requests list.
     */
    public static int getCancelledRequests(int teacherID) {
        String sql = "SELECT count(*) FROM request WHERE status = 'cancelled' AND teacherid = ?;";
        return getInt(sql, teacherID);
    }

    /**
     * Returns all the requests that were handled this week of a teacher.
     * @param userID of the teacher.
     * @return requests list.
     */
    public static int getWeeklyHandledRequests(int userID) {
        String sql = "SELECT count(*) FROM request_handling WHERE timetabler_id = ? " +
                "AND CAST(current_date AS date) - CAST(date_handled AS date) <= 7";
        return getInt(sql, userID);
    }

    /**
     * Returns all the requests.
     * @return requests list.
     */
    public static int getTotalRequests() {
        String sql = "SELECT count(*) FROM request";
        return getInt(sql);
    }

    /**
     * Changes the status of the request into cancelled or accepted only if the request is pending.
     * @param status new status.
     * @param id of the request.
     */
    public static void changeRequestStatus(Status status, int id) {
        String sql = "UPDATE request SET status = ? WHERE id = ? AND status = 'pending'";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = connect();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status.toString());
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds into the database a new request handling which show when a new request was made by a teacher.
     * @param requestID of the request
     * @param userID of the teacher
     */
    public static void addRequestHandling(int requestID, int userID) {
        String sql = "INSERT INTO request_handling VALUES(?, ?, ?)";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTime now = LocalDateTime.now();
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = connect();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, requestID);
            pstmt.setString(2, dtf.format(now));
            pstmt.setInt(3, userID);
            pstmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns all the requests of a teacher.
     * @param teacherID of the teacher.
     * @return requests list.
     */
    public static List<Request> getRequests(int teacherID) {
        String sql = "SELECT * FROM request WHERE teacherid = ?;";
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = connect();
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, teacherID);
            ResultSet result = pstmt.executeQuery();
            connection.commit();
            return createRequestList(result);
        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Used to execute update operations on a table given a certain id.
     * @param sql which is going to be executed.
     * @param change the new value.
     * @param userID id of the entry.
     */
    private static void update(String sql, String change, int userID) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = connect();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, change);
            pstmt.setInt(2, userID);
            pstmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Changes the email of a user.
     * @param newEmail new email address.
     * @param userID of the user.
     */
    public static void changeEmail(String newEmail, int userID) {
        String sql = "UPDATE users SET email = ? WHERE user_id = ?;";
        update(sql, newEmail, userID);
    }

    /**
     * Changes the password of the user.
     * @param password new password of the user.
     * @param userID of the user.
     * @param oldPassword old password of the user.
     */
    public static void changePassword(String password, int userID, String oldPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ? AND password LIKE ?;";
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = connect();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, password);
            pstmt.setInt(2, userID);
            pstmt.setString(3, oldPassword);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Changes the name of a user.
     * @param newName of the user.
     * @param teacherID of the user.
     */
    public static void changeName(String newName, int teacherID) {
        String sql = "UPDATE users SET staff_name = ? WHERE user_id = ?";
        update(sql, newName, teacherID);
    }

    /**
     * Sets a default faculty for the teacher.
     * @param faculty desired faculty.
     * @param userID name of the user.
     */
    public static void setDefaultFaculty(String faculty, int userID) {
        String sql = "UPDATE favourites SET default_faculty = ? WHERE id IN " +
                "(SELECT user_id FROM users WHERE user_id = ?);";
        update(sql, faculty, userID);
    }

    /**
     * Sets a new room for the reschedule requests.
     * @param room room number.
     * @param id id of the request.
     */
    public static void setNewRoom(String room, int id) {
        String sql = "UPDATE request SET newroom = ? WHERE id = ? AND requesttype = 'reschedule';";
        update(sql, room, id);
    }

    /**
     * Adds comments to the request with a given id.
     * @param comments String of comments.
     * @param id of the request
     */
    public static void setComments(String comments, int id) {
        String sql = "UPDATE request SET comms = ? WHERE id = ?;";
        update(sql, comments, id);
    }

    /**
     * Adds a cookie in the database.
     * @param user_id of the user who logs in.
     * @param cookie the hashed cookie.
     */
    public static void addCookie(int user_id, String cookie) {
        String sql = "INSERT INTO cookies VALUES(?, ?);";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = connect();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, user_id);
            pstmt.setString(2, cookie);
            pstmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a user is already connected by checking if his id is in the cookie table.
     * @param userID of the user.
     * @return true if the user is connected, false otherwise.
     */
    public static void checkAlreadyConnected(int userID) {
        String sql = "SELECT user_id FROM cookies WHERE user_id = ?;";
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = connect();
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, userID);
            ResultSet resultSet = pstmt.executeQuery();
            connection.commit();
            if (resultSet.next()) {
                deletCookie(userID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the cookie of the user from the table cookie.
     * @param userID of the user.
     */
    public static void deletCookie(int userID) {
        String sql = "DELETE FROM cookies WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try  {
            conn = connect();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userID);
            pstmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the last id of a table.
     * @param sql which is going to be executed.
     * @return the id.
     */
    private static int getLastID(String sql) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = connect();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            ResultSet resultSet = pstmt.executeQuery();
            conn.commit();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Returns the last id from teacher table or the next id if there exists a discontinuation.
     * @return the id.
     */
    public static int getLasTeacherID() {
        String sql = "SELECT u.user_id FROM users u WHERE NOT EXISTS " +
                "(SELECT user_id FROM users WHERE user_id = u.user_id + 1);";
        return getLastID(sql);
    }

    /**
     * Adds a support ticket in the database.
     * @param id of the support ticket.
     * @param email email of the user.
     * @param head message title.
     * @param body content of the message.
     */
    public static void addSupport(int id, String email, String head, String body) {
        String sql = "INSERT INTO support VALUES(?, ?, ?, ?);";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = connect();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.setString(2, email);
            pstmt.setString(3, head);
            pstmt.setString(4, body);
            pstmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the last id from support table or the next id if there exists a discontinuation.
     * @return the id.
     */
    public static int getLastSupportID() {
        String sql = "SELECT s.ticket_id FROM support s WHERE NOT EXISTS " +
                "(SELECT ticket_id FROM support WHERE ticket_id = s.ticket_id + 1);";
        return getLastID(sql);
    }

    /**
     * Returns a list of integers from the table new_req.
     * @param teacherID email of the teacher who made the requests.
     * @return integer list.
     */
    public static List<Integer> getNewRequests(String teacherID) {
        String sql = "SELECT rid FROM new_req WHERE email = ?;";
        List<Integer> ids = new ArrayList<>();
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = connect();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, teacherID);
            ResultSet resultSet = pstmt.executeQuery();
            conn.commit();
            while (resultSet.next()) {
                ids.add(resultSet.getInt(1));
            }
            return ids;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }

    /**
     * Deletes an entry in the table new_req
     * @param teacherID of the user
     */
    public static void deleteNewRequests(String teacherID) {
        String sql = "DELETE FROM new_req WHERE email = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = connect();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, teacherID);
            pstmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Lecturer getTeacher(int userID) {
        String sql = "SELECT * FROM users WHERE user_id = ?;";
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = connect();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userID);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                String email = resultSet.getString(2);
                String password = resultSet.getString(3);
                String name = resultSet.getString(4);
                boolean isTimetabler = resultSet.getBoolean(5);
                Lecturer l = new Lecturer(id, name, email);
                l.setTimetabler(isTimetabler);
                l.setPassowrd(password);
                return l;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        List<Request> requests = DatabaseCommunication.getRequests();
        for (Request search : requests) {
            System.out.println(search.getId());
        }
    }

}
