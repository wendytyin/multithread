The elevator should accept input/output as formated below.

Notation conventions are as follows:

There is one building with F floors, E elevators, and R riders.  Let N be the
maximum capacity of an elevator. Let us also assume that all the elevators
share the same maximum capacity. All the constants are integers starting from
1.

Each elevator contains floor selection buttons for each of the F floors. Each
button will be designated by the letter "E" followed by the elevator number
followed by the letter "B" followed by the desired floor number. For example,
the button in elevator 2 used to choose floor 3 would be designated "E2B3".

The up/down call buttons in the hallways are designated with "U" and "D"
followed by the number of the floor they are on. Note that there is no "D1",
and similarly there is no U for the top floor.

The input can be specified as ASCII text.

The first line contains the four numbers as follows:

(1) F
(2) E
(3) R
(4) N

Each additional line contains triplets of numbers representing an elevator ride
for one person as follows:

(1) rider number
(2) starting floor
(3) destination floor

Each rider thread should be organized as a loop that reads one line from the
input file, processes that request in its entirely, and goes back to read
another input line. All rider threads will share this one input file; you are
responsible for appropriately synchronizing access to it. When the all lines
from the input file have been read and all pending requests have been serviced,
the simulation should terminate gracefully.

Output log file:

Your program should write all its output to a single log file called
Elevator.log. Each significant event in an elevator or rider thread should
produce one line in the log file. Events can be logged in the format shown
below, with each ? replaced by the appropriate integer. Note that since there
is a one-to-one mapping of elevators to elevator threads, it is sufficient to
indicate just the elevator number when logging elevator events. However, since
rider threads do not map to any specific rider numbers, rider events must
indicate both the thread number and the rider number.

Elevator thread events: 
E? on F? opens
E? on F? closes
E? moves up to F?
E? moves down to F?


Rider thread events: 
R? pushes U?
R? pushes D?
R? pushes E?B?
R? enters E? on F?
R? exits E? on F?


Be sure to do the output in a way so that each output line is written
atomically. If you wish to produce additional output lines beyond those in this
specification for debugging, please have all such output lines begin with a #
character.

Sample data and example:

Input file:
4 3 9 4
7 1 3
3 4 2

The first line indicates four floors, three elevators, up to nine total riders,
three rider threads, maximum of four riders per elevator. This is followed by
two requests; rider seven wants to go from the first floor to the third floor,
and rider three wants to go from the fourth floor to the second floor. One
possible output log for the above input:

R7 pushes U1
E1 on F1 opens
R7 enters E1 on F1
R7 pushes E1B3
E1 on F1 closes
E1 moves up to F2
R3 pushes D4
E1 moves up to F3
E1 on F3 opens
R7 exits E1 on F3
E1 on F3 closes
E1 moves down to F2
E1 moves down to F1
E2 moves up to F2
E2 moves up to F3
E2 moves up to F4
E2 on F4 opens
R3 enters E2 on F4
R3 pushes E2B2
E2 on F4 closes
E2 moves down to F3
E2 moves down to F2
E2 on F2 opens
R3 exits E2 on F3
E2 on F2 closes
E2 moves down to F1

Note that there are many possible orderings; anything reasonable and consistent
with physical reality is acceptable.
