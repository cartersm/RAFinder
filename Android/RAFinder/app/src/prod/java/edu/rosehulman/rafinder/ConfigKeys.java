package edu.rosehulman.rafinder;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ConfigKeys {

    public static final String LOG_TAG = "RAFinder";

    public static final String FIREBASE_ROOT_URL = "https://ra-finder.firebaseio.com";

    // Root Firebase keys
    public static final String Employees = "Employees";

    // Employees Firebase keys
    public static final String ResidentAssistants = "Resident Assistants";
    public static final String SophomoreAdvisors = "Sophomore Advisors";
    public static final String GraduateAssistants = "Graduate Assistants";
    public static final String Administrators = "Administrators";

    // Employee Firebase keys
    public static final String employeeName = "name";
    public static final String employeeEmail = "email";
    public static final String employeeFloor = "floor";
    public static final String employeeHall = "hall";
    public static final String employeePhone = "phoneNumber";
    public static final String employeeRoom = "room";
    public static final String employeeStatus = "status";
    public static final String employeeStatusDetail = "statusDetail";
    public static final String employeePicture = "profilePicture";

    // HallRoster and DutyRoster bundle keys
    public static final String DATE = "DATE";
    public static final String HALL = "HALL";
    public static final String FLOOR = "FLOOR";

    public static final String dateFormat = "yyyy-MM-dd";
    public static final DateTimeFormatter formatter = DateTimeFormat.forPattern(dateFormat);

    // Shared prefs keys
    public static final String KEY_SHARED_PREFS = "RA_FINDER_SHARED_PREFERENCES";
    public static final String KEY_USER_TYPE = "KEY_USER_TYPE";
    public static final String KEY_RA_EMAIL = "KEY_RA_EMAIL";
    public static final String KEY_USER_EMAIL = "KEY_USER_EMAIL";
    public static final String FEEDBACK_EMAIL = "rafindernoreply@gmail.com";
    public static final String FEEDBACK_PASSWORD = "Rose-Hulman-RAFinder-201540";
}
