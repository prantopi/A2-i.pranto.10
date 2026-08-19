import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Everything a ride and a show have in common: an id, a name, a waiting
 * line, a record of who they've served, an operator, how many visitors
 * they admit per cycle, and how many cycles they've run. This lives here,
 * written once, so Ride and Show only ever add what actually differs
 * between them.
 *
 * Abstract, so nobody can create a plain "attraction" that is neither a
 * ride nor a show - every object using this class has to be a real,
 * specific kind, and every subclass is forced to supply its own rule for
 * runCycle(), since that's the one thing that genuinely differs.
 */
public abstract class Attraction {

    private final String id;
    private final String name;
    private final int visitorsPerCycle;
    private final Queue<Visitor> waitingLine;
    private final ArrayList<Visitor> history;
    private Staff operator;
    private int cyclesRun;

    // back-reference to whichever Park this attraction is registered with,
    // set by Park.registerAttraction() - lets a cycle report into the
    // park-wide concurrent total (Part 8) without the park having to poll
    // every attraction itself. Package-private: only Park should set it.
    private Park park;

    protected Attraction(String id, String name, int visitorsPerCycle) {
        if (id == null || id.trim().length() == 0) {
            throw new IllegalArgumentException("Attraction id cannot be empty.");
        }
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("Attraction name cannot be empty.");
        }
        if (visitorsPerCycle <= 0) {
            throw new IllegalArgumentException("An attraction must serve at least one visitor per cycle.");
        }
        this.id = id;
        this.name = name;
        this.visitorsPerCycle = visitorsPerCycle;
        this.waitingLine = new LinkedList<Visitor>();
        this.history = new ArrayList<Visitor>();
        this.operator = null;
        this.cyclesRun = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getVisitorsPerCycle() {
        return visitorsPerCycle;
    }

    public int getCyclesRun() {
        return cyclesRun;
    }

    public Staff getOperator() {
        return operator;
    }

    void setPark(Park park) {
        this.park = park;
    }

    // ---------------------------------------------------------------
    // Part 2 - operator
    // ---------------------------------------------------------------

    public void assignOperator(Staff staff) {
        if (staff == null) {
            throw new IllegalArgumentException("Cannot assign a null operator.");
        }
        this.operator = staff;
        System.out.println(staff.getName() + " is now operating " + name + " (id " + id + ").");
    }

    public void removeOperator() {
        if (operator == null) {
            System.out.println(name + " (id " + id + ") has no operator to remove.");
            return;
        }
        System.out.println(operator.getName() + " has stopped operating " + name + " (id " + id + ").");
        operator = null;
    }

    // ---------------------------------------------------------------
    // Part 3 - the waiting line
    // ---------------------------------------------------------------
    // A Queue (backed by a LinkedList) is the natural fit here: visitors
    // must be served in exactly the order they arrived, there's no fixed
    // cap on how many can wait, and a queue's whole job is FIFO
    // add-at-the-back / remove-from-the-front - unlike a List, which would
    // let something be inserted or removed from the middle, that isn't
    // needed and isn't what "waiting in line" means.

    public void joinWaitingLine(Visitor visitor) {
        if (visitor == null) {
            throw new IllegalArgumentException("A null visitor cannot join the line.");
        }
        waitingLine.add(visitor);
        System.out.println(visitor.getName() + " joins the waiting line for " + name
                + ". (" + waitingLine.size() + " now waiting)");
    }

    private Visitor pollNextVisitor() {
        return waitingLine.poll();
    }

    /** Removes and returns whoever is next in line, on their own, outside of a full cycle. */
    public Visitor admitNextVisitor() {
        Visitor next = pollNextVisitor();
        if (next == null) {
            System.out.println("No one is waiting for " + name + " right now.");
            return null;
        }
        System.out.println(next.getName() + " is admitted next at " + name + ".");
        return next;
    }

    public void displayWaitingLine() {
        System.out.println("Waiting line for " + name + " (" + waitingLine.size() + " waiting):");
        if (waitingLine.isEmpty()) {
            System.out.println("  (empty)");
            return;
        }
        int position = 1;
        for (Visitor visitor : waitingLine) {
            System.out.println("  " + position + ". " + visitor);
            position++;
        }
    }

    public int getWaitingCount() {
        return waitingLine.size();
    }

    /** A defensive copy of who is currently waiting, in order - used by Part 7's save. */
    public ArrayList<Visitor> getWaitingSnapshot() {
        return new ArrayList<Visitor>(waitingLine);
    }

    // ---------------------------------------------------------------
    // Part 4 - the visit history
    // ---------------------------------------------------------------
    // An ArrayList is the right fit here: the history only ever grows
    // (visitors are appended as they're served, never re-ordered in
    // storage), has no fixed size, and needs to support duplicates - the
    // same visitor may legitimately appear more than once, having ridden
    // several times. A Set would silently drop the repeat visits this
    // history is supposed to count.

    public void recordVisit(Visitor visitor) {
        if (visitor == null) {
            throw new IllegalArgumentException("Cannot record a null visitor.");
        }
        history.add(visitor);
        System.out.println(name + " recorded a visit by " + visitor.getName() + ".");
    }

    /** @return true if this attraction has served this visitor before (relies on Visitor.equals()). */
    public boolean hasBeenServed(Visitor visitor) {
        boolean found = history.contains(visitor);
        String visitorName = visitor == null ? "null" : visitor.getName();
        System.out.println(name + (found ? " has already served " : " has not served ") + visitorName + ".");
        return found;
    }

    public int getVisitCount() {
        return history.size();
    }

    /** A defensive copy of the full history, in the order visitors were served - used by Parts 6 and 7. */
    public ArrayList<Visitor> getHistory() {
        return new ArrayList<Visitor>(history);
    }

    public void displayHistory() {
        System.out.println("Visit history for " + name + " (" + history.size() + " served):");
        printVisitors(history);
    }

    /** Displays the history ordered by age, using Visitor's natural (Comparable) ordering. */
    public void displayHistoryByAge() {
        ArrayList<Visitor> byAge = new ArrayList<Visitor>(history);
        Collections.sort(byAge);
        System.out.println("Visit history for " + name + ", ordered by age:");
        printVisitors(byAge);
    }

    /** Displays the history ordered by name, then by age to break ties - via VisitorNameComparator. */
    public void displayHistoryByNameThenAge() {
        ArrayList<Visitor> byName = new ArrayList<Visitor>(history);
        Collections.sort(byName, new VisitorNameComparator());
        System.out.println("Visit history for " + name + ", ordered by name (then age):");
        printVisitors(byName);
    }

    private void printVisitors(ArrayList<Visitor> visitors) {
        if (visitors.isEmpty()) {
            System.out.println("  (no visitors served yet)");
            return;
        }
        for (Visitor visitor : visitors) {
            System.out.println("  " + visitor);
        }
    }

    // ---------------------------------------------------------------
    // Part 5 - operating an attraction
    // ---------------------------------------------------------------

    /**
     * Runs a single cycle. What "may run" means is entirely up to the
     * subclass (a ride needs someone waiting and to be open; a show
     * doesn't) - that's the one thing that genuinely differs between kinds
     * of attraction, so it's the one thing left abstract. Once a subclass
     * has decided a cycle may go ahead, it calls serveCycle() below to
     * actually do the (shared) work of moving visitors from the line into
     * the history.
     */
    public abstract void runCycle();

    /**
     * The shared mechanics of a cycle: take up to visitorsPerCycle
     * visitors from the front of the line and move each into the history -
     * this is where the waiting line (Part 3) and the visit history
     * (Part 4) work together. Also reports the visitors served into the
     * park-wide concurrent total (Part 8), if this attraction has been
     * registered with a Park.
     */
    protected void serveCycle() {
        int count = Math.min(visitorsPerCycle, waitingLine.size());
        for (int i = 0; i < count; i++) {
            Visitor visitor = pollNextVisitor();
            System.out.println(visitor.getName() + " leaves the line and enters " + name + ".");
            recordVisit(visitor);
        }
        cyclesRun++;
        System.out.println(name + " completed cycle #" + cyclesRun + ", serving " + count
                + " visitor(s). (" + waitingLine.size() + " still waiting)");

        if (park != null && count > 0) {
            park.recordVisitorsServed(count);
        }
    }

    /**
     * Used only by ParkIO when restoring a saved cycle count on load - not
     * part of the public "run a cycle" behaviour, so it stays package-private.
     */
    void restoreCycleCount(int cycles) {
        this.cyclesRun = cycles;
    }

    @Override
    public String toString() {
        return "Id: " + id + ", Name: " + name + ", Visitors/cycle: " + visitorsPerCycle
                + ", Cycles run: " + cyclesRun + ", Waiting: " + waitingLine.size()
                + ", Served: " + history.size()
                + ", Operator: " + (operator == null ? "none" : operator.getName());
    }
}
