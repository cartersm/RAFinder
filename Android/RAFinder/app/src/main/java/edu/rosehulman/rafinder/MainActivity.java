package edu.rosehulman.rafinder;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.Firebase;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.rosehulman.rafinder.controller.DutyRosterFragment;
import edu.rosehulman.rafinder.controller.EmergencyContactsFragment;
import edu.rosehulman.rafinder.controller.HallRosterFragment;
import edu.rosehulman.rafinder.controller.HomeFragment;
import edu.rosehulman.rafinder.controller.HomeHallRAsFragment;
import edu.rosehulman.rafinder.controller.HomeRAFragment;
import edu.rosehulman.rafinder.controller.HomeSAsFragment;
import edu.rosehulman.rafinder.controller.LoadingFragment;
import edu.rosehulman.rafinder.controller.LoginActivity;
import edu.rosehulman.rafinder.controller.NavigationDrawerFragment;
import edu.rosehulman.rafinder.controller.ProfileFragment;
import edu.rosehulman.rafinder.controller.SearchFragment;
import edu.rosehulman.rafinder.loader.DutyRosterLoader;
import edu.rosehulman.rafinder.loader.EmergencyContactsLoader;
import edu.rosehulman.rafinder.loader.EmployeeLoader;
import edu.rosehulman.rafinder.loader.HallLoader;
import edu.rosehulman.rafinder.loader.Loader;
import edu.rosehulman.rafinder.model.DutyRoster;
import edu.rosehulman.rafinder.model.DutyRosterItem;
import edu.rosehulman.rafinder.model.person.EmergencyContact;
import edu.rosehulman.rafinder.model.person.Employee;
import edu.rosehulman.rafinder.model.reshall.ResHall;

/**
 * The container activity for the entire app.
 */
// FIXME: Android can't yet use Java 8 Stream or Lambda
public class MainActivity extends Activity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        HomeFragment.HomeListener,
        EmergencyContactsFragment.EmergencyContactsListener,
        HallRosterFragment.HallRosterListener,
        ProfileFragment.StudentProfileListener,
        HomeHallRAsFragment.HomeMyHallRAsListener,
        HomeRAFragment.HomeMyRAListener,
        HomeSAsFragment.HomeMySAListener,
        Loader.LoaderListener,
        SearchFragment.SearchFragmentListener,
        DutyRosterFragment.DutyRosterListener {

    private static final int LOADING = -1;
    private static final int SEARCH = 0;
    private static final int HOME = 1;
    private static final int MY_RA = 2;
    private static final int EMERGENCY_CONTACTS = 3;
    private static final int DUTY_ROSTER = 4;
    private static final int HALL_ROSTER_OR_RESIDENT_LOGOUT = 5;
    private static final int EMPLOYEE_LOGOUT = 6;

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private String mEmail;
    private String mRaEmail;
    private int mFloor;
    private String mHallName = "";

    private EmployeeLoader mEmployeeLoader;
    private DutyRosterLoader mDutyRosterLoader;
    private HallLoader mHallLoader;
    private EmergencyContactsLoader mEmergencyContactsLoader;

    private DutyRosterItem mDutyRosterItem;

    private UserType mUserType = UserType.RESIDENT;

    private List<Employee> mAllRAs;
    private List<Employee> mAllSAs;
    private List<Employee> mAllGAs;
    private List<Employee> mAllAdmins;
    private List<EmergencyContact> mEmergencyContacts;
    private Employee mSelectedEmployee;
    private Employee mUser;
    private Employee myRA;
    private LocalDate mDate;
    private DutyRoster mDutyRoster;
    private List<ResHall> mHalls;

    /**
     * Borrowed from {@link PhoneNumberUtils#normalizeNumber(String)}, for use on devices below API21
     */
    private static String normalizeNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        int len = phoneNumber.length();
        for (int i = 0; i < len; i++) {
            char c = phoneNumber.charAt(i);
            // Character.digit() supports ASCII and Unicode digits (fullwidth, Arabic-Indic, etc.)
            int digit = Character.digit(c, 10);
            if (digit != -1) {
                sb.append(digit);
            } else if (sb.length() == 0 && c == '+') {
                sb.append(c);
            } else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                return normalizeNumber(PhoneNumberUtils.convertKeypadLettersToDigits(phoneNumber));
            }
        }
        return sb.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);

        mEmployeeLoader = new EmployeeLoader(ConfigKeys.FIREBASE_ROOT_URL, this);

        mRaEmail = getIntent().getStringExtra(ConfigKeys.KEY_RA_EMAIL);
        mUserType = UserType.valueOf(getIntent().getStringExtra(ConfigKeys.KEY_USER_TYPE));
        mEmail = getIntent().getStringExtra(ConfigKeys.KEY_USER_EMAIL);

        Log.d(ConfigKeys.LOG_TAG, "Main got UserType <" + mUserType + "> and raEmail <" + mRaEmail + ">");
        mTitle = getTitle();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.updateDrawerList(mUserType);
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.global, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            refreshFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment;

        switch (position) {
        case LOADING:
            fragment = LoadingFragment.newInstance();
            break;
        case SEARCH:
            fragment = SearchFragment.newInstance();
            break;
        case HOME:
            fragment = HomeFragment.newInstance();
            break;
        case MY_RA:
            switchToProfile(myRA);
            return;
        case EMERGENCY_CONTACTS:
            fragment = EmergencyContactsFragment.newInstance();
            break;
        case DUTY_ROSTER:
            fragment = DutyRosterFragment.newInstance(mDate);
            break;
        case HALL_ROSTER_OR_RESIDENT_LOGOUT:
            if (mUserType.equals(UserType.RESIDENT)) {
                logout();
                return;
            } else {
                fragment = HallRosterFragment.newInstance();
                break;
            }
        case EMPLOYEE_LOGOUT:
            logout();
            return;
        default:
            fragment = HomeFragment.newInstance();
        }

        String tag = fragment.getClass().toString();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, tag)
                .commit();
    }

    private void logout() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        new Firebase(ConfigKeys.FIREBASE_ROOT_URL).unauth();

        // clear persistent data
        SharedPreferences.Editor editor = getSharedPreferences(ConfigKeys.KEY_SHARED_PREFS, MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        startActivity(loginIntent);
        finish();
    }

    private void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        //noinspection deprecation
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    private void refreshFragment() {
        FragmentManager manager = getFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.content_frame);
        manager.beginTransaction()
                .detach(fragment)
                .attach(fragment)
                .commit();
    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        phoneNumber = MainActivity.normalizeNumber(phoneNumber);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void sendEmail(String email) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public List<EmergencyContact> getEmergencyContacts() {
        return mEmergencyContacts;
    }

    @Override
    public void switchToProfile(Employee employee) {
        mSelectedEmployee = employee;
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = ProfileFragment.newInstance();
        String tag = fragment.toString();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, tag)
                .commit();
    }

    @Override
    public List<Employee> getMySAs() {
        List<Employee> mySAs = new ArrayList<>();
        for (Employee sa : mAllSAs) {
            if (sa.getHall().equals(mHallName)) {
                mySAs.add(sa);
            }
        }
        return mySAs;
    }

    @Override
    public Employee getSelectedEmployee() {
        return mSelectedEmployee;
    }

    @Override
    public void sendFeedback(String name, String email, String body) {
        Log.d(ConfigKeys.LOG_TAG, "Preparing to send feedback email...");
        String subject = getString(R.string.profile_feedback_subject_format, name);
        String ccList = ",cartersm@rose-hulman.edu";
        new SendEmailAsyncTask().execute(subject, body, email + ccList);
    }

    public UserType getUserType() {
        return mUserType;
    }

    @Override
    public DutyRoster getDutyRoster() {
        return mDutyRoster;
    }

    @Override
    public List<Employee> getMyRAs() {
        List<Employee> myRAs = new ArrayList<>();
        for (Employee ra : mAllRAs) {
            if (ra.getHall().equals(mHallName)
                && ra.getFloor() == mFloor) {
                myRAs.add(ra);
            }
        }
        return myRAs;
    }

    @Override
    public List<Employee> getMyHallRAs() {
        List<Employee> hallRAs = new ArrayList<>();
        for (Employee ra : mAllRAs) {
            if (ra.getHall().equals(mHallName) && !ra.equals(myRA)) {
                hallRAs.add(ra);
            }
        }
        return hallRAs;
    }

    private Employee getEmployee(String email) {
        for (List<Employee> employees : Arrays.asList(mAllRAs, mAllSAs, mAllGAs, mAllAdmins)) {
            for (Employee e : employees) {
                if (e.getEmail().equals(email)) {
                    return e;
                }
            }
        }
        return null;
    }

    private Employee getRA(String email) {
        for (Employee ra : mAllRAs) {
            if (ra.getEmail().equals(email)) {
                return ra;
            }
        }
        for (Employee admin : mAllAdmins) {
            if (admin.getEmail().equals(email)) {
                return admin;
            }
        }
        return null;
    }

    @Override
    public List<ResHall> getHalls() {
        return mHalls;
    }

    public Employee getUser() {
        return mUser;
    }

    private void setAllEmployees() {
        mAllRAs = mEmployeeLoader.getRAs();
        mAllSAs = mEmployeeLoader.getSAs();
        mAllGAs = mEmployeeLoader.getGAs();
        mAllAdmins = mEmployeeLoader.getAdmins();
    }

    @Override
    public void onDutyRosterLoadingComplete(boolean isEdit) {
        mDutyRoster = mDutyRosterLoader.getDutyRoster();
        mDate = mDutyRosterLoader.getDate();
        if (!isEdit) {
            mHallLoader = new HallLoader(this);
        } else {
            onNavigationDrawerItemSelected(DUTY_ROSTER);
        }
    }

    @Override
    public void onEmergencyContactsLoadingComplete() {
        mEmergencyContacts = mEmergencyContactsLoader.getContactList();
        // We've finished loading; go to the Home page
        onNavigationDrawerItemSelected(HOME);
    }

    // The onLoadingComplete family of methods are arranged in a daisy-chain of loaders, as Firebase's async,
    // callback-based (i.e. non-blocking) nature requires us to frontload the data to prevent NullPointer exceptions
    @Override
    public void onEmployeeLoadingComplete() {
        setAllEmployees();
        myRA = getRA(mRaEmail);
        mUser = getEmployee(mEmail);
        mHallName = myRA.getHall();
        mFloor = myRA.getFloor();
        mDutyRosterLoader = new DutyRosterLoader(this, mAllRAs, false);
    }

    @Override
    public void onHallRosterLoadingComplete() {
        mHalls = mHallLoader.getHalls();
        mEmergencyContactsLoader = new EmergencyContactsLoader(this);
    }

    private Employee getRAForName(String name) {
        for (Employee ra : mAllRAs) {
            if (ra.getName().equalsIgnoreCase(name)) {
                return ra;
            }
        }
        return null;
    }

    public List<Employee> getEmployeesForName(String name) {
        List<Employee> employees = new ArrayList<>();
        for (List<Employee> emps : Arrays.asList(mAllRAs, mAllSAs, mAllGAs, mAllAdmins)) {
            for (Employee employee : emps) {
                if (employee.getName().contains(name)) {
                    employees.add(employee);
                }
            }
        }
        return employees;
    }

    /**
     * Borrowed from <a href=http://stackoverflow.com/questions/2020088/sending-email-in-android-using-javamail-api-without-using-the-default-built-in-a/2033124#2033124>This
     * StackOverflowArticle</a>
     * For sending email directly from the app.
     */
    private class SendEmailAsyncTask extends AsyncTask<String, Void, Boolean> {
        public static final int SUBJECT = 0;
        public static final int BODY = 1;
        public static final int TO = 2;
        final GmailSender gmailSender;

        private SendEmailAsyncTask() {
            gmailSender = new GmailSender(ConfigKeys.FEEDBACK_EMAIL, ConfigKeys.FEEDBACK_PASSWORD);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if (BuildConfig.DEBUG) {
                Log.v(SendEmailAsyncTask.class.getName(), "doInBackground()");
            }
            try {
                gmailSender.sendMail(params[SUBJECT], params[BODY], ConfigKeys.FEEDBACK_EMAIL, params[TO]);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }
}
