/**
 * A toilet block - a facility, not an attraction. It has no waiting line,
 * no operator, and no visitor history; the only thing it shares with a
 * ride is that it must be inspected from time to time, which is exactly
 * what the Inspectable contract is for. Toilet and Ride share no common
 * ancestor - Inspectable is the only thing connecting them.
 */
public class Toilet implements Inspectable {

    private static final String NOT_YET_INSPECTED = "Not yet inspected";

    private final String id;
    private final String location;
    private boolean closedForInspection;
    private String inspectionStatus;

    public Toilet(String id, String location) {
        if (id == null || id.trim().length() == 0) {
            throw new IllegalArgumentException("Toilet id cannot be empty.");
        }
        if (location == null || location.trim().length() == 0) {
            throw new IllegalArgumentException("Toilet location cannot be empty.");
        }
        this.id = id;
        this.location = location;
        this.closedForInspection = false;
        this.inspectionStatus = NOT_YET_INSPECTED;
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public void closeForInspection() {
        closedForInspection = true;
        System.out.println("Toilet " + id + " (" + location + ") is now closed for inspection.");
    }

    @Override
    public void reopenAfterInspection() {
        closedForInspection = false;
        System.out.println("Toilet " + id + " (" + location + ") has reopened after inspection.");
    }

    @Override
    public void recordInspectionOutcome(String outcome) {
        if (outcome == null || outcome.trim().length() == 0) {
            throw new IllegalArgumentException("An inspection outcome cannot be empty.");
        }
        this.inspectionStatus = outcome;
        System.out.println("Toilet " + id + "'s inspection outcome recorded: " + outcome);
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
        return "Toilet id: " + id + ", Location: " + location
                + ", Closed for inspection: " + closedForInspection + ", Inspection status: " + inspectionStatus;
    }
}
