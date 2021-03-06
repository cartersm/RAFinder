package edu.rosehulman.rafinder.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.UserType;
import edu.rosehulman.rafinder.controller.HomeFragment;
import edu.rosehulman.rafinder.model.person.Employee;

/**
 * Models a list of lists of employees, for use with {@link edu.rosehulman.rafinder.adapter.EmployeeListAdapter}.
 */
public class EmployeeList {

    private final List<EmployeeSubList> employees;

    public EmployeeList(HomeFragment.HomeListener context, String hallName) {
        this.employees = new ArrayList<>();

        UserType userType = context.getUserType();
        if (!userType.equals(UserType.RESIDENT)) {
            this.employees.add(new EmployeeSubList(
                    context.getString(R.string.my_profile),
                    Collections.singletonList(context.getUser())));
        } else if (hallName.equals(context.getHallName())) {
            List<Employee> myRAs = context.getMyRAs();
            String key = myRAs.size() > 1 ? context.getString(R.string.my_ras) : context.getString(R.string.my_ra);
            this.employees.add(new EmployeeSubList(key, myRAs));
        }

        this.employees.add(new EmployeeSubList(context.getString(R.string.hall_ras), context.getRAsForHall(hallName)));

        if (isFreshmanHall(hallName)) {
            this.employees.add(new EmployeeSubList(context.getString(R.string.hall_sas), context.getSAsForHall(hallName)));
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

    /**
     * Models a single list of employees within the larger list.
     */
    public class EmployeeSubList {
        private final String key;
        private final List<Employee> employees;

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
