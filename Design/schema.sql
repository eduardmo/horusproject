Room(room_number, building, room_nr, trivial_name, area, capacity_timetable, capacity_lecture, capacity_work, capacity_exam, first_row_handicapped, handicapped, furniture, gps_coordinates, floor_nr, PK (room_number));






Request(id, oldroom, olddate, newdate, teacherid, teachername, numberofstudents, requesttype, notes, coursetype, faculty, status, newroom, comms, PK(id));






Activities(activity_id, moduleid, startdate, enddate, allday, location, activitytext, activitydescription, department, color, PK(activity_id), FK(moduleid) REFERENCES Modules(moduleid));






Modules(module_id, moduleid, description, hostkey, PK(module_id));






Student_sets(student_set_id, student_set, activity_id, PK(student_set_id), FK (activity_id) REFERENCES Activities(activity_id));






Users(user_id, email, password, staff_name, is_timetabler, PK(user_id));






Favourites(id, default_faculty, PK(id), FK(id) REFERENCES Users (user_id));






Staff_activities(staff_id, staff_name, activity_id, PK (staff_id), FK (activity_id) REFERENCES Activities(activity_id));






Cookies(user_id, cookie, PK(user_id));