/**
 * A member of park staff - operates attractions and carries out
 * inspections. Kept as a single concrete class (the park doesn't need
 * different staff subtypes for this system) with just enough information
 * to identify who they are and which department they belong to.
 */
public class Staff {

    private static final String DEFAULT_DEPARTMENT = "General";

    private final String id;
    private final String name;
    private final int age;
    private final String department;

    /** Full constructor - explicit department. */
    public Staff(String id, String name, int age, String department) {
        if (id == null || id.length() == 0 || !isNumeric(id)) {
            throw new IllegalArgumentException("Staff id must contain digits only.");
        }
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("Staff name cannot be empty.");
        }
        if (age <= 0) {
            throw new IllegalArgumentException("Staff age must be a positive number.");
        }
        if (department == null || department.trim().length() == 0) {
            throw new IllegalArgumentException("Staff department cannot be empty.");
        }
        this.id = id;
        this.name = name;
        this.age = age;
        this.department = department;
    }

    // overloaded constructor - defaults to the General department when none is given
    public Staff(String id, String name, int age) {
        this(id, name, age, DEFAULT_DEPARTMENT);
    }

    private static boolean isNumeric(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getDepartment() {
        return department;
    }

    /**
     * Carries out an inspection of anything that honours the Inspectable
     * contract - a Ride, a Toilet, or anything else added later, without
     * this method ever needing to know which. Closes the item first (it
     * cannot be used mid-inspection), records the outcome, then reopens it.
     *
     * @param item   the ride, toilet, or other inspectable thing being checked
     * @param passed whether the item passed the inspection
     * @param notes  extra detail to record alongside the pass/fail outcome
     */
    public void performInspection(Inspectable item, boolean passed, String notes) {
        System.out.println(name + " begins an inspection of " + item + "...");
        item.closeForInspection();
        System.out.println("  " + item.getClass().getSimpleName()
                + " is now closed for the duration of the inspection (isClosed = " + item.isClosed() + ").");

        String outcome = (passed ? "PASSED" : "FAILED") + (notes == null || notes.trim().length() == 0 ? "" : " - " + notes);
        item.recordInspectionOutcome(outcome);
        item.reopenAfterInspection();

        System.out.println(name + " completes the inspection. Result: " + item.getInspectionStatus()
                + " (isClosed = " + item.isClosed() + ")");
    }

    @Override
    public String toString() {
        return "Id: " + id + ", Name: " + name + ", Age: " + age + ", Department: " + department;
    }

    // same id = same staff member, used so an attraction always refers to one real person
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Staff)) {
            return false;
        }
        Staff other = (Staff) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
