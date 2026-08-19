/**
 * A ride - a roller coaster, a merry-go-round, and so on. A ride only runs
 * when there is someone to serve: it does nothing when its line is empty.
 * Rides also need periodic technical safety checks, so a Ride honours the
 * Inspectable contract alongside everything it inherits from Attraction.
 */
public class Ride extends Attraction implements Inspectable {

    private static final String NOT_YET_INSPECTED = "Not yet inspected";

    private final int minimumHeightCm;
    private boolean closedForInspection;
    private String inspectionStatus;

    /** Full constructor - explicit minimum rider height. */
    public Ride(String id, String name, int visitorsPerCycle, int minimumHeightCm) {
        super(id, name, visitorsPerCycle);
        this.minimumHeightCm = minimumHeightCm;
        this.closedForInspection = false;
        this.inspectionStatus = NOT_YET_INSPECTED;
    }

    // overloaded constructor - most rides in this park have no minimum height requirement
    public Ride(String id, String name, int visitorsPerCycle) {
        this(id, name, visitorsPerCycle, 0);
    }

    public int getMinimumHeightCm() {
        return minimumHeightCm;
    }

    /**
     * A ride's rule for whether a cycle may run: it needs an operator, it
     * must not currently be closed for inspection, and someone has to
     * actually be waiting - unlike a show, an empty ride does nothing.
     */
    @Override
    public void runCycle() {
        if (getOperator() == null) {
            System.out.println(getName() + " cannot run - it has no operator assigned.");
            return;
        }
        if (closedForInspection) {
            System.out.println(getName() + " cannot run - it is currently closed for inspection.");
            return;
        }
        if (getWaitingCount() == 0) {
            System.out.println(getName() + " cannot run - no one is waiting.");
            return;
        }
        serveCycle();
    }

    @Override
    public void closeForInspection() {
        closedForInspection = true;
        System.out.println(getName() + " is now closed for inspection.");
    }

    @Override
    public void reopenAfterInspection() {
        closedForInspection = false;
        System.out.println(getName() + " has reopened after inspection.");
    }

    @Override
    public void recordInspectionOutcome(String outcome) {
        if (outcome == null || outcome.trim().length() == 0) {
            throw new IllegalArgumentException("An inspection outcome cannot be empty.");
        }
        this.inspectionStatus = outcome;
        System.out.println(getName() + "'s inspection outcome recorded: " + outcome);
    }

    @Override
    public boolean isClosed() {
        return closedForInspection;
    }

    @Override
    public String getInspectionStatus() {
        return inspectionStatus;
    }

    @Override
    public String toString() {
        return super.toString() + ", Type: Ride, Minimum height: " + minimumHeightCm + "cm"
                + ", Closed for inspection: " + closedForInspection + ", Inspection status: " + inspectionStatus;
    }
}
