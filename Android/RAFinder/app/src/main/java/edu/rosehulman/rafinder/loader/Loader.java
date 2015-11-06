package edu.rosehulman.rafinder.loader;

import com.firebase.client.Firebase;

import java.util.List;

import edu.rosehulman.rafinder.model.DutyRoster;
import edu.rosehulman.rafinder.model.person.Employee;

public abstract class Loader {

    public interface LoaderListener {
        public void onDutyRosterLoadingComplete(boolean isEdit);

        public DutyRoster getDutyRoster();
        public List<Employee> getMyRAs();
        public void onEmergencyContactsLoadingComplete();

        public void onEmployeeLoadingComplete();

        public void onHallRosterLoadingComplete();
    }

    protected abstract void loadData(Firebase firebase);
}
