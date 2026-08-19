import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Saves the park's data to a text file and loads it back. All the file
 * handling lives here, out of Park/Attraction/Visitor/Staff - those
 * classes know what a *valid* park looks like, this class knows what the
 * *file* looks like. Swap this format for something else tomorrow and none
 * of the model classes change; add a field to one of them and only the
 * code here changes.
 *
 * File format - one tagged, comma-separated record per line. Staff and
 * visitors are unique things, so each is written once and referenced
 * elsewhere by id, the same way a customer is referenced by id rather than
 * repeated on every order in the Module 5 shop example. Groups are written
 * in dependency order - staff and visitors first, then attractions (which
 * reference staff by id), then waiting lines and histories (which
 * reference both attractions and visitors by id) - and read back in the
 * same order, so every id a line references already exists by the time
 * that line is read.
 *
 * <pre>
 * STAFF,id,name,age,department
 * VISITOR,id,name,age,ticketType
 * RIDE,id,name,visitorsPerCycle,cyclesRun,minimumHeightCm,operatorId,closedForInspection,inspectionStatus
 * SHOW,id,name,visitorsPerCycle,cyclesRun,genre,operatorId
 * WAITING,attractionId,visitorId;visitorId;...
 * HISTORY,attractionId,visitorId;visitorId;...
 * </pre>
 *
 * operatorId is the literal text NONE when an attraction has no operator.
 * Assumes no name/department/genre/etc. contains a comma or semicolon -
 * handling quoted delimiters properly is outside the scope of this format.
 */
public class ParkIO {

    private static final String NO_OPERATOR = "NONE";

    // utility class - only ever used through its static methods
    private ParkIO() {
    }

    // -----------------------------------------------------------
    // Saving
    // -----------------------------------------------------------

    public static void save(Park park, File file) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {

            // gather every unique staff member / visitor across every
            // attraction first, so each is written exactly once no matter
            // how many attractions reference them
            Map<String, Staff> staffById = new HashMap<String, Staff>();
            Map<String, Visitor> visitorById = new HashMap<String, Visitor>();
            for (Attraction attraction : park.getAllAttractions()) {
                if (attraction.getOperator() != null) {
                    staffById.put(attraction.getOperator().getId(), attraction.getOperator());
                }
                for (Visitor v : attraction.getWaitingSnapshot()) {
                    visitorById.put(v.getId(), v);
                }
                for (Visitor v : attraction.getHistory()) {
                    visitorById.put(v.getId(), v);
                }
            }

            for (Staff s : staffById.values()) {
                writer.write("STAFF," + s.getId() + "," + s.getName() + "," + s.getAge() + "," + s.getDepartment());
                writer.newLine();
            }
            for (Visitor v : visitorById.values()) {
                writer.write("VISITOR," + v.getId() + "," + v.getName() + "," + v.getAge() + "," + v.getTicketType());
                writer.newLine();
            }
            for (Attraction attraction : park.getAllAttractions()) {
                String operatorId = attraction.getOperator() == null ? NO_OPERATOR : attraction.getOperator().getId();
                if (attraction instanceof Ride) {
                    Ride ride = (Ride) attraction;
                    writer.write("RIDE," + ride.getId() + "," + ride.getName() + "," + ride.getVisitorsPerCycle()
                            + "," + ride.getCyclesRun() + "," + ride.getMinimumHeightCm() + "," + operatorId
                            + "," + ride.isClosed() + "," + ride.getInspectionStatus());
                    writer.newLine();
                } else if (attraction instanceof Show) {
                    Show show = (Show) attraction;
                    writer.write("SHOW," + show.getId() + "," + show.getName() + "," + show.getVisitorsPerCycle()
                            + "," + show.getCyclesRun() + "," + show.getGenre() + "," + operatorId);
                    writer.newLine();
                }
                // any future attraction kind not yet supported by this format is simply skipped
            }
            for (Attraction attraction : park.getAllAttractions()) {
                writer.write("WAITING," + attraction.getId() + "," + joinIds(attraction.getWaitingSnapshot()));
                writer.newLine();
            }
            for (Attraction attraction : park.getAllAttractions()) {
                writer.write("HISTORY," + attraction.getId() + "," + joinIds(attraction.getHistory()));
                writer.newLine();
            }
        }
        System.out.println("Park data saved to " + file.getPath() + ".");
    }

    private static String joinIds(List<Visitor> visitors) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < visitors.size(); i++) {
            builder.append(visitors.get(i).getId());
            if (i < visitors.size() - 1) {
                builder.append(";");
            }
        }
        return builder.toString();
    }

    // -----------------------------------------------------------
    // Loading
    // -----------------------------------------------------------

    /**
     * Loads a park back from storage. All-or-nothing: every line is read
     * and validated before anything is returned. If any line is
     * malformed, every problem found is collected (not just the first)
     * and a single ParkLoadException is thrown describing all of them -
     * no partial park is ever handed back, because a "park" missing
     * whatever the broken lines described would not be the park that was
     * saved, it would be a different, smaller one with nothing marking it
     * as such.
     */
    public static Park load(File file) throws ParkLoadException {
        if (!file.exists()) {
            throw new ParkLoadException("Cannot load park data - file does not exist: " + file.getPath());
        }
        if (!file.canRead()) {
            throw new ParkLoadException("Cannot load park data - file cannot be read: " + file.getPath());
        }

        List<String> problems = new ArrayList<String>();
        Map<String, Staff> staffById = new HashMap<String, Staff>();
        Map<String, Visitor> visitorById = new HashMap<String, Visitor>();
        Map<String, Attraction> attractionsById = new HashMap<String, Attraction>();
        List<String[]> waitingRecords = new ArrayList<String[]>();
        List<String[]> historyRecords = new ArrayList<String[]>();

        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().length() == 0) {
                    continue;
                }
                try {
                    parseLine(line, staffById, visitorById, attractionsById, waitingRecords, historyRecords);
                } catch (RuntimeException e) {
                    // a short line, an unknown tag, an unknown id reference, a bad
                    // number - all subclasses of RuntimeException, all collected
                    // here instead of stopping the whole load at the first one
                    problems.add("Line " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new ParkLoadException("Cannot load park data - an error occurred while reading "
                    + file.getPath() + ": " + e.getMessage());
        }

        // WAITING/HISTORY lines can only be applied once every attraction and
        // visitor they reference is known, which is why they're collected
        // above and only wired up now, in a second pass
        applyWaitingRecords(waitingRecords, attractionsById, visitorById, problems);
        applyHistoryRecords(historyRecords, attractionsById, visitorById, problems);

        if (!problems.isEmpty()) {
            throw new ParkLoadException("Park data could not be loaded - " + problems.size()
                    + " problem(s) found in " + file.getPath() + ":", problems);
        }

        // only now, with everything validated, is a park actually built and returned
        Park park = new Park();
        for (Attraction attraction : attractionsById.values()) {
            park.registerAttraction(attraction);
        }
        System.out.println("Park data loaded from " + file.getPath() + ".");
        return park;
    }

    private static void parseLine(String line, Map<String, Staff> staffById, Map<String, Visitor> visitorById,
            Map<String, Attraction> attractionsById, List<String[]> waitingRecords, List<String[]> historyRecords) {

        String[] fields = line.split(",", -1);
        String tag = fields[0];

        switch (tag) {
            case "STAFF": {
                requireFields(fields, 5, tag);
                Staff staff = new Staff(fields[1], fields[2], Integer.parseInt(fields[3]), fields[4]);
                staffById.put(staff.getId(), staff);
                break;
            }
            case "VISITOR": {
                requireFields(fields, 5, tag);
                Visitor visitor = new Visitor(fields[1], fields[2], Integer.parseInt(fields[3]), fields[4]);
                visitorById.put(visitor.getId(), visitor);
                break;
            }
            case "RIDE": {
                requireFields(fields, 9, tag);
                Ride ride = new Ride(fields[1], fields[2], Integer.parseInt(fields[3]), Integer.parseInt(fields[5]));
                ride.restoreCycleCount(Integer.parseInt(fields[4]));
                if (Boolean.parseBoolean(fields[7])) {
                    ride.closeForInspection();
                }
                if (!fields[8].equals("Not yet inspected")) {
                    ride.recordInspectionOutcome(fields[8]);
                }
                attachOperator(ride, fields[6], staffById);
                attractionsById.put(ride.getId(), ride);
                break;
            }
            case "SHOW": {
                requireFields(fields, 7, tag);
                Show show = new Show(fields[1], fields[2], Integer.parseInt(fields[3]), fields[5]);
                show.restoreCycleCount(Integer.parseInt(fields[4]));
                attachOperator(show, fields[6], staffById);
                attractionsById.put(show.getId(), show);
                break;
            }
            case "WAITING":
                requireFields(fields, 2, tag);
                waitingRecords.add(fields);
                break;
            case "HISTORY":
                requireFields(fields, 2, tag);
                historyRecords.add(fields);
                break;
            default:
                throw new IllegalArgumentException("Unrecognised record tag: " + tag);
        }
    }

    private static void attachOperator(Attraction attraction, String operatorId, Map<String, Staff> staffById) {
        if (operatorId.equals(NO_OPERATOR)) {
            return;
        }
        Staff staff = staffById.get(operatorId);
        if (staff == null) {
            throw new IllegalArgumentException(attraction.getId() + " references unknown staff id " + operatorId);
        }
        attraction.assignOperator(staff);
    }

    private static void applyWaitingRecords(List<String[]> waitingRecords, Map<String, Attraction> attractionsById,
            Map<String, Visitor> visitorById, List<String> problems) {
        for (String[] fields : waitingRecords) {
            try {
                Attraction attraction = attractionsById.get(fields[1]);
                if (attraction == null) {
                    throw new IllegalArgumentException("WAITING record refers to unknown attraction id " + fields[1]);
                }
                for (String visitorId : splitIds(fields.length > 2 ? fields[2] : "")) {
                    Visitor visitor = visitorById.get(visitorId);
                    if (visitor == null) {
                        throw new IllegalArgumentException("WAITING record refers to unknown visitor id " + visitorId);
                    }
                    attraction.joinWaitingLine(visitor);
                }
            } catch (RuntimeException e) {
                problems.add("Invalid WAITING record: " + e.getMessage());
            }
        }
    }

    private static void applyHistoryRecords(List<String[]> historyRecords, Map<String, Attraction> attractionsById,
            Map<String, Visitor> visitorById, List<String> problems) {
        for (String[] fields : historyRecords) {
            try {
                Attraction attraction = attractionsById.get(fields[1]);
                if (attraction == null) {
                    throw new IllegalArgumentException("HISTORY record refers to unknown attraction id " + fields[1]);
                }
                for (String visitorId : splitIds(fields.length > 2 ? fields[2] : "")) {
                    Visitor visitor = visitorById.get(visitorId);
                    if (visitor == null) {
                        throw new IllegalArgumentException("HISTORY record refers to unknown visitor id " + visitorId);
                    }
                    attraction.recordVisit(visitor);
                }
            } catch (RuntimeException e) {
                problems.add("Invalid HISTORY record: " + e.getMessage());
            }
        }
    }

    private static void requireFields(String[] fields, int expected, String tag) {
        if (fields.length < expected) {
            throw new IllegalArgumentException(tag + " record has too few fields (expected " + expected
                    + ", found " + fields.length + ")");
        }
    }

    // "a,b,".split(",") silently drops trailing empty fields - split(";", -1)
    // keeps them, and an empty field just means nobody was in that list
    private static List<String> splitIds(String field) {
        List<String> ids = new ArrayList<String>();
        if (field == null || field.trim().length() == 0) {
            return ids;
        }
        for (String part : field.split(";", -1)) {
            if (part.trim().length() > 0) {
                ids.add(part.trim());
            }
        }
        return ids;
    }
}
