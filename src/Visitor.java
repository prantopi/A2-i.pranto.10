/**
 * A visitor to the park - someone who joins waiting lines and takes part
 * in attractions. Kept simple - just enough to identify who they are, how
 * old they are (attractions order and report on this), and what kind of
 * ticket they hold.
 */
public class Visitor implements Comparable<Visitor> {

    // most visitors in this park buy a plain Standard ticket
    private static final String DEFAULT_TICKET_TYPE = "Standard";

    private final String id;
    private final String name;
    private final int age;
    private final String ticketType;

    /** Full constructor - explicit ticket type. */
    public Visitor(String id, String name, int age, String ticketType) {
        if (id == null || id.length() == 0 || !isNumeric(id)) {
            throw new IllegalArgumentException("Visitor id must contain digits only.");
        }
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("Visitor name cannot be empty.");
        }
        if (age <= 0) {
            throw new IllegalArgumentException("Visitor age must be a positive number.");
        }
        if (ticketType == null || ticketType.trim().length() == 0) {
            throw new IllegalArgumentException("Visitor ticket type cannot be empty.");
        }
        this.id = id;
        this.name = name;
        this.age = age;
        this.ticketType = ticketType;
    }

    // overloaded constructor - defaults to a Standard ticket when none is given
    public Visitor(String id, String name, int age) {
        this(id, name, age, DEFAULT_TICKET_TYPE);
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

    public String getTicketType() {
        return ticketType;
    }

    @Override
    public String toString() {
        return "Id: " + id + ", Name: " + name + ", Age: " + age + ", Ticket: " + ticketType;
    }

    // Two visitors are the same person if they share an id - this is what
    // lets an attraction's history recognise a repeat visit (Part 4) and
    // lets the park count a visitor once no matter how many attractions
    // they queued for (Part 6), by relying on a HashSet's use of equals()/hashCode().
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Visitor)) {
            return false;
        }
        Visitor other = (Visitor) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    // Natural ordering - youngest first. This is the ordering Collections.sort(list)
    // uses directly, and what the visit history's "order by age" view (Part 4) relies on.
    @Override
    public int compareTo(Visitor other) {
        return Integer.compare(this.age, other.age);
    }
}
