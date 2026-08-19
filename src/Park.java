import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manages the whole collection of the park's attractions - finds any one
 * of them directly by its id, reports how busy each has been, and works
 * out how many distinct visitors the park has admitted across the whole
 * day, counting a repeat visitor only once.
 */
public class Park {

    // A HashMap associates each attraction directly with its id and looks
    // one up in a single step, rather than searching through every
    // attraction each time - exactly what "retrieve directly by id" needs.
    private final Map<String, Attraction> attractions;

    // Part 8 - a single park-wide tally, updated by attractions themselves
    // as they run cycles, possibly concurrently. See recordVisitorsServed().
    private int totalVisitorsServedConcurrently;

    public Park() {
        this.attractions = new HashMap<String, Attraction>();
        this.totalVisitorsServedConcurrently = 0;
    }

    public void registerAttraction(Attraction attraction) {
        if (attraction == null) {
            throw new IllegalArgumentException("Cannot register a null attraction.");
        }
        if (attractions.containsKey(attraction.getId())) {
            System.out.println("Warning: an attraction with id " + attraction.getId()
                    + " is already registered - ignoring.");
            return;
        }
        attractions.put(attraction.getId(), attraction);
        attraction.setPark(this);
        System.out.println("Registered " + attraction.getName() + " (id " + attraction.getId() + ") with the park.");
    }

    /** Looks an attraction up directly by its id, in one step. */
    public Attraction findAttraction(String id) {
        Attraction found = attractions.get(id);
        if (found == null) {
            System.out.println("Warning: no attraction registered with id " + id + ".");
        } else {
            System.out.println("Found attraction: " + found.getName() + " (id " + id + ").");
        }
        return found;
    }

    /** Reports, for every registered attraction, how many "seats" it has served so far. */
    public void reportSeatsServed() {
        System.out.println("=== Seats served per attraction ===");
        if (attractions.isEmpty()) {
            System.out.println("  (no attractions registered)");
            return;
        }
        for (Attraction attraction : attractions.values()) {
            System.out.println("  " + attraction.getName() + " (id " + attraction.getId() + "): "
                    + attraction.getVisitCount() + " seat(s) served.");
        }
    }

    /**
     * Counts how many distinct visitors the park has admitted across every
     * attraction, counting a visitor who attended more than once only
     * once. A HashSet is the right tool here - unlike an attraction's own
     * history (an ArrayList, which must allow the same visitor to appear
     * more than once), this collection needs to throw duplicates away, and
     * it relies on Visitor.equals()/hashCode() to recognise one.
     */
    public int countDistinctVisitors() {
        Set<Visitor> distinct = new HashSet<Visitor>();
        for (Attraction attraction : attractions.values()) {
            distinct.addAll(attraction.getHistory());
        }
        System.out.println("The park has admitted " + distinct.size() + " distinct visitor(s) across the day.");
        return distinct.size();
    }

    /** A defensive copy of every registered attraction - used by ParkIO when saving. */
    public Collection<Attraction> getAllAttractions() {
        return new ArrayList<Attraction>(attractions.values());
    }

    // ---------------------------------------------------------------
    // Part 8 - running attractions concurrently
    // ---------------------------------------------------------------
    // Several attractions can call this at the same time from different
    // threads. Without synchronisation, two threads could both read the
    // current total, add their own count, and write back - and one
    // update would be lost. Marking the method synchronized means only
    // one thread can be inside it at a time, so the read-modify-write is
    // never split between two threads and no update goes missing.

    synchronized void recordVisitorsServed(int count) {
        totalVisitorsServedConcurrently += count;
    }

    public synchronized int getTotalVisitorsServedConcurrently() {
        return totalVisitorsServedConcurrently;
    }
}
