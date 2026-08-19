import java.util.Comparator;

/**
 * An alternative ordering for visitors - by name first, then by age to
 * break ties between visitors who share a name. Visitor's natural ordering
 * (Comparable, by age alone) is used everywhere by default; this comparator
 * is only plugged in where the visit history specifically needs to be
 * viewed a second way (Part 4). Kept as its own class, rather than another
 * method on Visitor, so extra orderings can be added later without ever
 * touching Visitor itself.
 */
public class VisitorNameComparator implements Comparator<Visitor> {

    @Override
    public int compare(Visitor a, Visitor b) {
        int byName = a.getName().compareTo(b.getName());
        if (byName != 0) {
            return byName;
        }
        // same name - age decides the order between them
        return Integer.compare(a.getAge(), b.getAge());
    }
}
