package edu.rosehulman.rafinder.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.rosehulman.rafinder.MainActivity;
import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.UserType;
import edu.rosehulman.rafinder.model.person.Employee;

public class EmployeeList {
    private final MainActivity context;

    private List<EmployeeSubList> employees;

    public EmployeeList(MainActivity context) {
        this.context = context;
        this.employees = new ArrayList<>();

        // FIXME: when we allow selection by hall, make sure "My RA" is not listed
        // FIXME: ditto, generify MainActivity's methods here
        // FIXME: ditto,
        UserType userType = context.getUserType();
        if (userType.equals(UserType.RESIDENT_ASSISTANT) || userType.equals(UserType.SOPHOMORE_ADVISOR)) {
            this.employees.add(new EmployeeSubList(
                    context.getString(R.string.my_profile),
                    Arrays.asList(context.getUser())));
        } else {
            // FIXME: consider the implications of this for admins/GAs.
            // Theoretically, once ditto is in place we won't have to worry because we're getting by hall.
            List<Employee> myRAs = context.getMyRAs();
            String key = myRAs.size() > 1 ? context.getString(R.string.my_ras) : context.getString(R.string.my_ra);
            this.employees.add(new EmployeeSubList(key, myRAs));
        }

        this.employees.add(new EmployeeSubList(context.getString(R.string.hall_ras), context.getMyHallRAs()));

        if (isFreshmanHall(context.getHallName())) {
            this.employees.add(new EmployeeSubList(context.getString(R.string.hall_sas), context.getMySAs()));
        }
    }

    private boolean isFreshmanHall(String hallName) {
        return hallName.equalsIgnoreCase("Mees") ||
               hallName.equalsIgnoreCase("Blumberg") ||
               hallName.equalsIgnoreCase("Scharpenberg") ||
               hallName.equalsIgnoreCase("Speed") ||
               hallName.equalsIgnoreCase("Deming") ||
               hallName.equalsIgnoreCase("BSB");
    }

    public List<EmployeeSubList> getEmployees() {
        return employees;
    }

    public class EmployeeSubList {
        private String key;
        private List<Employee> employees;

        public EmployeeSubList(String key, List<Employee> employees) {
            this.key = key;
            this.employees = employees;
        }

        public String getKey() {
            return key;
        }

        public List<Employee> getEmployees() {
            return employees;
        }

        public int size() {
            return this.employees.size();
        }

        public Employee get(int i) {
            return this.employees.get(i);
        }
    }
}