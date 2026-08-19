/**
 * A show - a live performance. Unlike a ride, a show runs whether or not
 * anyone is waiting: a performance goes ahead even to an empty house, and
 * that empty performance still counts as a cycle. A show is just
 * performers on a stage, so - unlike a ride - it does not need to be
 * inspected, and does not implement Inspectable.
 */
public class Show extends Attraction {

    private static final String DEFAULT_GENRE = "Variety";

    private final String genre;

    /** Full constructor - explicit genre. */
    public Show(String id, String name, int visitorsPerCycle, String genre) {
        super(id, name, visitorsPerCycle);
        this.genre = genre;
    }

    // overloaded constructor - defaults to a general Variety show
    public Show(String id, String name, int visitorsPerCycle) {
        this(id, name, visitorsPerCycle, DEFAULT_GENRE);
    }

    public String getGenre() {
        return genre;
    }

    /**
     * A show's rule for whether a cycle may run: it needs an operator, and
     * that's all - it goes ahead regardless of how many visitors (if any)
     * are waiting.
     */
    @Override
    public void runCycle() {
        if (getOperator() == null) {
            System.out.println(getName() + " cannot run - it has no operator assigned.");
            return;
        }
        serveCycle();
    }

    @Override
    public String toString() {
        return super.toString() + ", Type: Show, Genre: " + genre;
    }
}
