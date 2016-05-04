package edu.rosehulman.rafinder.loader;

import com.firebase.client.Firebase;

import java.util.List;

import edu.rosehulman.rafinder.model.DutyRoster;
import edu.rosehulman.rafinder.model.person.Employee;

public abstract class Loader {

    public interface LoaderListener {
        void onDutyRosterLoadingComplete(boolean isEdit);

        DutyRoster getDutyRoster();
        List<Employee> getMyRAs();
        void onEmergencyContactsLoadingComplete();

        void onEmployeeLoadingComplete();

        void onHallRosterLoadingComplete();
    }

    protected abstract void loadData(Firebase firebase);
}
