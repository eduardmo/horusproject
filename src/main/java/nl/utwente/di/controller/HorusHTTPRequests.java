package nl.utwente.di.controller;

import nl.utwente.di.exceptions.AlreadyConnectedException;
import nl.utwente.di.exceptions.InvalidInputException;
import nl.utwente.di.exceptions.InvalidPasswordException;
import nl.utwente.di.model.Lecturer;
import nl.utwente.di.model.Request;
import nl.utwente.di.model.Room;
import nl.utwente.di.model.Status;
import nl.utwente.di.security.Encryption;
import nl.utwente.di.security.PasswordStorage;
import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Path("/requests")
public class HorusHTTPRequests {

    PasswordStorage hashMaster = new PasswordStorage();

    /**
     * Sends a get request that returns a list with all the requests as json.
     * @return requests list as json.
     */
    @GET
    @Produces("application/json")
    public List<Request> getRequests() {
        return DatabaseCommunication.getRequests();
    }

    /**
     * Sends a get requests that returns all the requests of a teacher.
     * @param user name of the teacher.
     * @return requests list as json
     */
    @GET
    @Path("/user")
    @Produces("application/json")
    public List<Request> getRequest(@HeaderParam("user") int user) {
        return DatabaseCommunication.getRequests(user);
    }

    /**
     * Sends a get requests which returns a user and checks the credentials in order to log in.
     * @param username, the email of the user.
     * @param password, password of the user.
     * @param timestamp, timestamp when log in was done in order to create a cookie
     * @return response 200 if the log in was successful and a json with the session id or BAD_REQUEST otherwise.
     * @throws AlreadyConnectedException
     * @throws InvalidPasswordException
     */
    @GET
    @Path("/login")
    @Consumes("application/json")
    @Produces("application/json")
    public Response logIn(@HeaderParam("username") String username,
                          @HeaderParam("password") String password,
                          @HeaderParam("timestamp") long timestamp) throws AlreadyConnectedException, InvalidPasswordException {
        Lecturer lecturer = DatabaseCommunication.getUSer(username);

        boolean isPasswordOk = false;
        try {
            isPasswordOk = hashMaster.verifyPassword(password, new String(Base64.getDecoder().decode(lecturer.getPassword())));
        } catch (PasswordStorage.CannotPerformOperationException e) {
            e.printStackTrace();
        } catch (PasswordStorage.InvalidHashException e) {
            e.printStackTrace();
        }

        if (!isPasswordOk){
            throw new InvalidPasswordException();
        }

        String sessionID = lecturer.getTeacherId() + String.valueOf(timestamp);
        Encryption e = new Encryption();
        try {
            sessionID = e.encrypt(sessionID);
        } catch (NoSuchPaddingException e1) {
            e1.printStackTrace();
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (InvalidParameterSpecException e1) {
            e1.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (BadPaddingException e1) {
            e1.printStackTrace();
        } catch (IllegalBlockSizeException e1) {
            e1.printStackTrace();
        } catch (InvalidKeySpecException e1) {
            e1.printStackTrace();
        } catch (InvalidKeyException e1) {
            e1.printStackTrace();
        }
        DatabaseCommunication.checkAlreadyConnected(lecturer.getTeacherId());
        DatabaseCommunication.addCookie(lecturer.getTeacherId(), sessionID);
        if (lecturer != null) {
            JSONObject jsonObject = new JSONObject().put("teacherID", lecturer.getTeacherId())
                                                    .put("name", lecturer.getName())
                                                    .put("email", lecturer.getEmail())
                                                    .put("isAdmin", lecturer.isTimetabler())
                                                    .put("sessionID", sessionID);
            return Response.ok(jsonObject.toString(), "application/json").build();
        }  else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    /**
     * Checks if the json for adding a request is valid.
     * @param jsonObject containing all the data of the request
     * @return true if the json is correct, false otherwise.
     */
    private boolean checkValidRequestJSON(JSONObject jsonObject) {
        System.out.println(jsonObject);
        return jsonObject.has("oldRoom") && jsonObject.has("oldDate") && jsonObject.has("newDate") &&
                jsonObject.has("teacherID") && jsonObject.has("numberOfStudents") && jsonObject.has("type") &&
                jsonObject.has("name") && jsonObject.has("notes") && jsonObject.has("courseType") && jsonObject.has("faculty");
    }

    /**
     * Sends a post request which adds a new request in the database.
     * @param requestString, json object representing the request.
     * @throws InvalidInputException
     */
    @POST
    @Consumes("application/json")
    public void addRequest(String requestString) throws InvalidInputException, SQLException {
        JSONObject jsonObject = new JSONObject(requestString);
        if (!checkValidRequestJSON(jsonObject)) {
            throw new InvalidInputException();
        }
        Map<String, Room> rooms = DatabaseCommunication.getRooms();
        int id = DatabaseCommunication.getId() + 1;
        Room oldRoom = rooms.get(jsonObject.getString("oldRoom"));
        if (!DatabaseCommunication.checkValidRoom(oldRoom.getRoomNumber())) {
            throw new InvalidInputException();
        }
        String oldDate = jsonObject.getString("oldDate");
        String newDate = jsonObject.getString("newDate");
        int teacherID = jsonObject.getInt("teacherID");
        int numberOfStudents = jsonObject.getInt("numberOfStudents");
        String requestType = jsonObject.getString("type");
        String name = jsonObject.getString("name");
        String notes = jsonObject.getString("notes");
        String courseType = jsonObject.getString("courseType");
        String faculty = jsonObject.getString("faculty");
        Request request = new Request(id, oldRoom, oldDate, newDate, teacherID, name, numberOfStudents, requestType,
                notes, courseType, faculty);
        DatabaseCommunication.addNewRequest(request);
    }

    /**
     * Sends a post request which registers a new user.
     * @param lecturerString, json object representing the user.
     * @return a response if the operations was successful or not.
     */
    @POST
    @Path("/register")
    @Consumes("application/json")
    public Response addUser(String lecturerString) {
        JSONObject lecturerJson = new JSONObject(lecturerString);
        int teacherid = DatabaseCommunication.getLasTeacherID() + 1;
        String name = lecturerJson.getString("name");
        String password = lecturerJson.getString("password");
        String email = lecturerJson.getString("email");
        Lecturer lecturer = new Lecturer(teacherid, name, email);
        try {
            lecturer.setPassowrd(Base64.getEncoder().encodeToString(hashMaster.createHash(password).getBytes()));
        } catch (PasswordStorage.CannotPerformOperationException e) {
            e.printStackTrace();
            System.out.println("ERROR in password hashing");
        }
        if (DatabaseCommunication.checkExistingUser(lecturer.getEmail())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        DatabaseCommunication.addNewUser(lecturer);
        return Response.status(Response.Status.OK).build();
    }

    /**
     * Returns the number of pending requests.
     * @return the number of pending requests.
     */
    @GET
    @Path("/pending/admin")
    @Produces("application/json")
    public int getPendingRequests() {
        return DatabaseCommunication.getPendingRequests();
    }

    /**
     * Returns the number of pending requests of a teacher.
     * @return the number of pending requests of a teacher.
     */
    @GET
    @Path("/pending/user")
    @Consumes("application/json")
    public int getPendingRequests(@HeaderParam("teacherID") int teacherID) {
        return DatabaseCommunication.getPendingRequests(teacherID);
    }

    /**
     * Returns the number of pending and handled requests of a teacher.
     * @return the number of pending and handled requests of a teacher.
     */
    @GET
    @Path("/handled")
    @Consumes("application/json")
    public int getHandledRequests(@HeaderParam("teacherID") int teacherID) {
        return DatabaseCommunication.getWeeklyHandledRequests(teacherID);
    }

    /**
     * Returns the total number of requests.
     * @return the total number of requests.
     */
    @GET
    @Path("/total")
    @Consumes("application/json")
    public int getTotalRequests() {
        return DatabaseCommunication.getTotalRequests();
    }

    /**
     * Returns the number of pending and accepted requests of a teacher.
     * @return the number of pending and accepted requests of a teacher.
     */
    @GET
    @Path("/accepted")
    @Consumes("application/json")
    public int getAcceptedRequests(@HeaderParam("teacherID") int teacherID) {
        return DatabaseCommunication.getAcceptedRequests(teacherID);
    }

    /**
     * Returns the number of pending and cancelled requests of a teacher.
     * @return the number of pending and cancelled requests of a teacher.
     */
    @GET
    @Path("/cancelled")
    @Consumes("application/json")
    public int getCancelledRequests(@HeaderParam("teacherID") int teacherID) {
        return DatabaseCommunication.getCancelledRequests(teacherID);
    }

    /**
     * Updates the status of a requests
     * @param jsonBody containing the values to update
     * @return a response if the actions was successful or not.
     */
    @PUT
    @Path("/statusChange")
    @Consumes("application/json")
    public Response changeStatus(String jsonBody) {
        JSONObject jsonObject = new JSONObject(jsonBody);
        String status = jsonObject.getString("status");
        int id = jsonObject.getInt("id");
        String comments = jsonObject.getString("comments");
        String newRoom = jsonObject.getString("newRoom");
        int userID = jsonObject.getInt("userID");
        int teacherID = jsonObject.getInt("teacherID");
        DatabaseCommunication.changeRequestStatus(Status.valueOf(status), id);
        DatabaseCommunication.setComments(comments, id);
        DatabaseCommunication.setNewRoom(newRoom, id);
        DatabaseCommunication.addRequestHandling(id, userID);
        DatabaseCommunication.addNewRequest(id, ""+teacherID);
        return Response.status(Response.Status.OK).build();
    }

    /**
     * Changes the email of a user.
     * @param newEmail the user wants to have.
     * @param userID of the user.
     * @return a response if the actions was successful or not.
     */
    @PUT
    @Path("/changeEmail")
    @Consumes("application/json")
    public Response changeEmail(@HeaderParam("newEmail") String newEmail,
                                @HeaderParam("user") int userID) {
        DatabaseCommunication.changeEmail(newEmail, userID);
        return Response.status(Response.Status.OK).build();
    }

    /**
     * Changes the password of user.
     * @param newPass the user wants.
     * @param userID of the user.
     * @param oldPass of the user.
     * @return a response if the actions was successful or not.
     */
    @PUT
    @Path("/changePassword")
    @Consumes("application/json")
    public Response changePassword(@HeaderParam("newPass") String newPass,
                                   @HeaderParam("user") int userID,
                                   @HeaderParam("oldPass") String oldPass) throws PasswordStorage.InvalidHashException, PasswordStorage.CannotPerformOperationException {

        Lecturer lecturer = DatabaseCommunication.getTeacher(userID);
        if (hashMaster.verifyPassword(oldPass, new String(Base64.getDecoder().decode(lecturer.getPassword())))) {
            String newHash = hashMaster.createHash(newPass);
            newHash = Base64.getEncoder().encodeToString(newHash.getBytes());
            DatabaseCommunication.changePassword(newHash, userID, lecturer.getPassword());
            System.out.println(newHash);
        }
        return Response.status(Response.Status.OK).build();
    }

    /**
     * Sets the default faculty of a teacher.
     * @param faculty which is going to be default.
     * @param userID of the teacher.
     * @return a response if the actions was successful or not.
     */
    @PUT
    @Path("/defaultFaculty")
    @Consumes("application/json")
    public Response setDefaultFaculty(@HeaderParam("faculty") String faculty,
                                      @HeaderParam("user") int userID) {
        DatabaseCommunication.setDefaultFaculty(faculty, userID);
        return Response.status(Response.Status.OK).build();
    }

    /**
     * Removes from the database the cookies of the connected user.
     * @param userID of the user who logs out.
     * @return a response if the actions was successful or not.
     */
    @DELETE
    @Path("/logout")
    public Response logOut(@HeaderParam("user") int userID) {
        DatabaseCommunication.deletCookie(userID);
        return Response.status(Response.Status.ACCEPTED).build();
    }

    /**
     * Returns a list of the rooms found by the gaze.
     * @param requestID of the request.
     * @return the list with all the rooms found.
     */
    @GET
    @Path("/gazeIntoTheAbyss")
    @Produces("application/json")
    public List<String> startGazeOfHorus(@HeaderParam("requestID") int requestID) throws SQLException {
        return Gaze.lookUpForRooms(requestID);
    }

    /**
     * Adds a bew support ticket into the database.
     * @param email of the sender.
     * @param head of the message.
     * @param body contents of the message.
     * @return a response if the actions was successful or not.
     */
    @POST
    @Path("/support")
    @Consumes("application/json")
    public Response addSupport(@HeaderParam("email") String email,
                               @HeaderParam("head") String head,
                               @HeaderParam("body") String body) {
        int id = DatabaseCommunication.getLastSupportID() + 1;
        DatabaseCommunication.addSupport(id, email, head, body);
        return Response.status(Response.Status.OK).build();
    }

    /**
     * Returns a list with all the requests handled of a teacher.
     * @param teacherID of the teacher.
     * @return a list with the id of the requests handled.
     */
    @GET
    @Path("/newRequests")
    @Produces("application/json")
    public List<Integer> getNewAddedRequests(@HeaderParam("teacherID") String teacherID) {
        return DatabaseCommunication.getNewRequests(teacherID);
    }

    /**
     * Deletes the requests which were handled, it is used for the notifications.
     * @param teacherID of the teacher.
     * @return a response if the actions was successful or not.
     */
    @DELETE
    @Path("/deleteRequests")
    public Response deleteNewAddedRequests(@HeaderParam("teacherID") String teacherID) {
        DatabaseCommunication.deleteNewRequests(teacherID);
        return Response.status(Response.Status.ACCEPTED).build();
    }

    /**
     * Changes the name of a user.
     * @param newName which the user wants.
     * @param userID old name of the user.
     * @return a response if the actions was successful or not.
     */
    @PUT
    @Path("/changeName")
    public Response changeName(@HeaderParam("newName") String newName,
                                @HeaderParam("user") int userID) {
        DatabaseCommunication.changeName(newName, userID);
        return Response.status(Response.Status.OK).build();
    }
}
