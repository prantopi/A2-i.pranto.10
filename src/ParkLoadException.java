import java.util.Collections;
import java.util.List;

/**
 * Thrown when the park's data cannot be loaded from storage - the file is
 * missing, cannot be read, or one or more of its records are malformed.
 *
 * Checked, rather than unchecked: the caller cannot avoid this by checking
 * anything first, since the state of a file on disk (whether it exists,
 * whether another process has it locked, whether it was hand-edited into
 * something invalid) is entirely outside their control. That's the same
 * category of problem as the checked IOException thrown by the underlying
 * file operations.
 */
public class ParkLoadException extends Exception {

    private final List<String> problems;

    public ParkLoadException(String message) {
        super(message);
        this.problems = Collections.emptyList();
    }

    /** @param problems every individual problem found while reading the file, not just the first. */
    public ParkLoadException(String message, List<String> problems) {
        super(message);
        this.problems = Collections.unmodifiableList(problems);
    }

    public List<String> getProblems() {
        return problems;
    }
}
