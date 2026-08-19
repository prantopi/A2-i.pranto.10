# A2-i.pranto.10 — Theme Park Attraction and Visitor Management System

PROG2004 Object Oriented Programming — Assessment 2.

A backbone system for a theme park: staff who operate attractions (rides
and shows), visitors who queue for and take part in them, a waiting line
and visit history per attraction, a park-wide registry, save/restore to a
text file, and attractions running concurrently.

There is no user interface. `AssignmentTwo.main()` runs a short, scripted
simulation that exercises every part of the system and prints a log of
everything that happens — the console output is the only window into the
running system, so read it top to bottom to follow along.

## Requirements

- **Java 17 or later** (developed and tested on Java 26; any reasonably
  recent JDK will work — the code uses nothing newer than standard
  `java.util`/`java.io`/`java.util.concurrent` APIs and switch statements).
- No external libraries or build tool required — plain `.java` files, one
  class per file, all in the default (unnamed) package.

## Project layout

```
A2-i.pranto.10/
└── src/
    ├── AssignmentTwo.java        - main() - runs all 8 parts, in order
    ├── Visitor.java              - Part 1
    ├── Staff.java                - Part 1 (+ Part 2 inspections)
    ├── Inspectable.java          - Part 2
    ├── Attraction.java           - Parts 2-5 (abstract base class)
    ├── Ride.java                 - Parts 2 & 5
    ├── Show.java                 - Parts 2 & 5
    ├── Toilet.java                - Part 2
    ├── VisitorNameComparator.java - Part 4 (alternative history ordering)
    ├── Park.java                  - Parts 6 & 8
    ├── ParkIO.java                - Part 7
    └── ParkLoadException.java     - Part 7
```

## How to run it — command line

From the project's `src` directory:

```bash
cd A2-i.pranto.10/src

# compile every file into an "out" folder
javac -d out *.java

# run the simulation
java -cp out AssignmentTwo
```

That's it — the whole demonstration (all 8 parts) runs and prints to the
console. It takes well under a second.

**Note:** Part 7 of the demo writes two small text files into whatever
directory you run `java` from (`park_data.txt` and
`park_data_corrupt.txt`) so it can then read them back. This is expected -
they're the save/restore demonstration in action, not a bug. Both are
listed in `.gitignore` so they won't be committed.

## How to run it — from an IDE (Eclipse / IntelliJ / VS Code)

1. Import the `A2-i.pranto.10` folder as an existing Java project (in
   Eclipse: *File → Import → Existing Projects into Workspace*; in
   IntelliJ: *Open*, then mark `src` as the Sources Root if it isn't
   picked up automatically).
2. Make sure the project's source folder is `src` and its language level
   is Java 17+.
3. Run `AssignmentTwo` (the class with `public static void main`).

## What each part of the output demonstrates

The console output is printed in the same order as the 8 parts of the
assignment, each clearly labelled:

1. **Staff & visitors** — creation, printing full details, sorting
   visitors by age.
2. **Attractions & inspections** — a ride, a show, and a toilet;
   assigning/removing an operator; a staff member inspecting a ride and a
   toilet, showing each is closed for the duration.
3. **Waiting line** — visitors joining a ride's queue in order, the queue
   displayed, the next visitor served (FIFO).
4. **Visit history** — recording visits, checking whether a visitor has
   already been served, the total served, and the history displayed in
   two different orders (by age, and by name-then-age).
5. **Operating an attraction** — a successful ride cycle; a ride
   correctly refusing to run for all three reasons (no operator, nobody
   waiting, closed for inspection); a show running to an empty house and
   then with visitors.
6. **Managing the park** — registering attractions, looking one up by id
   (a hit and a miss), seats served per attraction, and the count of
   distinct visitors across the whole park.
7. **Backup & restore** — saving a park to a text file and loading it
   back into a fresh `Park`, confirming the data matches; then two
   deliberate failure cases (a missing file, and a corrupted file with
   several different kinds of bad lines), each handled gracefully with a
   clear error message instead of crashing.
8. **Concurrency** — two attractions running their cycles at the same
   time on a fixed thread pool, the program waiting for both to finish,
   and a single park-wide visitor total that stays correct despite being
   updated from two threads at once.
