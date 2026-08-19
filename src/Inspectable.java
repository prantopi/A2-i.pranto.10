/**
 * A capability shared by otherwise-unrelated things in the park that need
 * periodic safety checks. A Ride (an attraction) and a Toilet (just a
 * facility) share no common ancestor, but both need to be closed while
 * they're being looked at, reopened afterwards, and have the outcome of
 * the check recorded - so that behaviour lives here instead of on any one
 * class hierarchy. This is what lets Staff write a single method,
 * performInspection(Inspectable item, ...), that works on a ride or a
 * toilet interchangeably, and on whatever else needs inspecting later
 * (a car park, a first-aid station) without either of them ever knowing
 * about each other.
 *
 * Nothing about running an attraction belongs here - only what any
 * inspectable thing must be able to do, whatever it actually is.
 */
public interface Inspectable {

    /** Closes the item so it cannot be used while an inspection is underway. */
    void closeForInspection();

    /** Reopens the item once its inspection has finished. */
    void reopenAfterInspection();

    /**
     * Records the outcome of an inspection (e.g. "PASSED" or a failure
     * reason). Does not affect whether the item is currently closed -
     * that's controlled separately by closeForInspection()/reopenAfterInspection().
     */
    void recordInspectionOutcome(String outcome);

    /** @return true while the item is closed for an inspection. */
    boolean isClosed();

    /** @return the outcome recorded by the most recent inspection. */
    String getInspectionStatus();
}
