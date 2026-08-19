import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Entry point - builds a small theme park (staff, visitors, attractions)
 * and runs the system end to end, part by part, printing a log of
 * everything that happens along the way since the console output is the
 * only window into this system.
 */
public class AssignmentTwo {

    public static void main(String[] args) {

        // ================= Part 1: the park's people =================
        Staff s1 = new Staff("101", "Jordan Lee", 34, "Operations");
        Staff s2 = new Staff("102", "Amara Diallo", 41, "Safety Inspection");
        Staff s3 = new Staff("103", "Priya Osei", 29, "Guest Services");
        Staff s4 = new Staff("104", "Sam Rivera", 25); // overloaded ctor - defaults to "General" department

        Visitor v1 = new Visitor("201", "Liam Turner", 12);
        Visitor v2 = new Visitor("202", "Grace Kim", 45, "VIP");
        Visitor v3 = new Visitor("203", "Noah Ibrahim", 8);
        Visitor v4 = new Visitor("204", "Ava Petrov", 30);
        Visitor v5 = new Visitor("205", "Ella Novak", 22, "Season Pass");
        Visitor v6 = new Visitor("206", "Mason Reyes", 60);
        Visitor v7 = new Visitor("207", "Zoe Whitfield", 15);
        Visitor v8 = new Visitor("208", "Ben Alvarez", 33);
        Visitor v9 = new Visitor("209", "Mia Chen", 19);
        Visitor v10 = new Visitor("210", "Owen Brooks", 50);

        ArrayList<Staff> staff = new ArrayList<Staff>();
        staff.add(s1);
        staff.add(s2);
        staff.add(s3);
        staff.add(s4);

        ArrayList<Visitor> visitors = new ArrayList<Visitor>();
        visitors.add(v1);
        visitors.add(v2);
        visitors.add(v3);
        visitors.add(v4);
        visitors.add(v5);
        visitors.add(v6);
        visitors.add(v7);
        visitors.add(v8);
        visitors.add(v9);
        visitors.add(v10);

        System.out.println("=== Part 1: Staff ===");
        for (Staff s : staff) {
            System.out.println(s);
        }
        System.out.println();

        System.out.println("=== Part 1: Visitors ===");
        for (Visitor v : visitors) {
            System.out.println(v);
        }
        System.out.println();

        System.out.println("=== Part 1: Visitors ordered by age ===");
        ArrayList<Visitor> byAge = new ArrayList<Visitor>(visitors);
        Collections.sort(byAge); // natural ordering - Visitor implements Comparable<Visitor> by age
        for (Visitor v : byAge) {
            System.out.println(v);
        }
        System.out.println();

        // ================= Part 2: the park's attractions =================
        Ride ride = new Ride("R1", "Thrill Coaster", 3, 120);
        Ride ride2 = new Ride("R2", "Bumper Cars", 4); // overloaded ctor - no minimum height
        Show show = new Show("S1", "Starlight Revue", 5, "Musical");
        Toilet toilet = new Toilet("T1", "Restroom Block A");

        ride.assignOperator(s1);
        show.assignOperator(s3);

        ride2.removeOperator(); // demonstrates the warning branch - there was never an operator to remove
        show.removeOperator();
        show.assignOperator(s3); // reassign, so later parts still have an operator to work with

        System.out.println();
        System.out.println("=== Part 2: Inspections ===");
        s2.performInspection(ride, true, "All safety checks passed");
        s2.performInspection(toilet, false, "Needs restocking - revisit after refill");
        System.out.println();
        System.out.println(ride);
        System.out.println(toilet);
        System.out.println();

        // ================= Part 3: the waiting line =================
        System.out.println("=== Part 3: Waiting line (" + ride.getName() + ") ===");
        ride.joinWaitingLine(v1);
        ride.joinWaitingLine(v2);
        ride.joinWaitingLine(v3);
        ride.joinWaitingLine(v4);
        ride.displayWaitingLine();
        System.out.println();
        ride.admitNextVisitor(); // should be v1 - first to join, first served
        ride.displayWaitingLine();
        System.out.println();

        // ================= Part 4: the visit history =================
        System.out.println("=== Part 4: Visit history (" + ride.getName() + ") ===");
        ride.recordVisit(v5);
        ride.recordVisit(v6);
        ride.recordVisit(v2);
        ride.recordVisit(v7);
        ride.hasBeenServed(v5); // true
        ride.hasBeenServed(v3); // false - never recorded
        System.out.println("Total visitors served so far: " + ride.getVisitCount());
        ride.displayHistory();
        ride.displayHistoryByAge();
        ride.displayHistoryByNameThenAge();
        System.out.println();

        // ================= Part 5: operating an attraction =================
        System.out.println("=== Part 5: Operating an attraction ===");

        System.out.println(ride.getName() + " cycles run before: " + ride.getCyclesRun());
        ride.runCycle(); // succeeds - operator assigned, 3 waiting (v2, v3, v4), not closed
        System.out.println(ride.getName() + " cycles run after: " + ride.getCyclesRun());
        ride.displayWaitingLine();
        ride.displayHistory();
        System.out.println();

        System.out.println("--- " + ride2.getName() + ": three ways a ride can correctly refuse ---");
        ride2.runCycle(); // refuses - no operator
        ride2.assignOperator(s2);
        ride2.runCycle(); // refuses - nobody waiting
        ride2.joinWaitingLine(v8);
        ride2.closeForInspection();
        ride2.runCycle(); // refuses - closed for inspection
        ride2.reopenAfterInspection();
        ride2.runCycle(); // now succeeds
        System.out.println();

        System.out.println("--- " + show.getName() + ": a show goes ahead even to an empty house ---");
        System.out.println(show.getName() + " cycles run before: " + show.getCyclesRun());
        show.runCycle(); // succeeds even though nobody is waiting
        System.out.println(show.getName() + " cycles run after: " + show.getCyclesRun());
        show.joinWaitingLine(v9);
        show.joinWaitingLine(v10);
        show.runCycle(); // succeeds again, this time with visitors
        show.displayHistory();
        System.out.println();

        // ================= Part 6: managing the park =================
        System.out.println("=== Part 6: Managing the park ===");
        Park park = new Park();
        park.registerAttraction(ride);
        park.registerAttraction(ride2);
        park.registerAttraction(show);
        park.findAttraction("S1");  // found
        park.findAttraction("R99"); // not found - warning
        park.reportSeatsServed();
        park.countDistinctVisitors();
        System.out.println();

        // ================= Part 7: backing up and restoring the park =================
        System.out.println("=== Part 7: Backing up and restoring the park ===");

        Ride backupRide = new Ride("BR1", "Sky Swing", 2);
        Show backupShow = new Show("BS1", "Twilight Serenade", 2, "Acoustic");
        backupRide.assignOperator(s1);
        backupShow.assignOperator(s4);
        backupRide.joinWaitingLine(v8);
        backupRide.joinWaitingLine(v9);
        backupRide.recordVisit(v10);
        backupShow.recordVisit(v1);
        backupShow.recordVisit(v2);

        Park sourcePark = new Park();
        sourcePark.registerAttraction(backupRide);
        sourcePark.registerAttraction(backupShow);

        File saveFile = new File("park_data.txt");
        try {
            ParkIO.save(sourcePark, saveFile);
        } catch (IOException e) {
            System.out.println("Save failed: " + e.getMessage());
        }

        try {
            Park loadedPark = ParkIO.load(saveFile);
            System.out.println("--- Confirming the loaded park matches what was saved ---");
            Attraction loadedRide = loadedPark.findAttraction("BR1");
            if (loadedRide != null) {
                loadedRide.displayWaitingLine();
                loadedRide.displayHistory();
            }
            Attraction loadedShow = loadedPark.findAttraction("BS1");
            if (loadedShow != null) {
                loadedShow.displayHistory();
            }
        } catch (ParkLoadException e) {
            System.out.println("Load failed: " + e.getMessage());
        }
        System.out.println();

        System.out.println("--- Loading a missing file ---");
        try {
            ParkIO.load(new File("no_such_park_file.txt"));
        } catch (ParkLoadException e) {
            System.out.println("Expected failure: " + e.getMessage());
        }
        System.out.println();

        System.out.println("--- Loading a corrupted file ---");
        File corruptFile = new File("park_data_corrupt.txt");
        try {
            writeCorruptFile(corruptFile);
            ParkIO.load(corruptFile);
        } catch (IOException e) {
            System.out.println("Could not even write the corrupt demo file: " + e.getMessage());
        } catch (ParkLoadException e) {
            System.out.println("Expected failure: " + e.getMessage());
            for (String problem : e.getProblems()) {
                System.out.println("  - " + problem);
            }
        }
        System.out.println();

        // ================= Part 8: running the park concurrently =================
        System.out.println("=== Part 8: Running the park concurrently ===");

        Park concurrencyPark = new Park();
        Ride carousel = new Ride("R3", "Carousel", 2);
        Show fireworks = new Show("S3", "Fireworks Finale", 3, "Pyrotechnics");
        carousel.assignOperator(s1);
        fireworks.assignOperator(s3);

        carousel.joinWaitingLine(v1);
        carousel.joinWaitingLine(v2);
        carousel.joinWaitingLine(v3);
        carousel.joinWaitingLine(v4);
        carousel.joinWaitingLine(v5);
        carousel.joinWaitingLine(v6);

        fireworks.joinWaitingLine(v7);
        fireworks.joinWaitingLine(v8);
        fireworks.joinWaitingLine(v9);
        fireworks.joinWaitingLine(v10);

        concurrencyPark.registerAttraction(carousel);
        concurrencyPark.registerAttraction(fireworks);

        // a fixed-size pool, rather than a thread created and started by
        // hand for each attraction - the pool accepts each Runnable
        // directly and reuses its threads across the submitted tasks
        ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.submit(() -> runCyclesConcurrently(carousel, 3));
        pool.submit(() -> runCyclesConcurrently(fireworks, 3));

        pool.shutdown(); // stop accepting new tasks
        try {
            pool.awaitTermination(10, TimeUnit.SECONDS); // block until both tasks above have finished
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("All attractions have finished running their concurrent cycles.");
        System.out.println("Park-wide total visitors served concurrently: "
                + concurrencyPark.getTotalVisitorsServedConcurrently());
    }

    // runs a fixed number of cycles on one attraction - used as the body of
    // each pooled task in Part 8, so two (or more) attractions can be
    // mid-cycle on different threads at the same time
    private static void runCyclesConcurrently(Attraction attraction, int cycles) {
        for (int i = 0; i < cycles; i++) {
            attraction.runCycle();
            try {
                Thread.sleep(20); // small pause so the two attractions' cycles interleave, rather than one finishing before the other starts
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // writes a deliberately broken park data file, used to demonstrate that
    // a bad line is reported clearly and does not crash the program, and
    // that a bad reference (a line that itself failed to parse) is caught too
    private static void writeCorruptFile(File file) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
            writer.write("STAFF,999,Ghost Staff"); // too few fields - missing age and department
            writer.newLine();
            writer.write("VISITOR,301,Casey Stone,notanumber,Standard"); // age is not a number
            writer.newLine();
            writer.write("RIDE,BR2,Broken Ride,2,0,100,999,false,Not yet inspected"); // references staff id 999, which never loaded
            writer.newLine();
            writer.write("NONSENSE,abc,def"); // unrecognised tag
            writer.newLine();
            writer.write("WAITING,BR2,301"); // references an attraction and a visitor that never loaded
            writer.newLine();
        }
    }
}
