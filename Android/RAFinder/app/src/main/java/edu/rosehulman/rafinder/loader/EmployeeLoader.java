package edu.rosehulman.rafinder.loader;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.rafinder.ConfigKeys;
import edu.rosehulman.rafinder.model.person.Administrator;
import edu.rosehulman.rafinder.model.person.Employee;
import edu.rosehulman.rafinder.model.person.GraduateAssistant;
import edu.rosehulman.rafinder.model.person.ResidentAssistant;
import edu.rosehulman.rafinder.model.person.SophomoreAdvisor;

/**
 * A loader for Residence Life Employees.
 */
public class EmployeeLoader extends Loader {
    private final LoaderListener mListener;
    private List<Employee> mAdmins = new ArrayList<>();
    private List<Employee> mRAs = new ArrayList<>();
    private List<Employee> mGAs = new ArrayList<>();
    private List<Employee> mSAs = new ArrayList<>();


    public EmployeeLoader(String url, LoaderListener listener) {
        mListener = listener;
        loadData(new Firebase(url + "/" + ConfigKeys.Employees));
    }

    protected void loadData(Firebase firebase) {
        Log.d(ConfigKeys.LOG_TAG, "Loading Employee data...");
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    switch (child.getKey()) {
                        case ConfigKeys.Administrators:
                            for (DataSnapshot adminDS : child.getChildren()) {
                                Administrator admin = adminDS.getValue(Administrator.class);
                                admin.setUid(adminDS.getKey());
                                admin.setFirebase(adminDS.getRef());
                                mAdmins.add(admin);
                            }
                            break;
                        case ConfigKeys.GraduateAssistants:
                            for (DataSnapshot gaDS : child.getChildren()) {
                                GraduateAssistant ga = gaDS.getValue(GraduateAssistant.class);
                                ga.setUid(gaDS.getKey());
                                ga.setFirebase(gaDS.getRef());
                                mGAs.add(ga);
                            }
                            break;
                        case ConfigKeys.ResidentAssistants:
                            for (DataSnapshot raDS : child.getChildren()) {
                                ResidentAssistant ra = raDS.getValue(ResidentAssistant.class);
                                ra.setUid(raDS.getKey());
                                ra.setFirebase(raDS.getRef());
                                mRAs.add(ra);
                            }
                            break;
                        case ConfigKeys.SophomoreAdvisors:
                            for (DataSnapshot saDS : child.getChildren()) {
                                SophomoreAdvisor sa = saDS.getValue(SophomoreAdvisor.class);
                                sa.setUid(saDS.getKey());
                                sa.setFirebase(saDS.getRef());
                                mSAs.add(sa);
                            }
                            break;
                    }
                }
                Log.d(ConfigKeys.LOG_TAG, "Finished loading Employee data.");
                mListener.onEmployeeLoadingComplete();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // ignored
            }
        });
    }

    public List<Employee> getAdmins() {
        return mAdmins;
    }

    public void setAdmins(List<Employee> admins) {
        mAdmins = admins;
    }

    public List<Employee> getRAs() {
        return mRAs;
    }

    public void setRAs(List<Employee> ras) {
        mRAs = ras;
    }

    public List<Employee> getGAs() {
        return mGAs;
    }

    public void setGas(List<Employee> gas) {
        mGAs = gas;
    }

    public List<Employee> getSAs() {
        return mSAs;
    }

    public void setSAs(List<Employee> sas) {
        mSAs = sas;
    }
}
