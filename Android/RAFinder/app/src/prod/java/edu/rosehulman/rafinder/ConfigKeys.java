package edu.rosehulman.rafinder;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ConfigKeys {
    // FIXME: extract these lists to the DB?
    public static final String SUPPORT_EMAIL = "sean47933@gmail.com";
    public static final String FEEDBACK_CC = ",rhodeska@rose-hulman.edu,";

    public static final String LOG_TAG = "RAFinder";
    public static final String FIREBASE_ROOT_URL = "https://ra-finder.firebaseio.com";
    public static final Environment ENV = Environment.PROD;
    public static final String REGISTRY_TOKEN = "bf2f8ab7c101995e3ae8669d1dd467592dec77d1158673685f9b0ba2efbe6ba5508" +
            "1ba228ff365dfa68f9beeba5fd196adXg/W9RjB0YrQHQHIw3dCOyVQXrXrboe7TEqu/6U8C1U4J9wnGQa0BFW+X3duaugCr2TybsIt" +
            "42XZSjZmkvgQ==";

    public static final int RC_ROSEFIRE_LOGIN = 127;

    // Root Firebase keys
    public static final String EMPLOYEES = "Employees";

    // EMPLOYEES Firebase keys
    public static final String RESIDENT_ASSISTANTS = "Resident Assistants";
    public static final String SOPHOMORE_ADVISORS = "Sophomore Advisors";
    public static final String GRADUATE_ASSISTANTS = "Graduate Assistants";
    public static final String ADMINISTRATORS = "Administrators";

    // Employee Firebase keys
    public static final String EMPLOYEE_NAME = "name";
    public static final String EMPLOYEE_EMAIL = "email";
    public static final String EMPLOYEE_FLOOR = "floor";
    public static final String EMPLOYEE_HALL = "hall";
    public static final String EMPLOYEE_PHONE = "phoneNumber";
    public static final String EMPLOYEE_ROOM = "room";
    public static final String EMPLOYEE_STATUS = "status";
    public static final String EMPLOYEE_STATUS_DETAIL = "statusDetail";
    public static final String EMPLOYEE_PICTURE = "profilePicture";

    // HallRoster and DutyRoster bundle keys
    public static final String DATE = "DATE";
    public static final String HALL = "HALL";
    public static final String FLOOR = "FLOOR";

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern(DATE_FORMAT);

    // Shared prefs keys
    public static final String KEY_SHARED_PREFS = "RA_FINDER_SHARED_PREFERENCES";
    public static final String KEY_USER_TYPE = "KEY_USER_TYPE";
    public static final String KEY_RA_EMAIL = "KEY_RA_EMAIL";
    public static final String KEY_USER_EMAIL = "KEY_USER_EMAIL";
    public static final String FEEDBACK_EMAIL = "rafindernoreply@gmail.com";
    public static final String FEEDBACK_PASSWORD = "Rose-Hulman-RAFinder-201540";
}
