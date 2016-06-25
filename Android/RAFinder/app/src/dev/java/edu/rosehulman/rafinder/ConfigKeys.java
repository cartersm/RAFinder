package edu.rosehulman.rafinder;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ConfigKeys {
    public static final String SUPPORT_EMAIL = "csseadmin@rose-hulman.edu";
    public static final String FEEDBACK_CC = ",rhodeska@rose-hulman.edu,";

    public static final String LOG_TAG = "RAFinder";
    public static final String FIREBASE_ROOT_URL = "https://ra-finder-dev.firebaseio.com";
    public static final Environment ENV = Environment.DEV;
    public static final String REGISTRY_TOKEN = "931baaeed4ddf12032ed98e45edc2f769ba8be1874e2c062f58437e42ae848997a76" +
                                                "f1d4ecfeb10e45a6b5cf422d87f7xrEHABH6J/MMf5QA3sxjZChYEuY90FpsAgFK5MgM" +
                                                "a0vqD4Q/nNzPrzmaoYNGkRZDPTnlXdn0gE206vrqVkltEw==";
    public static final int RC_ROSEFIRE_LOGIN = 127;

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
